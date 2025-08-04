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

public class RoomPanel extends JPanel {

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roomTypeFilter;
    private JComboBox<String> statusFilter;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton addRoomButton;

    public RoomPanel() {
        initializeUI();
        createRoomsTableIfNotExists();
        loadRoomData();
    }

    private void initializeUI() {
        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(0, 0, 1456, 1100);

        // Title
        JLabel titleLabel = new JLabel("HOSPITAL ROOMS & SUITES");
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
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter Rooms"));

        JLabel searchLabel = new JLabel("Room Number:");
        searchLabel.setBounds(20, 25, 100, 25);
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(120, 25, 150, 25);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(searchField);

        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setBounds(290, 25, 80, 25);
        typeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(typeLabel);

        roomTypeFilter = new JComboBox<>(new String[]{
                "All Types", "Standard", "Deluxe", "Suite", "ICU", "Emergency", "Operation Theater"
        });
        roomTypeFilter.setBounds(370, 25, 120, 25);
        roomTypeFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(roomTypeFilter);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(510, 25, 50, 25);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchPanel.add(statusLabel);

        statusFilter = new JComboBox<>(new String[]{
                "All Status", "Available", "Occupied", "Maintenance", "Reserved"
        });
        statusFilter.setBounds(560, 25, 100, 25);
        statusFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchPanel.add(statusFilter);

        searchButton = new JButton("Search");
        searchButton.setBounds(680, 25, 80, 25);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(780, 25, 80, 25);
        refreshButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(refreshButton);

        addRoomButton = new JButton("Add New Room");
        addRoomButton.setBounds(880, 25, 120, 25);
        addRoomButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        addRoomButton.setBackground(new Color(255, 193, 7));
        addRoomButton.setForeground(Color.BLACK);
        addRoomButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(addRoomButton);

        add(searchPanel);

        // Create table model
        String[] columnNames = {
                "Room ID", "Room Number", "Room Type", "Floor", "Status",
                "Patient Name", "Daily Rate ($)", "Facilities", "Last Updated"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Tahoma", Font.PLAIN, 11));
        roomTable.setRowHeight(25);
        roomTable.setSelectionBackground(new Color(184, 207, 229));
        roomTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = roomTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 12));
        header.setBackground(new Color(0, 100, 150));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Set column widths
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        roomTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(50, 190, 1350, 450);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(50, 660, 1350, 120);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Room Statistics"));
        add(statsPanel);

        // Add action listeners
        searchButton.addActionListener(e -> searchRooms());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            roomTypeFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            loadRoomData();
        });
        addRoomButton.addActionListener(e -> showAddRoomDialog());

        // Add enter key support for search
        searchField.addActionListener(e -> searchRooms());
    }

    private void createRoomsTableIfNotExists() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS rooms (
                    room_id INT AUTO_INCREMENT PRIMARY KEY,
                    room_number VARCHAR(10) UNIQUE NOT NULL,
                    room_type ENUM('Standard', 'Deluxe', 'Suite', 'ICU', 'Emergency', 'Operation Theater') NOT NULL,
                    floor_number INT NOT NULL,
                    status ENUM('Available', 'Occupied', 'Maintenance', 'Reserved') DEFAULT 'Available',
                    patient_name VARCHAR(100),
                    daily_rate DECIMAL(10,2) NOT NULL,
                    facilities TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """;

            PreparedStatement pstmt = c.connection.prepareStatement(createTableQuery);
            pstmt.executeUpdate();
            pstmt.close();

            // Insert sample data if table is empty
            insertSampleRoomData(c);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleRoomData(DatabaseConnection c) {
        try {
            // Check if data already exists
            PreparedStatement checkStmt = c.connection.prepareStatement("SELECT COUNT(*) FROM rooms");
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                rs.close();
                checkStmt.close();
                return; // Data already exists
            }
            rs.close();
            checkStmt.close();

            // Insert sample room data
            String insertQuery = """
                INSERT INTO rooms (room_number, room_type, floor_number, status, patient_name, daily_rate, facilities) VALUES
                ('101', 'Standard', 1, 'Available', NULL, 150.00, 'Bed, TV, Private Bathroom'),
                ('102', 'Standard', 1, 'Occupied', 'John Smith', 150.00, 'Bed, TV, Private Bathroom'),
                ('201', 'Deluxe', 2, 'Available', NULL, 250.00, 'Bed, TV, Private Bathroom, Mini Fridge'),
                ('202', 'Deluxe', 2, 'Reserved', NULL, 250.00, 'Bed, TV, Private Bathroom, Mini Fridge'),
                ('301', 'Suite', 3, 'Available', NULL, 400.00, 'Bed, TV, Private Bathroom, Mini Fridge, Sofa, Dining Area'),
                ('302', 'Suite', 3, 'Occupied', 'Mary Johnson', 400.00, 'Bed, TV, Private Bathroom, Mini Fridge, Sofa, Dining Area'),
                ('ICU1', 'ICU', 4, 'Available', NULL, 500.00, 'Advanced Life Support, Monitoring Equipment'),
                ('ICU2', 'ICU', 4, 'Occupied', 'Robert Brown', 500.00, 'Advanced Life Support, Monitoring Equipment'),
                ('ER1', 'Emergency', 1, 'Available', NULL, 200.00, 'Emergency Equipment, Crash Cart'),
                ('OT1', 'Operation Theater', 2, 'Maintenance', NULL, 800.00, 'Surgical Equipment, Anesthesia Machine'),
                ('103', 'Standard', 1, 'Available', NULL, 150.00, 'Bed, TV, Private Bathroom'),
                ('203', 'Deluxe', 2, 'Available', NULL, 250.00, 'Bed, TV, Private Bathroom, Mini Fridge'),
                ('303', 'Suite', 3, 'Available', NULL, 400.00, 'Bed, TV, Private Bathroom, Mini Fridge, Sofa, Dining Area'),
                ('ICU3', 'ICU', 4, 'Available', NULL, 500.00, 'Advanced Life Support, Monitoring Equipment'),
                ('OT2', 'Operation Theater', 2, 'Available', NULL, 800.00, 'Surgical Equipment, Anesthesia Machine')
                """;

            PreparedStatement pstmt = c.connection.prepareStatement(insertQuery);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRoomData() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            String query = "SELECT * FROM rooms ORDER BY room_number";
            PreparedStatement pstmt = c.connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int totalRooms = 0;
            int availableRooms = 0;
            int occupiedRooms = 0;
            double totalRevenue = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getInt("floor_number"),
                        rs.getString("status"),
                        rs.getString("patient_name") != null ? rs.getString("patient_name") : "N/A",
                        String.format("$%.2f", rs.getDouble("daily_rate")),
                        rs.getString("facilities"),
                        rs.getTimestamp("updated_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                totalRooms++;
                String status = rs.getString("status");
                if ("Available".equals(status)) availableRooms++;
                if ("Occupied".equals(status)) {
                    occupiedRooms++;
                    totalRevenue += rs.getDouble("daily_rate");
                }
            }

            // Update statistics
            updateStatistics(totalRooms, availableRooms, occupiedRooms, totalRevenue);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading room data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchRooms() {
        try {
            DatabaseConnection c = new DatabaseConnection();

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM rooms WHERE 1=1");

            String roomNumber = searchField.getText().trim();
            String roomType = (String) roomTypeFilter.getSelectedItem();
            String status = (String) statusFilter.getSelectedItem();

            if (!roomNumber.isEmpty()) {
                queryBuilder.append(" AND room_number LIKE ?");
            }
            if (!"All Types".equals(roomType)) {
                queryBuilder.append(" AND room_type = ?");
            }
            if (!"All Status".equals(status)) {
                queryBuilder.append(" AND status = ?");
            }

            queryBuilder.append(" ORDER BY room_number");

            PreparedStatement pstmt = c.connection.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            if (!roomNumber.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + roomNumber + "%");
            }
            if (!"All Types".equals(roomType)) {
                pstmt.setString(paramIndex++, roomType);
            }
            if (!"All Status".equals(status)) {
                pstmt.setString(paramIndex++, status);
            }

            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            int foundRooms = 0;
            int availableRooms = 0;
            int occupiedRooms = 0;
            double totalRevenue = 0;

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getInt("floor_number"),
                        rs.getString("status"),
                        rs.getString("patient_name") != null ? rs.getString("patient_name") : "N/A",
                        String.format("$%.2f", rs.getDouble("daily_rate")),
                        rs.getString("facilities"),
                        rs.getTimestamp("updated_at").toString().substring(0, 16)
                };
                tableModel.addRow(row);

                foundRooms++;
                String roomStatus = rs.getString("status");
                if ("Available".equals(roomStatus)) availableRooms++;
                if ("Occupied".equals(roomStatus)) {
                    occupiedRooms++;
                    totalRevenue += rs.getDouble("daily_rate");
                }
            }

            // Update statistics
            updateStatistics(foundRooms, availableRooms, occupiedRooms, totalRevenue);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching rooms: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Room", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel roomNumLabel = new JLabel("Room Number:");
        roomNumLabel.setBounds(20, 20, 100, 25);
        dialog.add(roomNumLabel);

        JTextField roomNumField = new JTextField();
        roomNumField.setBounds(130, 20, 200, 25);
        dialog.add(roomNumField);

        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setBounds(20, 60, 100, 25);
        dialog.add(typeLabel);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Standard", "Deluxe", "Suite", "ICU", "Emergency", "Operation Theater"
        });
        typeCombo.setBounds(130, 60, 200, 25);
        dialog.add(typeCombo);

        JLabel floorLabel = new JLabel("Floor:");
        floorLabel.setBounds(20, 100, 100, 25);
        dialog.add(floorLabel);

        JTextField floorField = new JTextField();
        floorField.setBounds(130, 100, 200, 25);
        dialog.add(floorField);

        JLabel rateLabel = new JLabel("Daily Rate ($):");
        rateLabel.setBounds(20, 140, 100, 25);
        dialog.add(rateLabel);

        JTextField rateField = new JTextField();
        rateField.setBounds(130, 140, 200, 25);
        dialog.add(rateField);

        JLabel facilitiesLabel = new JLabel("Facilities:");
        facilitiesLabel.setBounds(20, 180, 100, 25);
        dialog.add(facilitiesLabel);

        JTextArea facilitiesArea = new JTextArea();
        facilitiesArea.setBounds(130, 180, 200, 80);
        facilitiesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dialog.add(facilitiesArea);

        JButton saveButton = new JButton("Save Room");
        saveButton.setBounds(130, 280, 100, 30);
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        dialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(240, 280, 90, 30);
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String roomNumber = roomNumField.getText().trim();
                String roomType = (String) typeCombo.getSelectedItem();
                int floor = Integer.parseInt(floorField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                String facilities = facilitiesArea.getText().trim();

                if (roomNumber.isEmpty() || facilities.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseConnection c = new DatabaseConnection();
                String insertQuery = "INSERT INTO rooms (room_number, room_type, floor_number, daily_rate, facilities) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.connection.prepareStatement(insertQuery);
                pstmt.setString(1, roomNumber);
                pstmt.setString(2, roomType);
                pstmt.setInt(3, floor);
                pstmt.setDouble(4, rate);
                pstmt.setString(5, facilities);

                pstmt.executeUpdate();
                pstmt.close();

                JOptionPane.showMessageDialog(dialog, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadRoomData();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for floor and rate!", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding room: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateStatistics(int totalRooms, int availableRooms, int occupiedRooms, double totalRevenue) {
        // Find and update the statistics panel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getBounds().y == 660) {
                JPanel statsPanel = (JPanel) comp;
                statsPanel.removeAll();

                JLabel totalLabel = new JLabel("Total Rooms: " + totalRooms);
                totalLabel.setBounds(50, 30, 150, 25);
                totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                totalLabel.setForeground(new Color(0, 100, 150));
                statsPanel.add(totalLabel);

                JLabel availableLabel = new JLabel("Available: " + availableRooms);
                availableLabel.setBounds(220, 30, 120, 25);
                availableLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                availableLabel.setForeground(new Color(0, 150, 0));
                statsPanel.add(availableLabel);

                JLabel occupiedLabel = new JLabel("Occupied: " + occupiedRooms);
                occupiedLabel.setBounds(360, 30, 120, 25);
                occupiedLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                occupiedLabel.setForeground(new Color(220, 53, 69));
                statsPanel.add(occupiedLabel);

                JLabel revenueLabel = new JLabel(String.format("Daily Revenue: $%.2f", totalRevenue));
                revenueLabel.setBounds(500, 30, 180, 25);
                revenueLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                revenueLabel.setForeground(new Color(255, 140, 0));
                statsPanel.add(revenueLabel);

                double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0;
                JLabel occupancyLabel = new JLabel(String.format("Occupancy Rate: %.1f%%", occupancyRate));
                occupancyLabel.setBounds(700, 30, 160, 25);
                occupancyLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                occupancyLabel.setForeground(new Color(128, 0, 128));
                statsPanel.add(occupancyLabel);

                statsPanel.revalidate();
                statsPanel.repaint();
                break;
            }
        }
    }
}