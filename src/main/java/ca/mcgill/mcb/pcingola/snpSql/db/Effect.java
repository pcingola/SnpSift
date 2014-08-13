package ca.mcgill.mcb.pcingola.snpSql.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import ca.mcgill.mcb.pcingola.vcf.VcfEffect;

@Entity
public class Effect extends Pojo<Effect> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String effect;
	String impact;
	String funClass;
	String codon;
	String aa;
	int aaLen;
	String gene;
	String bioType;
	String coding;
	String transcriptId;
	String exonId;

	@ManyToOne
	Entry entry;

	/**
	 * Load (do it right now, not lazy)
	 *
	 * @param id
	 * @return
	 */
	public static Effect get(long id) {
		return (Effect) DbUtil.getCurrentSession().get(Effect.class, id);
	}

	public Effect() {
		id = null;
	}

	public Effect(VcfEffect veff) {
		id = null;
		effect = veff.getEffectType() != null ? veff.getEffectType().toString() : null;
		impact = veff.getImpact() != null ? veff.getImpact().toString() : null;
		funClass = veff.getFunClass() != null ? veff.getFunClass().toString() : null;
		codon = veff.getCodon();
		aa = veff.getAa();
		aaLen = veff.getAaLen();
		gene = veff.getGene();
		bioType = veff.getBioType();
		coding = veff.getCoding() != null ? veff.getCoding().toString() : null;
		transcriptId = veff.getTranscriptId();
		exonId = veff.getExonId();
	}

	@Override
	public void copySimpleValues(Pojo c) {
		throw new RuntimeException("Unimplemented!");
	}

	public String getAa() {
		return aa;
	}

	public int getAaLen() {
		return aaLen;
	}

	public String getBioType() {
		return bioType;
	}

	public String getCoding() {
		return coding;
	}

	public String getCodon() {
		return codon;
	}

	public String getEffect() {
		return effect;
	}

	public Entry getEntry() {
		return entry;
	}

	public String getExonId() {
		return exonId;
	}

	public String getFunClass() {
		return funClass;
	}

	public String getGene() {
		return gene;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getImpact() {
		return impact;
	}

	public String getTranscriptId() {
		return transcriptId;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public void setAaLen(int aaLen) {
		this.aaLen = aaLen;
	}

	public void setBioType(String bioType) {
		this.bioType = bioType;
	}

	public void setCoding(String coding) {
		this.coding = coding;
	}

	public void setCodon(String codon) {
		this.codon = codon;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public void setExonId(String exonId) {
		this.exonId = exonId;
	}

	public void setFunClass(String funClass) {
		this.funClass = funClass;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setImpact(String impact) {
		this.impact = impact;
	}

	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}

	public void setVcfEntryDb(Entry vcfEntryDb) {
		entry = vcfEntryDb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(effect);
		sb.append("(");

		if (impact != null) sb.append(impact);
		sb.append("|");

		if (funClass != null) sb.append(funClass);
		sb.append("|");

		if (codon != null) sb.append(codon);
		sb.append("|");

		if (aa != null) sb.append(aa);
		sb.append("|");

		if (aaLen > 0) sb.append(aaLen);
		sb.append("|");

		if (gene != null) sb.append(gene);
		sb.append("|");

		if (bioType != null) sb.append(bioType);
		sb.append("|");

		if (coding != null) sb.append(coding);
		sb.append("|");

		if (transcriptId != null) sb.append(transcriptId);
		sb.append("|");

		if (exonId != null) sb.append(exonId);

		sb.append(")");

		return sb.toString();
	}
}
