package com.higgs.wrng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public final class JsonLoader {
    private final File file;

    private final List<String> choices = new LinkedList<>();
    private final List<Integer> weights = new LinkedList<>();

    public JsonLoader(final File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("Invalid file!");
        }
        this.file = file;
    }

    public void load() {
        final StringBuilder sb = new StringBuilder();
        try (final FileReader fr = new FileReader(this.file);
             final BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final JSONArray array = new JSONArray(sb.toString());
        for (int i = 0; i < array.length(); i++) {
            final Object o = array.get(i);
            if (o instanceof JSONObject object) {
                final String choice = object.getString("choice");
                final Integer weight = object.getInt("weight");

                this.choices.add(choice);
                this.weights.add(weight);
            }
        }
    }

    public String[] getChoices() {
        return this.choices.toArray(new String[0]);
    }

    public Integer[] getWeights() {
        return this.weights.toArray(new Integer[0]);
    }
}
