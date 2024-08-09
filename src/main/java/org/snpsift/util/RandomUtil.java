package org.snpsift.util;

import java.util.ArrayList;
import java.util.Random;

import org.snpeff.util.Tuple;
import org.snpsift.annotate.mem.VariantCategory;

public class RandomUtil {

    public static final String[] ACGT = new String[] { "A", "C", "G", "T" };
    public static final int SHORT_VARIANT_LEN = 5;

    double nullRatio = 0.1;
    long seed;
    Random rand;

    public RandomUtil() {
        this(42);
    }

    public RandomUtil(int seed) {
        this.seed = seed;
        rand = new Random(seed);
    }

    public void reset() {
        rand = new Random(seed);
    }

    public double rand() {
        return rand.nextDouble();
    }

    public String randAcgt() {
        return ACGT[rand.nextInt(ACGT.length)];
    }

    public String randBase(String notBase) {
        String base = randAcgt();
        while( base.equals(notBase)) base = randAcgt();
        return base;
    }

    public String randBases(int len) {
        return randBases(len, null);
    }

    public String randBases(int len, String notBases) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String randBase = null;
            if((notBases != null) && (i < notBases.length())) {
                var notBase = notBases.substring(i, i + 1);
                randBase = randBase(notBase);
            } else {
                randBase = randAcgt();
            }
            sb.append(randBase);
        }
        return sb.toString();
    }

    public boolean randBool() {
        return rand.nextDouble() <= 0.5;
    }

    public Boolean randBoolOrNull() {
        return rand.nextDouble() < nullRatio ? null : randBool();
    }

    public char randChar() {
        return (char) (randInt(26) + 'A');
    }

    public Character randCharOrNull() {
        return rand.nextDouble() < nullRatio ? null : randChar();
    }

    public double randDouble(double maxDouble) {
        return rand.nextDouble() * maxDouble;
    }

    public Double randDoubleOrNull() {
        return rand.nextDouble() < nullRatio ? null : rand.nextDouble();
    }

    public String randEnum(ArrayList<String> enumStrings) {
        int ord = randInt(enumStrings.size());
        return enumStrings.get(ord);
    }
    public String randEnumOrNull(ArrayList<String> enumStrings) {
        return rand.nextDouble() < nullRatio ? null : randEnum(enumStrings);
    }

    public int randInt() {
        return rand.nextInt();
    }
    
    public int randInt(int maxInt) {
        return rand.nextInt(maxInt);
    }

    public Integer randIntOrNull() {
        return rand.nextDouble() < nullRatio ? null : rand.nextInt();
    }

    public Integer randIntOrNull(int maxInt) {
        return rand.nextDouble() < nullRatio ? null : randInt(maxInt);
    }

    public long randLong() {
        return rand.nextLong();
    }

    public long randLong(long maxLong) {
        return rand.nextLong() % maxLong;
    }

    public Long randLongOrNull() {
        return rand.nextDouble() < nullRatio ? null : randLong();
    }

    public Long randLongOrNull(long maxLong) {
        return rand.nextDouble() < nullRatio ? null : randLong(maxLong);
    }

    public String randString(int maxLen) {
        // Create a random string (random length), but return null 10% of the time
        var len = rand.nextInt(maxLen);
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(randChar());
        return sb.toString();
    }

    public String randStringOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randString(256);
    }

    public String randStringOrNull(int maxLen) {
        return rand.nextDouble() < nullRatio ? null : randString(maxLen);
    }

    public Tuple<String, String> randVariant(VariantCategory variantCategory) {
        var ref = randVariantRef(variantCategory);
        var alt = randVariantAlt(variantCategory, ref);
        return new Tuple<String, String>(ref, alt);
    }

    public String randVariantAlt(VariantCategory variantCategory, String ref) {
        switch (variantCategory) {
            case SNP_A:
                return "A";
            case SNP_C:
                return "C";
            case SNP_G:
                return "G";
            case SNP_T:
                return "T";
            case MNP:
                return randBases(ref.length(), ref);
            case INS:
                var len = randInt(SHORT_VARIANT_LEN) + 1;
                return randAcgt() + randBases(len);
            case DEL:
                return "";
            case MIXED:
                len = randInt(SHORT_VARIANT_LEN) + 2;
                while( len == ref.length() ) len = randInt(10) + 2;
                return randBases(len, ref);
            default:
                throw new RuntimeException("Unimplemented for variant category " + variantCategory);
        }
    }

    public String randVariantRef(VariantCategory variantCategory) {
        switch (variantCategory) {
            case SNP_A:
                return randBase("A");
            case SNP_C:
                return randBase("C");
            case SNP_G:
                return randBase("G");
            case SNP_T:
                return randBase("T");
            case MNP:
                var len = randInt(SHORT_VARIANT_LEN) + 2;
                return randBases(len);
            case INS:
                return "";
            case DEL:
                len = randInt(SHORT_VARIANT_LEN) + 1;
                return randBases(len);
        case MIXED:
                len = randInt(SHORT_VARIANT_LEN) + 2;
                return randBases(len);
        default:
                throw new RuntimeException("Unimplemented for variant category " + variantCategory);
        }

    }

    public void setNullRatio(double nullRatio) {
        this.nullRatio = nullRatio;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

}