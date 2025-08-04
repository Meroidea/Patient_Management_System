package patient.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class NewPatientForm extends JPanel {

    // Form components
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField insuranceField;
    private JComboBox<String> documentBox;
    private JTextField idnumberField;
    private JComboBox<String> genderBox;
    private JTextField dobDisplayField;
    private JComboBox<String> countryCodeBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressField;
    private JTextArea historyField;
    private JComboBox<String> roomBox;
    private JLabel documentNumberLabel;
    private JTextField patientIdField;

    // NEW: Doctor and timestamp fields
    private JTextField admittingDoctorField;
    private JTextField enrollmentTimeField;
    private String currentDoctorId;
    private String currentDoctorName;

    // Date picker components
    private JDialog datePickerDialog;
    private JComboBox<String> yearCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<String> dayCombo;

    // Email validation pattern
    private Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Constructor now requires logged-in doctor information
    public NewPatientForm(String doctorId) {
        this.currentDoctorId = doctorId;
        this.currentDoctorName = getDoctorNameById(doctorId);

        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(50, 80, 1673, 700);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        createDatePickerDialog();

        // Set current timestamp and doctor info (non-editable)
        setEnrollmentTimeAndDoctor();
    }

    private void initializeComponents() {
        // Initialize all form components
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        insuranceField = new JTextField();
        documentBox = new JComboBox<>(new String[]{"Select Document Type", "Photo ID", "Medicare Card", "Drivers' License", "Passport"});
        idnumberField = new JTextField();
        genderBox = new JComboBox<>(new String[]{"Select gender", "Male", "Female", "Other"});
        dobDisplayField = new JTextField();
        countryCodeBox = new JComboBox<>(new String[]{
                "+61 (Australia)", "+977 (Nepal)", "+974 (Qatar)", "+1 (USA/Canada)", "+44 (UK)", "+91 (India)",
                "+86 (China)", "+81 (Japan)", "+49 (Germany)", "+33 (France)",
                "+39 (Italy)", "+34 (Spain)", "+7 (Russia)", "+55 (Brazil)"
        });
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextArea();
        historyField = new JTextArea();
        roomBox = new JComboBox<>(getAvailableRooms());
        documentNumberLabel = new JLabel();
        patientIdField = new JTextField(generatePatientId());

        // NEW: Initialize doctor and timestamp fields
        admittingDoctorField = new JTextField();
        enrollmentTimeField = new JTextField();

        // Set default selections
        documentBox.setSelectedIndex(0);
        genderBox.setSelectedIndex(0);

        // Configure DOB field
        dobDisplayField.setEditable(false);
        dobDisplayField.setText("Click to select date");
        dobDisplayField.setBackground(Color.WHITE);

        // Configure patient ID field
        patientIdField.setEditable(false);

        // NEW: Configure doctor and timestamp fields (non-editable)
        admittingDoctorField.setEditable(false);
        admittingDoctorField.setBackground(new Color(240, 240, 240));
        admittingDoctorField.setFont(new Font("Tahoma", Font.BOLD, 14));

        enrollmentTimeField.setEditable(false);
        enrollmentTimeField.setBackground(new Color(240, 240, 240));
        enrollmentTimeField.setFont(new Font("Tahoma", Font.BOLD, 14));

        // Configure text areas
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        historyField.setLineWrap(true);
        historyField.setWrapStyleWord(true);
    }

    private void setupLayout() {
        // Title
        JLabel title = new JLabel("📝 Admit New Patient");
        title.setFont(new Font("Tahoma", Font.BOLD, 26));
        title.setBounds(180, 0, 300, 30);
        add(title);

        // Patient ID
        add(label("Patient ID:", 30, 50));
        patientIdField.setBounds(180, 50, 100, 30);
        add(patientIdField);

        // NEW: Admitting Doctor (non-editable)
        add(label("Admitting Doctor:", 400, 50));
        admittingDoctorField.setBounds(550, 50, 250, 30);
        add(admittingDoctorField);

        // NEW: Enrollment Time (non-editable)
        add(label("Enrollment Time:", 850, 50));
        enrollmentTimeField.setBounds(980, 50, 200, 30);
        add(enrollmentTimeField);

        int y = 90;

        // First Name
        add(label("First Name:", 30, y));
        firstNameField.setBounds(180, y, 300, 30);
        add(firstNameField);

        // Last Name
        add(label("Last Name:", 600, y));
        lastNameField.setBounds(720, y, 300, 30);
        add(lastNameField);
        y += 50;

        // Identity Document
        add(label("Identity Document:", 30, y));
        documentBox.setBounds(180, y, 300, 30);
        add(documentBox);

        // Document Number (initially hidden)
        documentNumberLabel.setBounds(540, y, 180, 30);
        documentNumberLabel.setVisible(false);
        add(documentNumberLabel);
        idnumberField.setBounds(720, y, 300, 30);
        idnumberField.setVisible(false);
        add(idnumberField);
        y += 50;

        // Gender
        add(label("Gender:", 30, y));
        genderBox.setBounds(180, y, 100, 30);
        add(genderBox);

        // Phone with country code
        add(label("Phone:", 610, y));
        countryCodeBox.setBounds(720, y, 80, 30);
        add(countryCodeBox);
        phoneField.setBounds(794, y, 230, 30);
        add(phoneField);
        y += 50;

        // DOB with date picker
        add(label("DOB:", 30, y));
        dobDisplayField.setBounds(180, y, 210, 30);
        add(dobDisplayField);
        JButton calendarButton = new JButton("📅");
        calendarButton.setBounds(400, y, 50, 30);
        add(calendarButton);

        // insurance number
        add(label("Insurance Number:", 550, y));
        insuranceField.setBounds(720, y, 300, 30);
        add(insuranceField);
        y += 50;

        // Email
        add(label("Email:", 30, y));
        emailField.setBounds(180, y, 360, 30);
        add(emailField);
        y += 40;

        // Room Number
        add(label("Room Number:", 30, y));
        roomBox.setBounds(180, y, 360, 30);
        add(roomBox);
        y += 40;

        // Address
        add(label("Address:", 30, y));
        JScrollPane addressScroll = new JScrollPane(addressField);
        addressScroll.setBounds(180, y, 360, 50);
        add(addressScroll);
        y += 60;

        // Medical History
        add(label("Medical History:", 30, y));
        JScrollPane historyScroll = new JScrollPane(historyField);
        historyScroll.setBounds(180, y, 360, 60);
        add(historyScroll);
        y += 80;

        // Submit Button
        JButton submit = new JButton("Submit");
        submit.setBounds(180, y, 120, 40);
        styleButton(submit);
        add(submit);

        // Add submit functionality
        submit.addActionListener(e -> {
            if (validateForm()) {
                insertPatientData();
            }
        });

        // Calendar button event
        calendarButton.addActionListener(e -> datePickerDialog.setVisible(true));
    }

    private void setupEventListeners() {
        // Document selection listener
        documentBox.addActionListener(e -> {
            String selectedDocument = (String) documentBox.getSelectedItem();

            if (selectedDocument.equals("Select Document Type")) {
                documentNumberLabel.setVisible(false);
                idnumberField.setVisible(false);
                idnumberField.setText("");
                return;
            }

            String labelText = "";
            switch (selectedDocument) {
                case "Photo ID":
                    labelText = "Photo ID Number:";
                    break;
                case "Medicare Card":
                    labelText = "Medicare Card Number:";
                    break;
                case "Drivers' License":
                    labelText = "Drivers' License Number:";
                    break;
                case "Passport":
                    labelText = "Passport Number:";
                    break;
                default:
                    labelText = "Document Number:";
                    break;
            }

            documentNumberLabel.setText(labelText);
            documentNumberLabel.setVisible(true);
            idnumberField.setVisible(true);
            idnumberField.requestFocus();
        });

        // DOB field click listener
        dobDisplayField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                datePickerDialog.setVisible(true);
            }
        });

        // Phone validation
        phoneField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String phoneNumber = phoneField.getText().trim();
                if (!phoneNumber.isEmpty()) {
                    if (!phoneNumber.matches("^[0-9\\s\\-\\(\\)\\+]{8,15}$")) {
                        phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        showCustomErrorDialog("Invalid phone number format");
                    } else {
                        phoneField.setBorder(UIManager.getBorder("TextField.border"));
                    }
                }
            }
        });

        // Email validation
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String email = emailField.getText().trim();
                if (!email.isEmpty()) {
                    if (!emailPattern.matcher(email).matches()) {
                        emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        showCustomErrorDialog("Invalid email format");
                    } else {
                        emailField.setBorder(UIManager.getBorder("TextField.border"));
                    }
                }
            }
        });
    }

    private void createDatePickerDialog() {
        datePickerDialog = new JDialog();
        datePickerDialog.setUndecorated(true);
        datePickerDialog.setModal(true);
        datePickerDialog.setSize(540, 200);
        datePickerDialog.setLocationRelativeTo(this);
        datePickerDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JPanel datePanel = new JPanel(new GridBagLayout());
        datePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel dateTitle = new JLabel("Select Date of Birth");
        dateTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        datePanel.add(dateTitle, gbc);

        // Year dropdown
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        datePanel.add(new JLabel("Year:"), gbc);
        String[] years = new String[101]; // 1940-2040
        for (int i = 0; i < 101; i++) {
            years[i] = String.valueOf(1940 + i);
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem("2000");
        gbc.gridx = 1;
        datePanel.add(yearCombo, gbc);

        // Month dropdown
        gbc.gridx = 2;
        datePanel.add(new JLabel("Month:"), gbc);
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        gbc.gridx = 3;
        datePanel.add(monthCombo, gbc);

        // Day dropdown
        gbc.gridx = 4;
        datePanel.add(new JLabel("Day:"), gbc);
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i-1] = String.valueOf(i);
        }
        dayCombo = new JComboBox<>(days);
        gbc.gridx = 5;
        datePanel.add(dayCombo, gbc);

        // Update days when month/year changes
        ActionListener updateDays = e -> {
            String selectedMonth = (String) monthCombo.getSelectedItem();
            int year = Integer.parseInt((String) yearCombo.getSelectedItem());
            int month = java.util.Arrays.asList(months).indexOf(selectedMonth) + 1;

            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            dayCombo.removeAllItems();
            for (int i = 1; i <= daysInMonth; i++) {
                dayCombo.addItem(String.valueOf(i));
            }
        };

        yearCombo.addActionListener(updateDays);
        monthCombo.addActionListener(updateDays);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setForeground(Color.BLACK);
        buttonPanel.setBackground(Color.GREEN);
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        styleButton(okButton);
        styleButton(cancelButton);

        okButton.addActionListener(e -> {
            int year = Integer.parseInt((String) yearCombo.getSelectedItem());
            String monthName = (String) monthCombo.getSelectedItem();
            int month = java.util.Arrays.asList(months).indexOf(monthName) + 1;
            int day = Integer.parseInt((String) dayCombo.getSelectedItem());

            dobDisplayField.setText(String.format("%04d-%02d-%02d", year, month, day));
            datePickerDialog.setVisible(false);
        });

        cancelButton.addActionListener(e -> datePickerDialog.setVisible(false));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        datePanel.add(buttonPanel, gbc);

        datePickerDialog.add(datePanel);
    }

    // NEW: Method to set enrollment time and doctor information
    private void setEnrollmentTimeAndDoctor() {
        // Set current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        enrollmentTimeField.setText(now.format(formatter));

        // Set doctor name (non-editable)
        if (currentDoctorName != null && !currentDoctorName.isEmpty()) {
            admittingDoctorField.setText(currentDoctorName);
        } else {
            admittingDoctorField.setText("Dr. " + currentDoctorId);
        }
    }

    // NEW: Method to get doctor name from database
    private String getDoctorNameById(String doctorId) {
        String doctorName = "";
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM doctors WHERE doctor_id = ? OR username = ?";
            PreparedStatement pstmt = db.connection.prepareStatement(query);
            pstmt.setString(1, doctorId);
            pstmt.setString(2, doctorId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                doctorName = "Dr. " + rs.getString("full_name");
            }
            rs.close();
            pstmt.close();
            db.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching doctor name: " + e.getMessage());
        }
        return doctorName;
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder("Please fix the following errors:\n\n");
        boolean isValid = true;

        if (firstNameField.getText().trim().isEmpty()) {
            errors.append("• First Name is required\n");
            isValid = false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            errors.append("• Last Name is required\n");
            isValid = false;
        }

        if (documentBox.getSelectedIndex() == 0) {
            errors.append("• Please select an identity document\n");
            isValid = false;
        }

        if (dobDisplayField.getText().equals("Click to select date")) {
            errors.append("• Please select date of birth\n");
            isValid = false;
        }

        if (genderBox.getSelectedIndex() == 0) {
            errors.append("• Please select gender\n");
            isValid = false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.append("• Phone number is required\n");
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty() || !emailPattern.matcher(emailField.getText().trim()).matches()) {
            errors.append("• Valid email is required\n");
            isValid = false;
        }

        if (!isValid) {
            showCustomValidationDialog(errors.toString());
        }

        return isValid;
    }

    // ENHANCED: Updated database insertion with doctor and timestamp
    private void insertPatientData() {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String sql = "INSERT INTO patients (patient_id, first_name, last_name, gender, dob, phone, email, address, medical_history, room_number, enrollment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = db.connection.prepareStatement(sql);
            pstmt.setString(1, patientIdField.getText());
            pstmt.setString(2, firstNameField.getText().trim());
            pstmt.setString(3, lastNameField.getText().trim());
            pstmt.setString(4, (String) genderBox.getSelectedItem());
            pstmt.setString(5, dobDisplayField.getText());
            pstmt.setString(6, countryCodeBox.getSelectedItem() + " " + phoneField.getText().trim());
            pstmt.setString(7, emailField.getText().trim());
            pstmt.setString(8, addressField.getText().trim());
            pstmt.setString(9, historyField.getText().trim());
            pstmt.setString(10, (String) roomBox.getSelectedItem());
          //  pstmt.setString(11, currentDoctorId); // Doctor ID
          //  pstmt.setString(12, currentDoctorName); // Doctor Name
            pstmt.setTimestamp(13, Timestamp.valueOf(enrollmentTimeField.getText())); // Enrollment timestamp

            int result = pstmt.executeUpdate();
            if (result > 0) {
                // Update room status to occupied
                updateRoomStatus((String) roomBox.getSelectedItem(), "occupied");

                showCustomSuccessDialog("Patient enrolled successfully!\nAdmitting Doctor: " + currentDoctorName + "\nEnrollment Time: " + enrollmentTimeField.getText());
                clearForm();
            }

            pstmt.close();
            db.connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            showCustomErrorDialog("Database Error: " + ex.getMessage());
        }
    }

    // NEW: Method to update room status
    private void updateRoomStatus(String roomNumber, String status) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String sql = "UPDATE rooms SET status = ? WHERE room_number = ?";
            PreparedStatement pstmt = db.connection.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, roomNumber);
            pstmt.executeUpdate();
            pstmt.close();
            db.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        documentBox.setSelectedIndex(0);
        idnumberField.setText("");
        idnumberField.setVisible(false);
        documentNumberLabel.setVisible(false);
        genderBox.setSelectedIndex(0);
        dobDisplayField.setText("Click to select date");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        historyField.setText("");
        insuranceField.setText("");
        patientIdField.setText(generatePatientId());

        // Refresh room list and reset timestamp/doctor info
        roomBox.setModel(new DefaultComboBoxModel<>(getAvailableRooms()));
        setEnrollmentTimeAndDoctor();
    }

    // Custom dialog methods
    private void showCustomErrorDialog(String message) {
        JDialog errorDialog = new JDialog();
        errorDialog.setUndecorated(true);
        errorDialog.setModal(true);
        errorDialog.setSize(350, 150);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        errorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        messagePanel.add(iconLabel);
        messagePanel.add(messageLabel);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.setBackground(new Color(220, 53, 69));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        errorPanel.add(messagePanel, BorderLayout.CENTER);
        errorPanel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.add(errorPanel);
        errorDialog.setVisible(true);
    }

    private void showCustomSuccessDialog(String message) {
        JDialog successDialog = new JDialog();
        successDialog.setUndecorated(true);
        successDialog.setModal(true);
        successDialog.setSize(400, 200);
        successDialog.setLocationRelativeTo(this);
        successDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

        JPanel successPanel = new JPanel(new BorderLayout());
        successPanel.setBackground(Color.WHITE);
        successPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("✅");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
        messageArea.setBackground(Color.WHITE);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messagePanel.add(iconLabel);
        messagePanel.add(messageArea);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.setBackground(new Color(40, 167, 69));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        okButton.addActionListener(e -> successDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        successPanel.add(messagePanel, BorderLayout.CENTER);
        successPanel.add(buttonPanel, BorderLayout.SOUTH);

        successDialog.add(successPanel);
        successDialog.setVisible(true);
    }

    private void showCustomValidationDialog(String message) {
        JDialog validationDialog = new JDialog();
        validationDialog.setUndecorated(true);
        validationDialog.setModal(true);
        validationDialog.setSize(400, 250);
        validationDialog.setLocationRelativeTo(this);
        validationDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        JPanel validationPanel = new JPanel(new BorderLayout());
        validationPanel.setBackground(Color.WHITE);
        validationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Validation Errors");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
        messageArea.setBackground(Color.WHITE);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(350, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.setBackground(new Color(255, 193, 7));
        okButton.setForeground(Color.BLACK);
        okButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        okButton.addActionListener(e -> validationDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        validationPanel.add(titleLabel, BorderLayout.NORTH);
        validationPanel.add(scrollPane, BorderLayout.CENTER);
        validationPanel.add(buttonPanel, BorderLayout.SOUTH);

        validationDialog.add(validationPanel);
        validationDialog.setVisible(true);
    }

    // Helper methods
    private JLabel label(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 150, 30);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
        return lbl;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(246, 215, 118));
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 235, 59));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(246, 215, 118));
            }
        });
    }

    private String generatePatientId() {
        int count = 0;
        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT COUNT(*) AS total FROM patients");
            if (rs.next()) count = rs.getInt("total") + 1;
            rs.close();
            db.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PA" + String.format("%04d", count);
    }

    private String[] getAvailableRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT room_number FROM rooms WHERE status = 'available'");
            while (rs.next()) {
                rooms.add(rs.getString("room_number"));
            }
            rs.close();
            db.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms.toArray(new String[0]);
    }

    // NEW: Static method to create form with doctor session
    public static NewPatientForm createWithDoctorSession(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or empty");
        }
        return new NewPatientForm(doctorId);
    }

    // NEW: Method to refresh doctor information (if doctor session changes)
    public void refreshDoctorSession(String newDoctorId) {
        this.currentDoctorId = newDoctorId;
        this.currentDoctorName = getDoctorNameById(newDoctorId);
        setEnrollmentTimeAndDoctor();
    }

    // NEW: Getter methods for current session info
    public String getCurrentDoctorId() {
        return currentDoctorId;
    }

    public String getCurrentDoctorName() {
        return currentDoctorName;
    }

    public String getEnrollmentTime() {
        return enrollmentTimeField.getText();
    }
}