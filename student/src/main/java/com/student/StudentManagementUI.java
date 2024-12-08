package com.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentManagementUI extends JFrame {
    private JTextField nameField, idField, degreeField, gpaField;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private Database db = new Database();

    public StudentManagementUI() {
        setTitle("Student Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Student Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);

        // ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        inputPanel.add(idField, gbc);

        // Degree
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Degree:"), gbc);

        gbc.gridx = 1;
        degreeField = new JTextField(20);
        inputPanel.add(degreeField, gbc);

        // GPA
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("GPA:"), gbc);

        gbc.gridx = 1;
        gpaField = new JTextField(20);
        inputPanel.add(gpaField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton insertButton = new JButton("Insert");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Course", "GPA" }, 0);
        addData();
        studentTable = new JTable(tableModel);
        studentTable.getColumnModel().getColumn(0).setCellEditor(null);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Action Listeners
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });
    }

    private void addStudent() {
        String name = nameField.getText();
        String degree = degreeField.getText();
        String id = idField.getText();
        String gpa = gpaField.getText();

        if (name.isEmpty() || id.isEmpty() || degree.isEmpty() || gpa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String query = "INSERT INTO student (name, id, course, gpa) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement st = db.getConn().prepareStatement(query);
            st.setString(1, name);
            st.setInt(2, Integer.parseInt(id));
            st.setString(3, degree);
            st.setDouble(4, Double.parseDouble(gpa));

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for ID or GPA.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        tableModel.addRow(new Object[] { id, name, degree, gpa });
        clearFields();
    }

    private void addData() {
        // Retrieve data from the database
        List<Data> list = db.RetreiveData("SELECT * FROM student");
        for (Data student : list) {
            Object[] rowData = new Object[] {
                    student.id,
                    student.name,
                    student.course,
                    student.gpa
            };
            tableModel.addRow(rowData);
        }
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(studentTable.getValueAt(selectedRow, 0).toString());
        String name = studentTable.getValueAt(selectedRow, 1).toString();
        String degree = studentTable.getValueAt(selectedRow, 2).toString();
        Double gpa = Double.parseDouble(studentTable.getValueAt(selectedRow, 3).toString());
        // System.out.println(id + " " + name);

        String query = "UPDATE student SET name = ?, course = ?, gpa = ? WHERE name = ? AND id = ?";

        PreparedStatement st;
        try {
            st = db.getConn().prepareStatement(query);
            st.setString(1, name);
            st.setString(2, degree);
            st.setDouble(3, gpa);

            st.setString(4, name);
            st.setInt(5, id);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No student found to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot edit ID Or DB error", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        clearFields();
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        String id = studentTable.getValueAt(selectedRow, 0).toString();
        String name = studentTable.getValueAt(selectedRow, 1).toString();
        String degree = studentTable.getValueAt(selectedRow, 2).toString();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM student WHERE name = ? AND id = ? AND course = ?";
        PreparedStatement st;
        try {
            st = db.getConn().prepareStatement(query);
            st.setString(1, name);
            st.setInt(2, Integer.parseInt(id));
            st.setString(3, degree);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No student found to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(name + " " + degree + " " + id);

        tableModel.removeRow(selectedRow);
    }

    private void clearFields() {
        nameField.setText("");
        idField.setText("");
        degreeField.setText("");
        gpaField.setText("");
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(() -> {
    // StudentManagementUI ui = new StudentManagementUI();
    // ui.setVisible(true);
    // });
    // }
}
