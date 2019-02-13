package com.bateman.richard.math;

/**
 * Activation functions used to compute the output of a neuron (and limit it to some range) given a set of input.
 * Also some basic functions useful for neural networks.
 */
public class NNMath {

    /**
     * Returns true if the two supplied doubles are equal within a small range
     * (i.e., 0.000000001d)
     *
     * See:
     * >https://en.wikibooks.org/wiki/Artificial_Neural_Networks/Activation_Functions
     * >http://www.cscjournals.org/manuscript/Journals/IJAE/volume1/Issue4/IJAE-26.pdf
     * >http://dontveter.com/bpr/activate.html (I like this one)
     * >https://www.desmos.com/calculator (Nice Graph Calculator)
     * @param d1
     * @param d2
     * @return
     */
    public static boolean doublesEqualWithEpsilon(double d1, double d2) {
        final double EPSILON = 0.000000001d;
        return ((d1 == d2) ? true : Math.abs(d1 - d2) < EPSILON);
    }

    /**
     * A sigmoidal whose output is bounded to [0..1].  Unipolar Sigmoid (Logistic) function.
     * @param input The total input into this function
     * @return The output, bounded between 0 and 1.
     * @apiNote
     * The derivative of this function = f(x) * (1 - f(x))
     *         Sample google sheets formula: =(1/(1 + 2.718281828459^-B1))
     *         Approximate outputs:
     *              -5   >> ~ 0.002
     *              -2   >> ~ 0.119
     *              -1   >> ~ 0.268
     *              -0.5 >> ~ 0.377
     *              0    >> 5
     *              0.5  >> ~ 0.622
     *              1    >> ~ 0.731
     *              2    >> ~ 0.880
     *              5    >> ~ 0.993
     */
    public static double sigmoidal_0_1(double input) {
        return (1.0 / (1.0 + Math.pow(Math.E, -input)));
    }

    /**
     * A sigmoidal whose output is bounded to [-1...1].  Bipolar sigmoid function.
     * @param input The total input into this function
     * @return Output bounded between -1 and +1.
     * @apiNote
     * The derivative of this function = 0.5 * (1 + f(x)) * (1-f(x))
     */
    public static double sigmoidal_1_1(double input) {
        return 2 * sigmoidal_0_1(input) - 1;
        // return (1 - Math.pow(Math.E, -input)) / (1 + Math.pow(Math.E, -input));
    }

    /**
     * The output of tanh ranges from -1 to +1.
     * It is equivalent to (2 / (1 + e^(-2x))) - 1
     * Its deriviative is simply 1 - x^2
     * @param input The total input to the function
     * @return The output, bounded between -1 and +1.
     * @apiNote
     * Approximate outputs (flipped for positive values):
     *           -5   >> ~ -1
     *           -2   >> ~ -0.96
     *           -1   >> ~ -0.76
     *           -0.5 >> ~ -0.46
     *           0    >> 0
     */
    public static double tanh(double input) {
        return Math.tanh(input);
    }

    /**
     * The derivative of tanh.
     * @param input Input to function
     * @return The derivative
     */
    public static double tanhDerivative(double input) {
        return (1 - Math.pow(input, 2));
    }
}
