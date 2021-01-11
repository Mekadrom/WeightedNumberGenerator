package com.higgs.wrng.ui;

import com.higgs.wrng.JsonLoader;
import com.higgs.wrng.JsonSaveBuilder;
import com.higgs.wrng.WeightedRandomNumberGenerator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

public class WRNGFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(540, 600);

    private static final String TITLE = "Weighted Random Number Generator";

    private static final String ADD = "Add";
    private static final String REMOVE = "Remove";
    private static final String CLEAR = "Clear";
    private static final String RANDOMIZE = "Randomize";
    private static final String SAVE = "Save";
    private static final String LOAD = "Load";
    private static final String CLOSE = "Close";
    private static final String NORMALIZE = "Normalize";
    private static final String FILL_LAST = "Fill Last";
    private static final String REDISTRIBUTE = "Redistribute";

    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel();

    public WRNGFrame() {
        super(WRNGFrame.TITLE);

        this.init();
    }

    private void init() {
        final JPanel content = new JPanel(new BorderLayout());

        content.add(this.getTablePanel(), BorderLayout.CENTER);
        content.add(this.getButtonPanel(), BorderLayout.SOUTH);

        this.setContentPane(content);
        this.setSize(WRNGFrame.SIZE);
        this.setResizable(false);
    }

    private JPanel getTablePanel() {
        final JPanel panel = new JPanel(new BorderLayout());

        this.initTable();

        final JScrollPane tableScroller = new JScrollPane();
        tableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        tableScroller.setVerticalScrollBar(tableScroller.createVerticalScrollBar());

        final JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(this.table, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(this.getEditButtonPanel(), BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return panel;
    }

    private JPanel getEditButtonPanel() {
        final JButton normalize = new JButton(WRNGFrame.NORMALIZE);
        final JButton fillLast = new JButton(WRNGFrame.FILL_LAST);
        final JButton redistribute = new JButton(WRNGFrame.REDISTRIBUTE);

        final JButton add = new JButton(WRNGFrame.ADD);
        final JButton remove = new JButton(WRNGFrame.REMOVE);
        final JButton clear = new JButton(WRNGFrame.CLEAR);

        normalize.addActionListener(e -> this.normalize());
        normalize.setToolTipText("Sets the total weight to 100 and scales everything accordingly.");
        fillLast.addActionListener(e -> this.fillLast());
        redistribute.addActionListener(e -> this.redistribute());

        add.addActionListener(e -> this.addRow());
        remove.addActionListener(e -> this.removeRow());
        clear.addActionListener(e -> this.clearRows());

        final JPanel buttonPanel = new JPanel(new BorderLayout());

        final JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.add(normalize);
        leftButtonPanel.add(fillLast);
        leftButtonPanel.add(redistribute);

        final JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.add(add);
        rightButtonPanel.add(remove);
        rightButtonPanel.add(clear);

        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    private void initTable() {
        this.model.setColumnCount(2);
        this.model.setColumnIdentifiers(new String[]{"Choice", "Weight"});
        this.table.setModel(this.model);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.table.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    private JPanel getButtonPanel() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JLabel lastResult = new JLabel(" ");

        final JButton randomize = new JButton(WRNGFrame.RANDOMIZE);
        final JButton save = new JButton(WRNGFrame.SAVE);
        final JButton load = new JButton(WRNGFrame.LOAD);
        final JButton close = new JButton(WRNGFrame.CLOSE);

        randomize.addActionListener(e -> this.showResult(lastResult));
        save.addActionListener(e -> this.save());
        load.addActionListener(e -> this.load());
        close.addActionListener(e -> System.exit(0));

        panel.add(lastResult);
        panel.add(randomize);
        panel.add(save);
        panel.add(load);
        panel.add(close);

        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return panel;
    }

    private void normalize() {
        final Integer[] weights = this.getWeights();
        final Integer[] newWeights = new Integer[weights.length];
        final int prevSum = this.sumWeights();
        final double[] proportions = new double[weights.length];

        for (int i = 0; i < weights.length; i++) {
            proportions[i] = Double.valueOf(weights[i]) / prevSum;
            newWeights[i] = (int) (proportions[i] * 100.0);
        }

        this.setWeights(newWeights);
    }

    private void fillLast() {
        final int sum = this.sumWeights();
        if (sum >= 100) {
            JOptionPane.showMessageDialog(this, "Total is already greater than 100. Try normalizing.");
        } else {
            this.addRow();
            final int selected = this.table.getSelectedRow();
            this.selectLast();
            this.model.setValueAt(100 - sum, this.table.getSelectedRow(), 1);
            this.table.setRowSelectionInterval(selected, selected);
        }
    }

    private void redistribute() {
        final Integer[] weights = this.getWeights();
        final Integer[] newWeights = new Integer[weights.length];

        Arrays.fill(newWeights, this.sumWeights() / weights.length);

        this.setWeights(newWeights);
    }

    private void addRow() {
        this.model.addRow(new String[]{String.format("Choice %s", this.model.getRowCount() + 1), "1"});
        this.selectLast();
    }

    private void removeRow() {
        final int selected = this.table.getSelectedRow();
        if (selected >= 0) {
            this.model.removeRow(selected);
            this.selectLast();
        }
    }

    private void clearRows() {
        IntStream.range(0, this.table.getRowCount()).forEach(i -> this.removeRow());
    }

    private void selectLast() {
        final int rowCount = this.table.getRowCount();
        if (rowCount > 0) {
            this.table.setRowSelectionInterval(this.table.getRowCount() - 1, this.table.getRowCount() - 1);
        }
    }

    private int sumWeights() {
        int sumWeights = 0;
        for (final int weight : this.getWeights()) {
            sumWeights += weight;
        }
        return sumWeights;
    }

    private void showResult(final JLabel resultLabel) {
        final WeightedRandomNumberGenerator wrng = new WeightedRandomNumberGenerator(this.getWeights());
//        JOptionPane.showMessageDialog(this, String.format("Choice is: %s", this.table.getValueAt(wrng.getResult(), 0)));
        resultLabel.setText(String.valueOf(this.table.getValueAt(wrng.getResult(), 0)));
    }

    private void save() {
        final FileDialog fd = new FileDialog(this);
        fd.setDirectory(System.getProperty("user.dir"));
        fd.setFilenameFilter((dir, name) -> name.endsWith("json"));
        fd.setMultipleMode(false);
        fd.setTitle("Save");

        fd.setMode(FileDialog.SAVE);

        fd.setLocationRelativeTo(null);
        fd.setVisible(true);

        final File[] files = fd.getFiles();
        if (files.length > 0) {
            final File file = files[0];
            JsonSaveBuilder.create()
                    .setChoices(this.getChoices())
                    .setWeights(this.getWeights())
                    .save(file);
        }
    }

    private void load() {
        final FileDialog fd = new FileDialog(this);
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

                this.clearRows();

                IntStream.range(0, choices.length).forEach(i -> this.addRow());
                this.setChoices(choices);
                this.setWeights(weights);
            }
        });
    }

    private String[] getChoices() {
        return IntStream.range(0, this.table.getRowCount())
                .mapToObj(i -> this.table.getModel().getValueAt(i, 0))
                .map(String::valueOf)
                .toArray(String[]::new);
    }

    private Integer[] getWeights() {
        return IntStream.range(0, this.table.getRowCount())
                .mapToObj(i -> this.table.getModel().getValueAt(i, 1))
                .map(String::valueOf)
                .map(Integer::valueOf)
                .toArray(Integer[]::new);
    }

    private void setChoices(final String[] choices) {
        if (this.table.getRowCount() < choices.length) {
            throw new IllegalArgumentException("Too many choices for table!");
        }

        this.setValuesInColumn(choices, 0);
    }

    private void setWeights(final Integer[] weights) {
        if (this.table.getRowCount() < weights.length) {
            throw new IllegalArgumentException("Too many weights for table!");
        }

        this.setValuesInColumn(weights, 1);
    }

    private void setValuesInColumn(final Object[] weights, final int column) {
        for (int i = 0; i < weights.length; i++) {
            this.model.setValueAt(weights[i], i, column);
        }
    }
}
