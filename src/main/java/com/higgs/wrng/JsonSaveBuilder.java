package com.higgs.wrng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public final class JsonSaveBuilder {
    public static JsonSaveBuilder create() {
        return new JsonSaveBuilder();
    }

    private String[] choices;
    private Integer[] weights;

    private JsonSaveBuilder() { }

    public JsonSaveBuilder setChoices(final String[] choices) {
        if (choices == null) throw new IllegalArgumentException("Choices cannot be null for saving!");

        if (this.weights != null && choices.length != this.weights.length) {
            throw new IllegalArgumentException("Choices array has to be the same length as weights array!");
        }

        this.choices = choices;
        return this;
    }

    public JsonSaveBuilder setWeights(final Integer[] weights) {
        if (weights == null) throw new IllegalArgumentException("Weights cannot be null for saving!");

        if (this.choices != null && weights.length != this.choices.length) {
            throw new IllegalArgumentException("Weights array has to be the same length as choices array!");
        }

        this.weights = weights;
        return this;
    }

    public void save(final File file) {
        if (this.choices == null || this.weights == null) {
            throw new IllegalArgumentException("Choices or weights is null!");
        }

        if (this.choices.length != this.weights.length) {
            throw new IllegalArgumentException("Different number of choices and weights!");
        }

        final String[] lines = this.buildJson().split("\n");
        try (final FileWriter fw = new FileWriter(file);
             final PrintWriter pw = new PrintWriter(fw)) {
            Arrays.stream(lines).forEach(line -> {
                pw.println(line);
                System.out.println(line);
            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private String buildJson() {
        final int entries = this.choices.length;

        final JSONArray array = new JSONArray();
        for (int i = 0; i < entries; i++) {
            final String choice = this.choices[i];
            final Integer weight = this.weights[i];

            final JSONObject object = new JSONObject();
            object.put("choice", choice);
            object.put("weight", weight);

            array.put(object);
        }
        return array.toString(4);
    }
}
