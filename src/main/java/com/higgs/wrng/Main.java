package com.higgs.wrng;

import com.bulenkov.darcula.DarculaLaf;
import com.higgs.wrng.ui.WRNGFrame;

import javax.swing.*;

public final class Main {
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        final JFrame frame = new WRNGFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setEnabled(true);
        frame.setVisible(true);
    }
}
