package com.higgs.wrng;

import com.higgs.wrng.ui.WRNGFrame;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public final class JsonLoader {
    private final File file;

    private final List<String> choices = new LinkedList<>();
    private final List<Integer> weights = new LinkedList<>();

    public static void load(final WRNGFrame parent) {
        final FileDialog fd = new FileDialog(parent);
        fd.setDirectory(System.getProperty("user.dir"));
        fd.setFilenameFilter((dir, name) -> name.endsWith("json"));
        fd.setMultipleMode(false);
        fd.setTitle("Load");

        fd.setMode(FileDialog.LOAD);

        fd.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            final File[] files = fd.getFiles();
            if (files.length > 0) {
                final File file = files[0];
                final JsonLoader loader = new JsonLoader(file);
                loader.load();

                final String[] choices = loader.getChoices();
                final Integer[] weights = loader.getWeights();

                parent.clearRows();

                IntStream.range(0, choices.length).forEach(i -> parent.addRow());
                parent.setChoices(choices);
                parent.setWeights(weights);
            }
        });
    }

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
