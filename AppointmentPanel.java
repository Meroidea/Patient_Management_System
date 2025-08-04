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

public class AppointmentPanel extends JPanel {

    private String doctorId;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> dateFilter;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton addAppointmentButton;
    private JButton updateStatusButton;

    public AppointmentPanel(String doctorId) {
        this.doctorId = doctorId;
        initializeUI();
        createAppointmentsTableIfNotExists();
        loadAppointmentData();
    }

    private void initializeUI() {
        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(0, 0, 1456, 1100);

        // Title
        JLabel titleLabel = new JLabel("YOUR APPOINTMENTS");
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
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter Appointments"));

        JLabel searchLabel = new JLabel("Patient Name:");
        searchLabel.setBounds(20, 25, 100, 25);
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(120, 25, 150, 25);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(searchField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(290, 25, 50, 25);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(statusLabel);

        statusFilter = new JComboBox<>(new String[]{
                "All Status", "Scheduled", "Completed", "Cancelled", "No-Show"
        });
        statusFilter.setBounds(340, 25, 100, 25);
        statusFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(statusFilter);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(460, 25, 40, 25);
        dateLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(dateLabel);

        dateFilter = new JComboBox<>(new String[]{
                "All Dates", "Today", "Tomorrow", "This Week", "Next Week", "This Month"
        });
        dateFilter.setBounds(500, 25, 100, 25);
        dateFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(dateFilter);

        searchButton = new JButton("Search");
        searchButton.setBounds(620, 25, 80, 25);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(720, 25, 80, 25);
        refreshButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(refreshButton);

        addAppointmentButton = new JButton("New Appointment");
        addAppointmentButton.setBounds(820, 25, 130, 25);
        addAppointmentButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        addAppointmentButton.setBackground(new Color(255, 193, 7));
        addAppointmentButton.setForeground(Color.BLACK);
        addAppointmentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(addAppointmentButton);

        updateStatusButton = new JButton("Update Status");
        updateStatusButton.setBounds(970, 25, 120, 25);
        updateStatusButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        updateStatusButton.setBackground(new Color(108, 117, 125));
        updateStatusButton.setForeground(Color.WHITE);
        updateStatusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(updateStatusButton);

        add(searchPanel);

        // Create table model
        String[] columnNames = {
                "Appointment ID", "Patient Name", "Phone", "Date", "Time",
                "Status", "Type", "Notes", "Created"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Tahoma", Font.PLAIN, 11));
        appointmentTable.setRowHeight(25);
        appointmentTable.setSelectionBackground(new Color(184, 207, 229));
        appointmentTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 12));
        header.setBackground(new Color(0, 100, 150));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Set column widths
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        appointmentTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBounds(50, 190, 1350, 450);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(50, 660, 1350, 100);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Appointment Statistics"));
        add(statsPanel);

        // Add action listeners
        searchButton.addActionListener(e -> searchAppointments());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            dateFilter.setSelectedIndex(0);
            loadAppointmentData();
        });
        addAppointmentButton.addActionListener(e -> showAddAppointmentDialog());
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());

        // Add enter key support for search
        searchField.addActionListener(e -> searchAppointments());
    }

    private void createAppointmentsTableIfNotExists() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS appointments (
                    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
                    doctor_id VARCHAR(10) NOT NULL,
                    patient_name VARCHAR(100) NOT NULL,
                    patient_phone VARCHAR(20),
                    appointment_date DATE NOT NULL,
                    appointment_time TIME NOT NULL,
                    status ENUM('Scheduled', 'Completed', 'Cancelled', 'No-Show') DEFAULT 'Scheduled',
                    appointment_type VARCHAR(50) DEFAULT 'Consultation',
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
                )
                """;

            PreparedStatement pstmt = c.connection.prepareStatement(createTableQuery);
            pstmt.executeUpdate();
            pstmt.close();

            // Insert sample data if table is empty
            insertSampleAppointmentData(c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleAppointmentData(DatabaseConnection c) {
        try {
            // Check if data already exists for this doctor
            PreparedStatement checkStmt = c.connection.prepareStatement("SELECT COUNT(*) FROM appointments WHERE doctor_id = ?");
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

            // Insert sample appointment data
            String insertQuery = """
                INSERT INTO appointments (doctor_id, patient_name, patient_phone, appointment_date, appointment_time, status, appointment_type, notes) VALUES
                (?, 'Alice Wilson', '+61-400-111-001', CURDATE(), '09:00:00', 'Scheduled', 'Consultation', 'Regular checkup'),
                (?, 'Bob Johnson', '+61-400-111-002', CURDATE(), '10:30:00', 'Scheduled', 'Follow-up', 'Blood pressure monitoring'),
                (?, 'Carol Smith', '+61-400-111-003', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', 'Scheduled', 'Consultation', 'Initial examination'),
                (?, 'David Brown', '+61-400-111-004', DATE_ADD(CURDATE(), INTERVAL -1 DAY), '11:00:00', 'Completed', 'Consultation', 'Routine checkup completed'),
                (?, 'Emma Davis', '+61-400-111-005', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:30:00', 'Scheduled', 'Procedure', 'Minor procedure scheduled'),
                (?, 'Frank Miller', '+61-400-111-006', DATE_ADD(CURDATE(), INTERVAL -2 DAY), '09:30:00', 'No-Show', 'Consultation', 'Patient did not show up'),
                (?, 'Grace Lee', '+61-400-111-007', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '16:00:00', 'Scheduled', 'Follow-up', 'Post-treatment checkup')
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

    private void loadAppointmentData() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String query = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date DESC, appointment_time DESC";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int totalAppointments = 0;
            int scheduledCount = 0;
            int completedCount = 0;
            int cancelledCount = 0;
            int noShowCount = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getString("patient_phone"),
                        rs.getDate("appointment_date").toString(),
                        rs.getTime("appointment_time").toString().substring(0, 5),
                        rs.getString("status"),
                        rs.getString("appointment_type"),
                        rs.getString("notes"),
                        rs.getTimestamp("created_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                totalAppointments++;
                String status = rs.getString("status");
                switch (status) {
                    case "Scheduled": scheduledCount++; break;
                    case "Completed": completedCount++; break;
                    case "Cancelled": cancelledCount++; break;
                    case "No-Show": noShowCount++; break;
                }
            }

            // Update statistics
            updateStatistics(totalAppointments, scheduledCount, completedCount, cancelledCount, noShowCount);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading appointment data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAppointments() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM appointments WHERE doctor_id = ?");

            String patientName = searchField.getText().trim();
            String status = (String) statusFilter.getSelectedItem();
            String dateRange = (String) dateFilter.getSelectedItem();

            if (!patientName.isEmpty()) {
                queryBuilder.append(" AND patient_name LIKE ?");
            }
            if (!"All Status".equals(status)) {
                queryBuilder.append(" AND status = ?");
            }

            // Add date filtering
            switch (dateRange) {
                case "Today":
                    queryBuilder.append(" AND appointment_date = CURDATE()");
                    break;
                case "Tomorrow":
                    queryBuilder.append(" AND appointment_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)");
                    break;
                case "This Week":
                    queryBuilder.append(" AND appointment_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)");
                    break;
                case "Next Week":
                    queryBuilder.append(" AND appointment_date BETWEEN DATE_ADD(CURDATE(), INTERVAL 7 DAY) AND DATE_ADD(CURDATE(), INTERVAL 14 DAY)");
                    break;
                case "This Month":
                    queryBuilder.append(" AND MONTH(appointment_date) = MONTH(CURDATE()) AND YEAR(appointment_date) = YEAR(CURDATE())");
                    break;
            }

            queryBuilder.append(" ORDER BY appointment_date DESC, appointment_time DESC");

            PreparedStatement pstmt = c.connection.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            pstmt.setString(paramIndex++, doctorId);

            if (!patientName.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + patientName + "%");
            }
            if (!"All Status".equals(status)) {
                pstmt.setString(paramIndex++, status);
            }

            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int foundAppointments = 0;
            int scheduledCount = 0;
            int completedCount = 0;
            int cancelledCount = 0;
            int noShowCount = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getString("patient_phone"),
                        rs.getDate("appointment_date").toString(),
                        rs.getTime("appointment_time").toString().substring(0, 5),
                        rs.getString("status"),
                        rs.getString("appointment_type"),
                        rs.getString("notes"),
                        rs.getTimestamp("created_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                foundAppointments++;
                String appointmentStatus = rs.getString("status");
                switch (appointmentStatus) {
                    case "Scheduled": scheduledCount++; break;
                    case "Completed": completedCount++; break;
                    case "Cancelled": cancelledCount++; break;
                    case "No-Show": noShowCount++; break;
                }
            }

            // Update statistics
            updateStatistics(foundAppointments, scheduledCount, completedCount, cancelledCount, noShowCount);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching appointments: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Appointment", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        dialog.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(130, 20, 250, 25);
        dialog.add(nameField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(20, 60, 100, 25);
        dialog.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(130, 60, 250, 25);
        dialog.add(phoneField);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(20, 100, 130, 25);
        dialog.add(dateLabel);

        JTextField dateField = new JTextField();
        dateField.setBounds(150, 100, 230, 25);
        // Set default to today
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dialog.add(dateField);

        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeLabel.setBounds(20, 140, 100, 25);
        dialog.add(timeLabel);

        JTextField timeField = new JTextField();
        timeField.setBounds(130, 140, 250, 25);
        timeField.setText("09:00");
        dialog.add(timeField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 180, 100, 25);
        dialog.add(typeLabel);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Consultation", "Follow-up", "Procedure", "Emergency", "Checkup"
        });
        typeCombo.setBounds(130, 180, 250, 25);
        dialog.add(typeCombo);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setBounds(20, 220, 100, 25);
        dialog.add(notesLabel);

        JTextArea notesArea = new JTextArea();
        notesArea.setBounds(130, 220, 250, 80);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(notesArea);

        JButton saveButton = new JButton("Save Appointment");
        saveButton.setBounds(130, 320, 130, 30);
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        dialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(270, 320, 110, 30);
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                String type = (String) typeCombo.getSelectedItem();
                String notes = notesArea.getText().trim();

                if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseConnection c = new DatabaseConnection();
                String insertQuery = "INSERT INTO appointments (doctor_id, patient_name, patient_phone, appointment_date, appointment_time, appointment_type, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.connection.prepareStatement(insertQuery);
                pstmt.setString(1, doctorId);
                pstmt.setString(2, name);
                pstmt.setString(3, phone);
                pstmt.setString(4, date);
                pstmt.setString(5, time);
                pstmt.setString(6, type);
                pstmt.setString(7, notes);

                pstmt.executeUpdate();
                pstmt.close();

                JOptionPane.showMessageDialog(dialog, "Appointment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAppointmentData();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showUpdateStatusDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Appointment Status", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel infoLabel = new JLabel("Appointment for: " + patientName);
        infoLabel.setBounds(20, 20, 300, 25);
        infoLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        dialog.add(infoLabel);

        JLabel currentLabel = new JLabel("Current Status: " + currentStatus);
        currentLabel.setBounds(20, 50, 300, 25);
        dialog.add(currentLabel);

        JLabel newStatusLabel = new JLabel("New Status:");
        newStatusLabel.setBounds(20, 90, 100, 25);
        dialog.add(newStatusLabel);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
                "Scheduled", "Completed", "Cancelled", "No-Show"
        });
        statusCombo.setBounds(130, 90, 200, 25);
        statusCombo.setSelectedItem(currentStatus);
        dialog.add(statusCombo);

        JLabel notesLabel = new JLabel("Update Notes:");
        notesLabel.setBounds(20, 130, 100, 25);
        dialog.add(notesLabel);

        JTextArea notesArea = new JTextArea();
        notesArea.setBounds(130, 130, 200, 60);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(notesArea);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(130, 200, 100, 30);
        updateButton.setBackground(new Color(40, 167, 69));
        updateButton.setForeground(Color.WHITE);
        dialog.add(updateButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(240, 200, 90, 30);
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        dialog.add(cancelButton);

        updateButton.addActionListener(e -> {
            try {
                String newStatus = (String) statusCombo.getSelectedItem();
                String updateNotes = notesArea.getText().trim();

                DatabaseConnection c = new DatabaseConnection();
                String updateQuery = "UPDATE appointments SET status = ?, notes = CONCAT(COALESCE(notes, ''), ?) WHERE appointment_id = ?";
                PreparedStatement pstmt = c.connection.prepareStatement(updateQuery);
                pstmt.setString(1, newStatus);
                pstmt.setString(2, updateNotes.isEmpty() ? "" : "\n[Status Update]: " + updateNotes);
                pstmt.setInt(3, appointmentId);

                int rowsUpdated = pstmt.executeUpdate();
                pstmt.close();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(dialog, "Appointment status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadAppointmentData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update appointment!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateStatistics(int totalAppointments, int scheduledCount, int completedCount, int cancelledCount, int noShowCount) {
        // Find and update the statistics panel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBounds().y == 660) {
                JPanel statsPanel = (JPanel) comp;
                statsPanel.removeAll();

                JLabel totalLabel = new JLabel("Total: " + totalAppointments);
                totalLabel.setBounds(50, 30, 100, 25);
                totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                totalLabel.setForeground(new Color(0, 100, 150));
                statsPanel.add(totalLabel);

                JLabel scheduledLabel = new JLabel("Scheduled: " + scheduledCount);
                scheduledLabel.setBounds(170, 30, 120, 25);
                scheduledLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                scheduledLabel.setForeground(new Color(0, 150, 0));
                statsPanel.add(scheduledLabel);

                JLabel completedLabel = new JLabel("Completed: " + completedCount);
                completedLabel.setBounds(310, 30, 120, 25);
                completedLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                completedLabel.setForeground(new Color(0, 100, 255));
                statsPanel.add(completedLabel);

                JLabel cancelledLabel = new JLabel("Cancelled: " + cancelledCount);
                cancelledLabel.setBounds(450, 30, 120, 25);
                cancelledLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                cancelledLabel.setForeground(new Color(220, 53, 69));
                statsPanel.add(cancelledLabel);

                JLabel noShowLabel = new JLabel("No-Show: " + noShowCount);
                noShowLabel.setBounds(590, 30, 120, 25);
                noShowLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                noShowLabel.setForeground(new Color(255, 140, 0));
                statsPanel.add(noShowLabel);

                // Calculate completion rate
                double completionRate = totalAppointments > 0 ? (double) completedCount / totalAppointments * 100 : 0;
                JLabel completionRateLabel = new JLabel(String.format("Completion Rate: %.1f%%", completionRate));
                completionRateLabel.setBounds(730, 30, 180, 25);
                completionRateLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                completionRateLabel.setForeground(new Color(128, 0, 128));
                statsPanel.add(completionRateLabel);

                // Show today's appointments count
                try {
                    DatabaseConnection c = new DatabaseConnection();
                    PreparedStatement pstmt = c.connection.prepareStatement("SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = CURDATE() AND status = 'Scheduled'");
                    pstmt.setString(1, doctorId);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    int todayCount = rs.getInt(1);
                    rs.close();
                    pstmt.close();

                    JLabel todayLabel = new JLabel("Today's Appointments: " + todayCount);
                    todayLabel.setBounds(930, 30, 180, 25);
                    todayLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                    todayLabel.setForeground(new Color(255, 0, 0));
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