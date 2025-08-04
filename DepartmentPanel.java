package patient.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentPanel extends JPanel {

    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton searchButton;

    public DepartmentPanel() {
        initializeUI();
        loadDepartmentData();
    }

    private void initializeUI() {
        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(0, 0, 1456, 1100);

        // Title
        JLabel titleLabel = new JLabel("HOSPITAL DEPARTMENTS");
        titleLabel.setBounds(500, 30, 400, 40);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 150));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(50, 90, 1350, 60);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Departments"));

        JLabel searchLabel = new JLabel("Search by Department Name:");
        searchLabel.setBounds(20, 25, 200, 25);
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(220, 25, 300, 25);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBounds(540, 25, 100, 25);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(660, 25, 100, 25);
        refreshButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(refreshButton);

        add(searchPanel);

        // Create table model
        String[] columnNames = {
                "Department ID", "Department Name", "Department Head", "Head Specialization",
                "Total Doctors", "Description"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        departmentTable = new JTable(tableModel);
        departmentTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        departmentTable.setRowHeight(25);
        departmentTable.setSelectionBackground(new Color(184, 207, 229));
        departmentTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = departmentTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 14));
        header.setBackground(new Color(0, 100, 150));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Set column widths
        departmentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        departmentTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        departmentTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        departmentTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        departmentTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        departmentTable.getColumnModel().getColumn(5).setPreferredWidth(400);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(departmentTable);
        scrollPane.setBounds(50, 170, 1350, 500);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(50, 690, 1350, 100);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Department Statistics"));

        // Will be populated after loading data
        add(statsPanel);

        // Add action listeners
        searchButton.addActionListener(e -> searchDepartments());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadDepartmentData();
        });

        // Add enter key support for search
        searchField.addActionListener(e -> searchDepartments());
    }

    private void loadDepartmentData() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String query = "SELECT d.department_id, d.department_name, d.description, " +
                    "CONCAT(doc.first_name, ' ', doc.last_name) as head_name, " +
                    "doc.specialization, " +
                    "(SELECT COUNT(*) FROM doctors WHERE department = d.department_name) as doctor_count " +
                    "FROM departments d " +
                    "LEFT JOIN doctors doc ON d.department_head = doc.doctor_id " +
                    "ORDER BY d.department_name";

            PreparedStatement pstmt = c.connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int totalDepartments = 0;
            int totalDoctors = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("head_name") != null ? rs.getString("head_name") : "Not Assigned",
                        rs.getString("specialization") != null ? rs.getString("specialization") : "N/A",
                        rs.getInt("doctor_count"),
                        rs.getString("description")
                };
                tableModel.addRow(row);
                totalDepartments++;
                totalDoctors += rs.getInt("doctor_count");
            }

            // Update statistics
            updateStatistics(totalDepartments, totalDoctors);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading department data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchDepartments() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadDepartmentData();
            return;
        }

        try {
            DatabaseConnection c = new DatabaseConnection();

            String query = "SELECT d.department_id, d.department_name, d.description, " +
                    "CONCAT(doc.first_name, ' ', doc.last_name) as head_name, " +
                    "doc.specialization, " +
                    "(SELECT COUNT(*) FROM doctors WHERE department = d.department_name) as doctor_count " +
                    "FROM departments d " +
                    "LEFT JOIN doctors doc ON d.department_head = doc.doctor_id " +
                    "WHERE d.department_name LIKE ? OR d.description LIKE ? " +
                    "ORDER BY d.department_name";

            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int foundDepartments = 0;
            int totalDoctors = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("head_name") != null ? rs.getString("head_name") : "Not Assigned",
                        rs.getString("specialization") != null ? rs.getString("specialization") : "N/A",
                        rs.getInt("doctor_count"),
                        rs.getString("description")
                };
                tableModel.addRow(row);
                foundDepartments++;
                totalDoctors += rs.getInt("doctor_count");
            }

            // Update statistics
            updateStatistics(foundDepartments, totalDoctors);

            rs.close();
            pstmt.close();

            if (foundDepartments == 0) {
                JOptionPane.showMessageDialog(this,
                        "No departments found matching: " + searchTerm,
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching departments: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatistics(int totalDepartments, int totalDoctors) {
        // Remove existing statistics labels
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBounds().y == 690) {
                JPanel statsPanel = (JPanel) comp;
                statsPanel.removeAll();

                JLabel deptCountLabel = new JLabel("Total Departments: " + totalDepartments);
                deptCountLabel.setBounds(50, 30, 200, 25);
                deptCountLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
                deptCountLabel.setForeground(new Color(0, 100, 150));
                statsPanel.add(deptCountLabel);

                JLabel doctorCountLabel = new JLabel("Total Doctors: " + totalDoctors);
                doctorCountLabel.setBounds(300, 30, 200, 25);
                doctorCountLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
                doctorCountLabel.setForeground(new Color(0, 150, 0));
                statsPanel.add(doctorCountLabel);

                double avgDoctorsPerDept = totalDepartments > 0 ? (double) totalDoctors / totalDepartments : 0;
                JLabel avgLabel = new JLabel(String.format("Average Doctors per Department: %.1f", avgDoctorsPerDept));
                avgLabel.setBounds(550, 30, 300, 25);
                avgLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
                avgLabel.setForeground(new Color(150, 100, 0));
                statsPanel.add(avgLabel);

                statsPanel.revalidate();
                statsPanel.repaint();
                break;
            }
        }
    }
}