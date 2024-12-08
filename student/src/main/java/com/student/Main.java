package com.student;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementUI ui = new StudentManagementUI();
            ui.setVisible(true);
        });
    }
}
