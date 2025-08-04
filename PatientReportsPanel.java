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
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientReportsPanel extends JPanel {

    private String doctorId;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> reportTypeFilter;
    private JComboBox<String> dateFilter;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton addReportButton;
    private JButton viewReportButton;

    public PatientReportsPanel(String doctorId) {
        this.doctorId = doctorId;
        initializeUI();
        createReportsTableIfNotExists();
        loadReportsData();
    }

    private void initializeUI() {
        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(0, 0, 1456, 1100);

        // Title
        JLabel titleLabel = new JLabel("PATIENT REPORTS");
        titleLabel.setBounds(500, 30, 400, 40);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 150));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Search and filter panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(50, 90, 1350, 80);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter Reports"));

        JLabel searchLabel = new JLabel("Patient Name:");
        searchLabel.setBounds(20, 25, 100, 25);
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(120, 25, 150, 25);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(searchField);

        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setBounds(290, 25, 80, 25);
        typeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(typeLabel);

        reportTypeFilter = new JComboBox<>(new String[]{
                "All Types", "Consultation", "Lab Results", "X-Ray", "MRI", "CT Scan", "Blood Test", "Prescription", "Discharge Summary"
        });
        reportTypeFilter.setBounds(370, 25, 120, 25);
        reportTypeFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(reportTypeFilter);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(510, 25, 40, 25);
        dateLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(dateLabel);

        dateFilter = new JComboBox<>(new String[]{
                "All Dates", "Today", "Yesterday", "This Week", "Last Week", "This Month", "Last Month"
        });
        dateFilter.setBounds(550, 25, 100, 25);
        dateFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(dateFilter);

        searchButton = new JButton("Search");
        searchButton.setBounds(670, 25, 80, 25);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(770, 25, 80, 25);
        refreshButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(refreshButton);

        addReportButton = new JButton("New Report");
        addReportButton.setBounds(870, 25, 100, 25);
        addReportButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        addReportButton.setBackground(new Color(255, 193, 7));
        addReportButton.setForeground(Color.BLACK);
        addReportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(addReportButton);

        viewReportButton = new JButton("View Report");
        viewReportButton.setBounds(980, 25, 100, 25);
        viewReportButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        viewReportButton.setBackground(new Color(108, 117, 125));
        viewReportButton.setForeground(Color.WHITE);
        viewReportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(viewReportButton);

        add(searchPanel);

        // Create table model
        String[] columnNames = {
                "Report ID", "Patient Name", "Report Type", "Date", "Diagnosis",
                "Treatment", "Status", "Notes", "Created"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        reportsTable = new JTable(tableModel);
        reportsTable.setFont(new Font("Tahoma", Font.PLAIN, 11));
        reportsTable.setRowHeight(25);
        reportsTable.setSelectionBackground(new Color(184, 207, 229));
        reportsTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = reportsTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 12));
        header.setBackground(new Color(0, 100, 150));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Set column widths
        reportsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        reportsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        reportsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reportsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        reportsTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        reportsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        reportsTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBounds(50, 190, 1350, 450);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(50, 660, 1350, 100);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Report Statistics"));
        add(statsPanel);

        // Add action listeners
        searchButton.addActionListener(e -> searchReports());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            reportTypeFilter.setSelectedIndex(0);
            dateFilter.setSelectedIndex(0);
            loadReportsData();
        });
        addReportButton.addActionListener(e -> showAddReportDialog());
        viewReportButton.addActionListener(e -> showViewReportDialog());

        // Add enter key support for search
        searchField.addActionListener(e -> searchReports());
    }

    private void createReportsTableIfNotExists() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS patient_reports (
                    report_id INT AUTO_INCREMENT PRIMARY KEY,
                    doctor_id VARCHAR(10) NOT NULL,
                    patient_name VARCHAR(100) NOT NULL,
                    report_type VARCHAR(50) NOT NULL,
                    report_date DATE NOT NULL,
                    diagnosis TEXT,
                    treatment TEXT,
                    prescription TEXT,
                    notes TEXT,
                    status ENUM('Draft', 'Completed', 'Reviewed') DEFAULT 'Draft',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
                )
                """;

            PreparedStatement pstmt = c.connection.prepareStatement(createTableQuery);
            pstmt.executeUpdate();
            pstmt.close();

            // Insert sample data if table is empty
            insertSampleReportsData(c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleReportsData(DatabaseConnection c) {
        try {
            // Check if data already exists for this doctor
            PreparedStatement checkStmt = c.connection.prepareStatement("SELECT COUNT(*) FROM patient_reports WHERE doctor_id = ?");
            checkStmt.setString(1, doctorId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                rs.close();
                checkStmt.close();
                return; // Data already exists
            }
            rs.close();
            checkStmt.close();

            // Insert sample reports data
            String insertQuery = """
                INSERT INTO patient_reports (doctor_id, patient_name, report_type, report_date, diagnosis, treatment, prescription, notes, status) VALUES
                (?, 'Alice Wilson', 'Consultation', CURDATE(), 'Hypertension', 'Lifestyle changes, medication', 'Lisinopril 10mg daily', 'Patient advised on diet and exercise', 'Completed'),
                (?, 'Bob Johnson', 'Blood Test', DATE_ADD(CURDATE(), INTERVAL -1 DAY), 'Normal blood count', 'No treatment required', 'None', 'All values within normal range', 'Reviewed'),
                (?, 'Carol Smith', 'X-Ray', DATE_ADD(CURDATE(), INTERVAL -2 DAY), 'Fractured radius', 'Cast application, follow-up in 6 weeks', 'Pain medication as needed', 'Patient fell from bicycle', 'Completed'),
                (?, 'David Brown', 'Consultation', DATE_ADD(CURDATE(), INTERVAL -3 DAY), 'Type 2 Diabetes', 'Diet control, medication', 'Metformin 500mg twice daily', 'Patient counseled on diabetes management', 'Completed'),
                (?, 'Emma Davis', 'Lab Results', DATE_ADD(CURDATE(), INTERVAL -1 DAY), 'Elevated cholesterol', 'Dietary changes, statin therapy', 'Atorvastatin 20mg daily', 'Follow-up in 3 months', 'Draft'),
                (?, 'Frank Miller', 'CT Scan', DATE_ADD(CURDATE(), INTERVAL -5 DAY), 'No abnormalities detected', 'No treatment required', 'None', 'Scan performed due to headaches', 'Reviewed'),
                (?, 'Grace Lee', 'Prescription', CURDATE(), 'Allergic rhinitis', 'Antihistamine therapy', 'Cetirizine 10mg daily', 'Seasonal allergies', 'Completed')
                """;

            PreparedStatement pstmt = c.connection.prepareStatement(insertQuery);
            for (int i = 1; i <= 7; i++) {
                pstmt.setString(i, doctorId);
            }
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReportsData() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String query = "SELECT * FROM patient_reports WHERE doctor_id = ? ORDER BY report_date DESC, created_at DESC";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int totalReports = 0;
            int draftCount = 0;
            int completedCount = 0;
            int reviewedCount = 0;

            while (rs.next()) {
                String treatment = rs.getString("treatment");
                if (treatment != null && treatment.length() > 50) {
                    treatment = treatment.substring(0, 47) + "...";
                }

                Object[] row = {
                        rs.getInt("report_id"),
                        rs.getString("patient_name"),
                        rs.getString("report_type"),
                        rs.getDate("report_date").toString(),
                        rs.getString("diagnosis"),
                        treatment,
                        rs.getString("status"),
                        rs.getString("notes") != null && rs.getString("notes").length() > 30 ?
                                rs.getString("notes").substring(0, 27) + "..." : rs.getString("notes"),
                        rs.getTimestamp("created_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                totalReports++;
                String status = rs.getString("status");
                switch (status) {
                    case "Draft": draftCount++; break;
                    case "Completed": completedCount++; break;
                    case "Reviewed": reviewedCount++; break;
                }
            }

            // Update statistics
            updateStatistics(totalReports, draftCount, completedCount, reviewedCount);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading reports data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchReports() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM patient_reports WHERE doctor_id = ?");

            String patientName = searchField.getText().trim();
            String reportType = (String) reportTypeFilter.getSelectedItem();
            String dateRange = (String) dateFilter.getSelectedItem();

            if (!patientName.isEmpty()) {
                queryBuilder.append(" AND patient_name LIKE ?");
            }
            if (!"All Types".equals(reportType)) {
                queryBuilder.append(" AND report_type = ?");
            }

            // Add date filtering
            switch (dateRange) {
                case "Today":
                    queryBuilder.append(" AND report_date = CURDATE()");
                    break;
                case "Yesterday":
                    queryBuilder.append(" AND report_date = DATE_ADD(CURDATE(), INTERVAL -1 DAY)");
                    break;
                case "This Week":
                    queryBuilder.append(" AND report_date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)");
                    break;
                case "Last Week":
                    queryBuilder.append(" AND report_date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) + 7 DAY) AND report_date < DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)");
                    break;
                case "This Month":
                    queryBuilder.append(" AND MONTH(report_date) = MONTH(CURDATE()) AND YEAR(report_date) = YEAR(CURDATE())");
                    break;
                case "Last Month":
                    queryBuilder.append(" AND MONTH(report_date) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) AND YEAR(report_date) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))");
                    break;
            }

            queryBuilder.append(" ORDER BY report_date DESC, created_at DESC");

            PreparedStatement pstmt = c.connection.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            pstmt.setString(paramIndex++, doctorId);

            if (!patientName.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + patientName + "%");
            }
            if (!"All Types".equals(reportType)) {
                pstmt.setString(paramIndex++, reportType);
            }

            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int foundReports = 0;
            int draftCount = 0;
            int completedCount = 0;
            int reviewedCount = 0;

            while (rs.next()) {
                String treatment = rs.getString("treatment");
                if (treatment != null && treatment.length() > 50) {
                    treatment = treatment.substring(0, 47) + "...";
                }

                Object[] row = {
                        rs.getInt("report_id"),
                        rs.getString("patient_name"),
                        rs.getString("report_type"),
                        rs.getDate("report_date").toString(),
                        rs.getString("diagnosis"),
                        treatment,
                        rs.getString("status"),
                        rs.getString("notes") != null && rs.getString("notes").length() > 30 ?
                                rs.getString("notes").substring(0, 27) + "..." : rs.getString("notes"),
                        rs.getTimestamp("created_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                foundReports++;
                String status = rs.getString("status");
                switch (status) {
                    case "Draft": draftCount++; break;
                    case "Completed": completedCount++; break;
                    case "Reviewed": reviewedCount++; break;
                }
            }

            // Update statistics
            updateStatistics(foundReports, draftCount, completedCount, reviewedCount);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching reports: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Report", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        dialog.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(130, 20, 300, 25);
        dialog.add(nameField);

        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setBounds(20, 60, 100, 25);
        dialog.add(typeLabel);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Consultation", "Lab Results", "X-Ray", "MRI", "CT Scan", "Blood Test", "Prescription", "Discharge Summary"
        });
        typeCombo.setBounds(130, 60, 300, 25);
        dialog.add(typeCombo);

        JLabel dateLabel = new JLabel("Report Date:");
        dateLabel.setBounds(20, 100, 100, 25);
        dialog.add(dateLabel);

        JTextField dateField = new JTextField();
        dateField.setBounds(130, 100, 300, 25);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dialog.add(dateField);

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setBounds(20, 140, 100, 25);
        dialog.add(diagnosisLabel);

        JTextArea diagnosisArea = new JTextArea();
        diagnosisArea.setBounds(130, 140, 300, 60);
        diagnosisArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(diagnosisArea);

        JLabel treatmentLabel = new JLabel("Treatment:");
        treatmentLabel.setBounds(20, 220, 100, 25);
        dialog.add(treatmentLabel);

        JTextArea treatmentArea = new JTextArea();
        treatmentArea.setBounds(130, 220, 300, 60);
        treatmentArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(treatmentArea);

        JLabel prescriptionLabel = new JLabel("Prescription:");
        prescriptionLabel.setBounds(20, 300, 100, 25);
        dialog.add(prescriptionLabel);

        JTextArea prescriptionArea = new JTextArea();
        prescriptionArea.setBounds(130, 300, 300, 60);
        prescriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(prescriptionArea);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setBounds(20, 380, 100, 25);
        dialog.add(notesLabel);

        JTextArea notesArea = new JTextArea();
        notesArea.setBounds(130, 380, 300, 80);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(notesArea);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(20, 480, 100, 25);
        dialog.add(statusLabel);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
                "Draft", "Completed", "Reviewed"
        });
        statusCombo.setBounds(130, 480, 300, 25);
        dialog.add(statusCombo);

        JButton saveButton = new JButton("Save Report");
        saveButton.setBounds(130, 520, 120, 30);
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        dialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(260, 520, 100, 30);
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                String date = dateField.getText().trim();
                String diagnosis = diagnosisArea.getText().trim();
                String treatment = treatmentArea.getText().trim();
                String prescription = prescriptionArea.getText().trim();
                String notes = notesArea.getText().trim();
                String status = (String) statusCombo.getSelectedItem();

                if (name.isEmpty() || diagnosis.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill required fields (Name, Diagnosis)!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseConnection c = new DatabaseConnection();
                String insertQuery = "INSERT INTO patient_reports (doctor_id, patient_name, report_type, report_date, diagnosis, treatment, prescription, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.connection.prepareStatement(insertQuery);
                pstmt.setString(1, doctorId);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setString(4, date);
                pstmt.setString(5, diagnosis);
                pstmt.setString(6, treatment);
                pstmt.setString(7, prescription);
                pstmt.setString(8, notes);
                pstmt.setString(9, status);

                pstmt.executeUpdate();
                pstmt.close();

                JOptionPane.showMessageDialog(dialog, "Report added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadReportsData();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding report: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showViewReportDialog() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to view!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reportId = (Integer) tableModel.getValueAt(selectedRow, 0);

        try {
            DatabaseConnection c = new DatabaseConnection();
            String query = "SELECT * FROM patient_reports WHERE report_id = ?";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setInt(1, reportId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "View Report - " + rs.getString("patient_name"), true);
                dialog.setSize(600, 700);
                dialog.setLocationRelativeTo(this);
                dialog.setLayout(null);

                // Create a scrollable text area for the report
                JTextArea reportArea = new JTextArea();
                reportArea.setEditable(false);
                reportArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
                reportArea.setBackground(Color.WHITE);

                StringBuilder reportContent = new StringBuilder();
                reportContent.append("PATIENT MEDICAL REPORT\n");
                reportContent.append("=".repeat(50)).append("\n\n");
                reportContent.append("Report ID: ").append(rs.getInt("report_id")).append("\n");
                reportContent.append("Patient Name: ").append(rs.getString("patient_name")).append("\n");
                reportContent.append("Report Type: ").append(rs.getString("report_type")).append("\n");
                reportContent.append("Report Date: ").append(rs.getDate("report_date")).append("\n");
                reportContent.append("Status: ").append(rs.getString("status")).append("\n\n");

                reportContent.append("DIAGNOSIS:\n");
                reportContent.append("-".repeat(20)).append("\n");
                reportContent.append(rs.getString("diagnosis") != null ? rs.getString("diagnosis") : "N/A").append("\n\n");

                reportContent.append("TREATMENT:\n");
                reportContent.append("-".repeat(20)).append("\n");
                reportContent.append(rs.getString("treatment") != null ? rs.getString("treatment") : "N/A").append("\n\n");

                reportContent.append("PRESCRIPTION:\n");
                reportContent.append("-".repeat(20)).append("\n");
                reportContent.append(rs.getString("prescription") != null ? rs.getString("prescription") : "N/A").append("\n\n");

                reportContent.append("NOTES:\n");
                reportContent.append("-".repeat(20)).append("\n");
                reportContent.append(rs.getString("notes") != null ? rs.getString("notes") : "N/A").append("\n\n");

                reportContent.append("Created: ").append(rs.getTimestamp("created_at")).append("\n");
                reportContent.append("Last Updated: ").append(rs.getTimestamp("updated_at")).append("\n");

                reportArea.setText(reportContent.toString());

                JScrollPane scrollPane = new JScrollPane(reportArea);
                scrollPane.setBounds(20, 20, 540, 580);
                dialog.add(scrollPane);

                JButton closeButton = new JButton("Close");
                closeButton.setBounds(260, 620, 100, 30);
                closeButton.setBackground(new Color(108, 117, 125));
                closeButton.setForeground(Color.WHITE);
                closeButton.addActionListener(e -> dialog.dispose());
                dialog.add(closeButton);

                dialog.setVisible(true);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatistics(int totalReports, int draftCount, int completedCount, int reviewedCount) {
        // Find and update the statistics panel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBounds().y == 660) {
                JPanel statsPanel = (JPanel) comp;
                statsPanel.removeAll();

                JLabel totalLabel = new JLabel("Total Reports: " + totalReports);
                totalLabel.setBounds(50, 30, 150, 25);
                totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                totalLabel.setForeground(new Color(0, 100, 150));
                statsPanel.add(totalLabel);

                JLabel draftLabel = new JLabel("Draft: " + draftCount);
                draftLabel.setBounds(220, 30, 100, 25);
                draftLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                draftLabel.setForeground(new Color(255, 140, 0));
                statsPanel.add(draftLabel);

                JLabel completedLabel = new JLabel("Completed: " + completedCount);
                completedLabel.setBounds(340, 30, 120, 25);
                completedLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                completedLabel.setForeground(new Color(0, 150, 0));
                statsPanel.add(completedLabel);

                JLabel reviewedLabel = new JLabel("Reviewed: " + reviewedCount);
                reviewedLabel.setBounds(480, 30, 120, 25);
                reviewedLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                reviewedLabel.setForeground(new Color(0, 100, 255));
                statsPanel.add(reviewedLabel);

                // Calculate completion rate
                double completionRate = totalReports > 0 ? (double) (completedCount + reviewedCount) / totalReports * 100 : 0;
                JLabel completionRateLabel = new JLabel(String.format("Completion Rate: %.1f%%", completionRate));
                completionRateLabel.setBounds(620, 30, 180, 25);
                completionRateLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                completionRateLabel.setForeground(new Color(128, 0, 128));
                statsPanel.add(completionRateLabel);

                // Show today's reports count
                try {
                    DatabaseConnection c = new DatabaseConnection();
                    PreparedStatement pstmt = c.connection.prepareStatement("SELECT COUNT(*) FROM patient_reports WHERE doctor_id = ? AND report_date = CURDATE()");
                    pstmt.setString(1, doctorId);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    int todayCount = rs.getInt(1);
                    rs.close();
                    pstmt.close();

                    JLabel todayLabel = new JLabel("Today's Reports: " + todayCount);
                    todayLabel.setBounds(820, 30, 150, 25);
                    todayLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                    todayLabel.setForeground(new Color(220, 53, 69));
                    statsPanel.add(todayLabel);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                statsPanel.revalidate();
                statsPanel.repaint();
                break;
            }
        }
    }
}