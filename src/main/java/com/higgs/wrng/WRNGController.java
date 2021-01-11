package com.higgs.wrng;

import java.util.Arrays;
import java.util.Random;

public class WRNGController {
    private final Random random = new Random(System.currentTimeMillis());

    public Integer[] normalize(final Integer[] weights) {
        final Integer[] newWeights = new Integer[weights.length];
        final int prevSum = this.sumWeights(weights);
        final double[] proportions = new double[weights.length];

        for (int i = 0; i < weights.length; i++) {
            proportions[i] = Double.valueOf(weights[i]) / prevSum;
            newWeights[i] = (int) (proportions[i] * 100.0);
        }

        return newWeights;
    }

    public Integer[] redistribute(final Integer[] weights) {
        final Integer[] newWeights = new Integer[weights.length];

        Arrays.fill(newWeights, this.sumWeights(weights) / weights.length);

        return newWeights;
    }

    public int getWeightedRandomNumber(final Integer[] weights) {
        int sumOfWeights = 0;
        for (final int weight : weights) {
            sumOfWeights += weight;
        }
        int rand = this.random.nextInt(sumOfWeights);
        for (int i = 0; i < weights.length; i++) {
            if (rand < weights[i]) {
                return i;
            }
            rand -= weights[i];
        }
        System.out.println("Something went wrong, defaulting to first entry.");
        return 0; // should never get here
    }

    public int sumWeights(final Integer[] weights) {
        int sumWeights = 0;
        for (final Integer weight : weights) {
            if (weight != null) {
                sumWeights += weight;
            }
        }
        return sumWeights;
    }
}
