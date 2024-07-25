package org.snpsift.util;

import java.util.Random;

public class RandomUtil {

    public static final String[] ACGT = new String[] { "A", "C", "G", "T" };

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

    public boolean randBool() {
        return rand.nextDouble() <= 0.5;
    }

    public Boolean randBoolOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randBool();
    }

    public char randChar() {
        return (char) (randInt(26) + 'A');
    }

    public Character randCharOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        var n = rand.nextInt(1000);
        return randChar();
    }

    public double randDouble(double maxDouble) {
        return rand.nextDouble() * maxDouble;
    }

    public Double randDoubleOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return rand.nextDouble();
    }

    public int randInt() {
        return rand.nextInt();
    }
    
    public int randInt(int maxInt) {
        return rand.nextInt(maxInt);
    }

    public Integer randIntOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return rand.nextInt();
    }

    public Integer randIntOrNull(int maxInt) {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randInt(maxInt);
    }

    public long randLong() {
        return rand.nextLong();
    }

    public long randLong(long maxLong) {
        return rand.nextLong(maxLong);
    }

    public Long randLongOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randLong();
    }

    public Long randLongOrNull(long maxLong) {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randLong(maxLong);
    }

    public String randString() {
        // Create a random string (random length), but return null 10% of the time
        var len = rand.nextInt(10);
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(randChar());
        return sb.toString();
    }

    public String randStringOrNull() {
        if (rand.nextDouble() < nullRatio)
            return null;
        return randString();
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