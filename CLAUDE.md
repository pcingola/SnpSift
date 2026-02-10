# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SnpSift is a collection of tools for variant filtering and annotation, designed to work with VCF (Variant Call Format) files in genomics workflows. It provides 35+ commands for filtering, annotating, transforming, and analyzing genomic variants. Version 5.4, synchronized with SnpEff.

## Build and Development

### Prerequisites
- Java 21
- Maven 3.8+
- SnpEff 5.4 (dependency)

### Building
```bash
# Compile
mvn compile

# Package (creates JAR with dependencies)
mvn clean package assembly:single

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=TestCasesFilter

# Run specific test method
mvn test -Dtest=TestCasesFilter#testFilter01
```

### Running
```bash
# General syntax
java -jar target/SnpSift-5.4-jar-with-dependencies.jar <command> [options] [files]

# Example: Filter VCF
java -jar target/SnpSift-5.4-jar-with-dependencies.jar filter "(QUAL > 30)" input.vcf

# During development (using classes directly)
java -cp target/classes:$SNPEFF_JAR org.snpsift.SnpSift <command> [options]
```

### ANTLR Grammar
The filter expression language is defined in `antlr/SnpSift.g`. To regenerate parser:
```bash
cd antlr
./build.sh
```
This compiles the grammar and copies generated Java files to `src/main/java/org/snpsift/antlr/`.

## Architecture

### Command Dispatch Pattern

Entry point: `org.snpsift.SnpSift.main()` acts as a dispatcher that:
1. Parses generic arguments (`-v`, `-c`, `-d`, `-db`, `-q`)
2. Calls `cmdFactory()` to instantiate the appropriate `SnpSiftCmd*` subclass
3. Delegates execution to the command's `run()` method

All commands extend the base `SnpSift` class and implement the Template Method pattern for VCF processing: `annotateInit()` → iterate entries → `annotate()` → `annotateFinish()`.

### Command Categories

**Filtering/Selection** (`SnpSiftCmdFilter`, `SnpSiftCmdFilterChrPos`, `SnpSiftCmdFilterGt`, `SnpSiftCmdPrivate`): Expression-based and position-based variant filtering.

**Annotation** (`SnpSiftCmdAnnotate`, `SnpSiftCmdDbNsfp`, `SnpSiftCmdVarType`, `SnpSiftCmdGwasCatalog`, `SnpSiftCmdPhastCons`): Add information from databases. `SnpSiftCmdAnnotate` supports three strategies via `AnnotateVcfDb`: MEMORY (entire DB in RAM), SORTED_VCF (indexed files), TABIX (bgzip/tabix).

**Analysis** (`SnpSiftCmdCaseControl`, `SnpSiftCmdConcordance`, `SnpSiftCmdTsTv`, `SnpSiftCmdHwe`): Statistical comparisons and quality metrics.

**Transformation** (`SnpSiftCmdExtractFields`, `SnpSiftCmdVcf2Tped`, `SnpSiftCmdSort`, `SnpSiftCmdSplit`): Convert or reorganize VCF data.

### In-Memory Annotation (`annmem` command)

The `annmem` command (`SnpSiftCmdAnnotateDf`) annotates VCF files using another VCF as a database. It operates in two stages:

**Stage 1: Database Creation** (`-create` flag). Reads a VCF database file and builds a compact, typed columnar representation serialized to disk. The process:
1. Parse VCF headers to extract field types into a `Fields` object.
2. Count variants per chromosome and variant type using `VariantTypeCounters` (also pre-calculates string byte sizes for memory pre-allocation).
3. For each chromosome, create a `VariantDataFrame` containing 9 typed `DataFrame` instances, one per `VariantCategory` (SNP_A, SNP_C, SNP_G, SNP_T, INS, DEL, MNP, MIXED, OTHER).
4. Populate DataFrames by iterating variants in sorted order via `SortedVariantsVcfIterator`.
5. Serialize each chromosome's `VariantDataFrame` to `<chr>.snpsift.df` and field metadata to `fields.snpsift.db_fields`, all under a `<vcf_file>.snpsift.vardb/` directory.

**Stage 2: Annotation** (default mode). Loads the serialized database and annotates input VCF entries:
1. Load `Fields` metadata from disk.
2. For each input VCF entry, load the chromosome's `VariantDataFrame` (lazy-loaded, cached).
3. Determine the variant's category, select the appropriate `DataFrame`, and call `find(pos, ref, alt)` which uses binary search on `PosIndex` followed by linear scan for exact ref/alt match.
4. If found, extract requested field values from the `DataFrameRow` and add them to the VCF INFO field (with optional prefix).

**Key classes in `org.snpsift.annotate.mem`:**
- `VariantDatabase`: Top-level orchestrator, manages per-chromosome `VariantDataFrame` objects and handles disk I/O.
- `VariantDataFrame`: Container for one chromosome's data, holds 9 variant-type-specific `DataFrame` instances.
- `DataFrame` (and subclasses `DataFrameSnp`, `DataFrameIns`, `DataFrameDel`, `DataFrameMnp`, `DataFrameMixed`, `DataFrameOther`): Columnar storage indexed by position via `PosIndex`. Each DataFrame contains typed columns (`DataFrameColumnBool`, `DataFrameColumnInt`, `DataFrameColumnLong`, `DataFrameColumnDouble`, `DataFrameColumnChar`, `DataFrameColumnString`), located in the `dataFrame.dataFrameColumn` subpackage.
- `DataFrameRow`: Represents a single variant, provides access to column values.
- `Fields`: Stores VCF header info and field-to-type mappings. Handles Number=A (one value per ALT allele) and Number=R (one value per allele including REF) field semantics when extracting values from VCF entries.
- `VariantCategory`: Enum that categorizes variants by type and (for SNPs) alternative allele base.
- `VariantTypeCounters` / `VariantTypeCounter`: Pre-count variants per chromosome/type for memory pre-allocation.
- `SortedVariantsVcfIterator`: Priority-queue-based iterator that yields variants in sorted order across chromosomes.

**Array types in `org.snpsift.annotate.mem.arrays`:**
- `PosIndex`: Integer array of chromosome positions with binary search for O(log n) lookups.
- `BoolArray`: Byte-backed boolean array for compact storage.
- `EnumArray`: Stores enumerated strings as byte indices (up to 255 unique values).
- `StringArray` / `StringArrayBase`: Compact string array implementations.

### Key Packages

- `org.snpsift`: Main command implementations (`SnpSiftCmd*` classes)
- `org.snpsift.lang`: Expression language compiler (`LangFactory`) - converts ANTLR AST to `Expression` objects
- `org.snpsift.lang.expression`: Expression tree nodes (binary/unary operators, literals, field access) using Composite pattern
- `org.snpsift.lang.function`: Built-in functions (`countHom`, `isHet`, `isVariant`)
- `org.snpsift.annotate`: Database annotation infrastructure with strategy pattern for different database types
- `org.snpsift.annotate.mem`: In-memory annotation core (`Fields`, `VariantCategory`, `SortedVariantsVcfIterator`)
- `org.snpsift.annotate.mem.arrays`: Compact array types (`PosIndex`, `BoolArray`, `EnumArray`, `StringArray`)
- `org.snpsift.annotate.mem.dataFrame`: DataFrame implementations per variant type and `DataFrameRow`
- `org.snpsift.annotate.mem.dataFrame.dataFrameColumn`: Typed column classes (`DataFrameColumnBool`, `Int`, `Long`, `Double`, `Char`, `String`)
- `org.snpsift.annotate.mem.database`: `VariantDatabase` and `VariantDataFrame` for database lifecycle management
- `org.snpsift.annotate.mem.variantTypeCounter`: Variant counting for memory pre-allocation
- `org.snpsift.fileIterator`: Parsers for dbNSFP, GWAS catalog
- `org.snpsift.caseControl`: Case-control statistical analysis
- `org.snpsift.gwasCatalog`: GWAS catalog data structures
- `org.snpsift.hwe`: Hardy-Weinberg equilibrium calculations
- `org.snpsift.pedigree`: Pedigree file handling
- `org.snpsift.phatsCons`: PhastCons conservation score support
- `org.snpsift.util`: Utilities (`FastaSample`, `FormatUtil`, `RandomUtil`, `ShowProgress`)
- `org.snpsift.antlr`: ANTLR-generated lexer/parser

### VCF Processing Flow

Standard pipeline inherited from `SnpSift` base class:
1. `openVcfInputFile()` returns `VcfFileIterator` (from SnpEff library)
2. `annotateInit()` performs setup (e.g., load database)
3. For each `VcfEntry`: `annotate(vcfEntry)` modifies or filters
4. `annotateFinish()` cleanup

The `annotate()` method either:
- Modifies the entry in-place and returns true (annotation commands)
- Returns boolean to include/exclude entry (filter commands)

### Expression Language

Filter expressions are parsed using ANTLR4 (`antlr/SnpSift.g`). The `LangFactory` class traverses the ANTLR AST and builds an `Expression` tree. Key features:

- Operators: `&`, `|`, `!`, `=`, `!=`, `<`, `>`, `<=`, `>=`
- Array access: `ANN[*].IMPACT`, `GEN[0]`
- Functions: `isHom()`, `isHet()`, `isVariant()`, `countHom()`, `countHet()`, etc.
- Example: `(QUAL > 30) & (ANN[*].IMPACT = 'HIGH')`

`FieldIterator` handles multi-valued fields with "ANY" (at least one match) and "ALL" (all must match) semantics.

### Database Annotation Strategies

`AnnotateVcfDb` selects strategy based on database characteristics:

- **MEMORY** (`DbVcfMem`): Loads entire VCF into `VariantDataFrame` for small databases. O(1) lookups.
- **SORTED_VCF** (`DbVcfSorted`): Uses file position indexing for sorted VCFs. Sequential scan per chromosome.
- **TABIX** (`DbVcfTabix`): Uses bgzip/tabix indices for compressed VCFs. Random access via HTSJDK.

## Testing

Tests use JUnit 5 and are located in `src/test/java/org/snpsift/tests/unit/` (43 test classes). Test data files are in `test/` directory (300+ VCF files for various scenarios, including `test/ann/` for annotation tests).

Test naming convention: `TestCases<Feature>.java` with methods `test<Feature><Number>()`.

`TestSuiteAll.java` (in `src/test/java/org/snpsift/tests/`) runs all tests. Individual test classes can be run via Maven's `-Dtest` parameter.

## Configuration

SnpSift depends on SnpEff's configuration system. The `snpEff.config` file (symlinked from sibling SnpEff project) defines database locations. Override with:
- `-c <config_file>`: Custom config path
- `-dataDir <dir>`: Override data directory
- `-db <database>`: Specify database name

## Encoding

Project uses ISO-8859-1 encoding (specified in pom.xml) for both source files and resources. This is critical for handling certain genomic annotations correctly.

## Dependencies

- **SnpEff 5.4**: Core VCF handling, provides `VcfFileIterator`, `VcfEntry`, `Config`
- **ANTLR4 4.9.3**: Expression parsing
- **HTSJDK 2.24.1**: Tabix/BAM support
- **Trove4j 3.0.2**: Optimized primitive collections for memory efficiency
- **Commons-math3 3.6.1**: Statistical functions
- **JUnit 5**: Testing framework

## SnpEff Sibling Project (`~/workspace/SnpEff/`)

SnpSift depends heavily on classes from the SnpEff library (same version 5.4, Java 21, Maven, ISO-8859-1 encoding). Source is at `src/main/java/org/snpEff/`. Key packages and classes used by SnpSift:

### VCF Processing (`org.snpeff.vcf`)

`VcfEntry` is the central class representing a VCF line with variant and genotype data. It extends `Marker` and handles parsing of all VCF columns (CHROM, POS, REF, ALT, QUAL, FILTER, INFO, FORMAT, genotypes). `VcfFileIterator` opens and iterates VCF files, parsing lines into `VcfEntry` objects and managing header parsing. `VcfHeader` stores INFO/FORMAT field definitions. `VcfHeaderInfo` represents a single INFO field definition with type and description. `VcfEffect` parses the ANN annotation field.

### Configuration (`org.snpeff.snpEffect`)

`Config` is the central configuration class. Loads from `snpEff.config` (Java properties format), manages genome versions, data directories, codon tables, and flags (verbose, debug, quiet, hgvs). Accessible via `Config.get()` singleton.

### Genomic Interval Model (`org.snpeff.interval`)

Hierarchical model: `Genome` contains `Chromosome` objects, which contain `Gene`, `Transcript`, `Exon`, and other feature intervals. `Marker` (base class) represents a genomic interval with an `EffectType`. `Variant` represents a genetic variant with type (SNP, MNP, INS, DEL, BND, INV, DUP), position, and ref/alt alleles. Interval trees (`org.snpeff.interval.tree`) provide fast spatial lookups of overlapping features.

### Effect Prediction (`org.snpeff.snpEffect`)

`SnpEffectPredictor` is the main prediction engine. `VariantEffect` represents the predicted effect of a variant on a gene/transcript, tracking effect type, impact (HIGH/MODERATE/LOW/MODIFIER), codon and amino acid changes, and cDNA/CDS positions. `EffectType` is an enum with 100+ effect types sorted by impact.

### File Iterators (`org.snpeff.fileIterator`)

`FileIterator` is the abstract base for all parsers. Subclasses include `VcfFileIterator`, `BedFileIterator`, `FastaFileIterator`, `Gff3FileIterator`, and 30+ others. All use the iterator pattern for memory-efficient streaming of large files.

### Utilities (`org.snpeff.util`)

`Gpr` provides general-purpose file I/O, string parsing, and math utilities. `Log` handles logging. `GprSeq` provides sequence utilities (reverse complement, IUPAC codes, codon translation).
