package com.higgs.wrng;

import java.util.Random;

public class WeightedRandomNumberGenerator {
    private final Integer[] weights;

    private final Random random = new Random(System.currentTimeMillis());

    public WeightedRandomNumberGenerator(final Integer[] weights) {
        this.weights = weights;
    }

    public int getResult() {
        int sumOfWeights = 0;
        for (final int weight : this.weights) {
            sumOfWeights += weight;
        }
        int rand = this.random.nextInt(sumOfWeights);
        for (int i = 0; i < this.weights.length; i++) {
            if (rand < this.weights[i]) {
                return i;
            }
            rand -= this.weights[i];
        }
        return -1; // should never get here
    }
}
