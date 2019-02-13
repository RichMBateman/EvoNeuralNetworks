package com.bateman.richard.math;

/**
 * Class for random number generation.
 */
public class RNG {

    /**
     * Returns a random number between 0 (inclusive) and 1.0 (exclusive).
     * @return
     */
    public static double rnd() {
        return Math.random();
    }

    /**
     * Returns a random number between 0 (inclusive) and maxExclusive (exclusive)
     * @param maxExclusive The upper limit of the random number range (exclusive)
     * @return
     */
    public static double rnd(double maxExclusive) {
        return Math.random() * maxExclusive;
    }

    /**
     * Returns a random number between min (inclusive) and max(exclusive)
     * @param min
     * @param max
     * @return
     */
    public static double rnd(double min, double max) {
        return min + (rnd() * (max - min));
    }

    /**
     * Returns either -1 or +1 (equal chance of each)
     * @return
     */
    public static int rnd1s() {
        return (rnd() <= 0.5 ? -1 : +1);
    }

    /**
     * Returns a random radian angle (a value from 0 to 2PI, i.e., between 0 and 360 degrees)
     * @return
     */
    public static double rndRadianAngle() {
        return rnd(0, 2 * Math.PI);
    }
}
