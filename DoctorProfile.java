package patient.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class DoctorProfile extends JPanel {

    // Doctor information
    private String doctorId;

    // Non-editable fields (permanent information)
    private JTextField doctorIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField departmentField;
    private JTextField specializationField;
    private JTextField licenseNumberField;
    private JTextField qualificationField;
    private JTextField experienceField;
    private JTextField hireDateField;
    private JTextField usernameField;

    // Editable fields (updatable information)
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> statusComboBox;

    // Buttons
    private JButton updateButton;
    private JButton changePasswordButton;
    private JButton refreshButton;

    // Email validation pattern
    private Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public DoctorProfile(String doctorId) {
        this.doctorId = doctorId;

        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(50, 80, 1673, 700);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadDoctorData();
    }

    private void initializeComponents() {
        // Non-editable fields
        doctorIdField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        departmentField = new JTextField();
        specializationField = new JTextField();
        licenseNumberField = new JTextField();
        qualificationField = new JTextField();
        experienceField = new JTextField();
        hireDateField = new JTextField();
        usernameField = new JTextField();

        // Editable fields
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextArea();
        currentPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        statusComboBox = new JComboBox<>(new String[]{"active", "on_leave"});

        // Buttons
        updateButton = new JButton("Update Profile");
        changePasswordButton = new JButton("Change Password");
        refreshButton = new JButton("Refresh");

        // Configure non-editable fields
        setFieldNonEditable(doctorIdField);
        setFieldNonEditable(firstNameField);
        setFieldNonEditable(lastNameField);
        setFieldNonEditable(departmentField);
        setFieldNonEditable(specializationField);
        setFieldNonEditable(licenseNumberField);
        setFieldNonEditable(qualificationField);
        setFieldNonEditable(experienceField);
        setFieldNonEditable(hireDateField);
        setFieldNonEditable(usernameField);

        // Configure text area
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void setFieldNonEditable(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));
        field.setFont(new Font("Tahoma", Font.BOLD, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void setupLayout() {
        // Title
        JLabel title = new JLabel("👨‍⚕️ Doctor Profile");
        title.setFont(new Font("Tahoma", Font.BOLD, 28));
        title.setBounds(50, 20, 300, 35);
        title.setForeground(new Color(0, 102, 204));
        add(title);

        // Subtitle
        JLabel subtitle = new JLabel("View and update your profile information");
        subtitle.setFont(new Font("Tahoma", Font.ITALIC, 14));
        subtitle.setBounds(50, 55, 400, 20);
        subtitle.setForeground(Color.GRAY);
        add(subtitle);

        int leftX = 50, rightX = 600;
        int y = 100;

        // Left Column - Personal Information
        JLabel personalInfoTitle = new JLabel("📋 Personal Information");
        personalInfoTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        personalInfoTitle.setBounds(leftX, y, 300, 25);
        personalInfoTitle.setForeground(new Color(0, 102, 204));
        add(personalInfoTitle);
        y += 40;

        // Doctor ID (non-editable)
        add(createLabel("Doctor ID:", leftX, y));
        doctorIdField.setBounds(leftX + 150, y, 200, 30);
        add(doctorIdField);
        y += 40;

        // First Name (non-editable)
        add(createLabel("First Name:", leftX, y));
        firstNameField.setBounds(leftX + 150, y, 200, 30);
        add(firstNameField);
        y += 40;

        // Last Name (non-editable)
        add(createLabel("Last Name:", leftX, y));
        lastNameField.setBounds(leftX + 150, y, 200, 30);
        add(lastNameField);
        y += 40;

        // Username (non-editable)
        add(createLabel("Username:", leftX, y));
        usernameField.setBounds(leftX + 150, y, 200, 30);
        add(usernameField);
        y += 40;

        // Email (editable)
        add(createLabel("Email:", leftX, y));
        emailField.setBounds(leftX + 150, y, 300, 30);
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(emailField);
        y += 40;

        // Phone (editable)
        add(createLabel("Phone:", leftX, y));
        phoneField.setBounds(leftX + 150, y, 200, 30);
        phoneField.setBackground(Color.WHITE);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(phoneField);
        y += 40;

        // Address (editable)
        add(createLabel("Address:", leftX, y));
        JScrollPane addressScroll = new JScrollPane(addressField);
        addressScroll.setBounds(leftX + 150, y, 300, 60);
        addressField.setBackground(Color.WHITE);
        add(addressScroll);
        y += 80;

        // Status (editable for doctors to request leave)
        add(createLabel("Status:", leftX, y));
        statusComboBox.setBounds(leftX + 150, y, 150, 30);
        statusComboBox.setBackground(Color.WHITE);
        add(statusComboBox);

        // Right Column - Professional Information
        y = 100;
        JLabel professionalInfoTitle = new JLabel("🏥 Professional Information");
        professionalInfoTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        professionalInfoTitle.setBounds(rightX, y, 300, 25);
        professionalInfoTitle.setForeground(new Color(0, 102, 204));
        add(professionalInfoTitle);
        y += 40;

        // Department (non-editable)
        add(createLabel("Department:", rightX, y));
        departmentField.setBounds(rightX + 150, y, 250, 30);
        add(departmentField);
        y += 40;

        // Specialization (non-editable)
        add(createLabel("Specialization:", rightX, y));
        specializationField.setBounds(rightX + 150, y, 300, 30);
        add(specializationField);
        y += 40;

        // License Number (non-editable)
        add(createLabel("License Number:", rightX, y));
        licenseNumberField.setBounds(rightX + 150, y, 200, 30);
        add(licenseNumberField);
        y += 40;

        // Qualification (non-editable)
        add(createLabel("Qualification:", rightX, y));
        qualificationField.setBounds(rightX + 150, y, 300, 30);
        add(qualificationField);
        y += 40;

        // Experience (non-editable)
        add(createLabel("Experience:", rightX, y));
        experienceField.setBounds(rightX + 150, y, 100, 30);
        add(experienceField);

        JLabel yearsLabel = new JLabel("years");
        yearsLabel.setBounds(rightX + 260, y, 50, 30);
        yearsLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(yearsLabel);
        y += 40;

        // Hire Date (non-editable)
        add(createLabel("Hire Date:", rightX, y));
        hireDateField.setBounds(rightX + 150, y, 150, 30);
        add(hireDateField);
        y += 60;

        // Password Change Section
        JLabel passwordTitle = new JLabel("🔒 Change Password");
        passwordTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        passwordTitle.setBounds(rightX, y, 200, 25);
        passwordTitle.setForeground(new Color(0, 102, 204));
        add(passwordTitle);
        y += 40;

        // Current Password
        add(createLabel("Current Password:", rightX, y));
        currentPasswordField.setBounds(rightX + 150, y, 200, 30);
        add(currentPasswordField);
        y += 35;

        // New Password
        add(createLabel("New Password:", rightX, y));
        newPasswordField.setBounds(rightX + 150, y, 200, 30);
        add(newPasswordField);
        y += 35;

        // Confirm Password
        add(createLabel("Confirm Password:", rightX, y));
        confirmPasswordField.setBounds(rightX + 150, y, 200, 30);
        add(confirmPasswordField);
        y += 50;

        // Buttons
        updateButton.setBounds(leftX + 150, 550, 150, 40);
        styleButton(updateButton);
        add(updateButton);

        changePasswordButton.setBounds(rightX + 150, 550, 180, 40);
        styleButton(changePasswordButton);
        add(changePasswordButton);

        refreshButton.setBounds(leftX + 320, 550, 100, 40);
        styleButton(refreshButton);
        add(refreshButton);

        // Information note
        JLabel noteLabel = new JLabel("Note: Fields with gray background cannot be modified");
        noteLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        noteLabel.setBounds(50, 600, 400, 20);
        noteLabel.setForeground(Color.GRAY);
        add(noteLabel);
    }

    private void setupEventListeners() {
        // Update Profile Button
        updateButton.addActionListener(e -> updateProfile());

        // Change Password Button
        changePasswordButton.addActionListener(e -> changePassword());

        // Refresh Button
        refreshButton.addActionListener(e -> {
            loadDoctorData();
            clearPasswordFields();
            showSuccessDialog("Profile data refreshed successfully!");
        });

        // Email validation
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String email = emailField.getText().trim();
                if (!email.isEmpty() && !emailPattern.matcher(email).matches()) {
                    emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    showErrorDialog("Invalid email format");
                } else {
                    emailField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(0, 102, 204)),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }
            }
        });

        // Phone validation
        phoneField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String phone = phoneField.getText().trim();
                if (!phone.isEmpty() && !phone.matches("^[+]?[0-9\\s\\-\\(\\)]{8,15}$")) {
                    phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    showErrorDialog("Invalid phone number format");
                } else {
                    phoneField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(0, 102, 204)),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }
            }
        });
    }

    private void loadDoctorData() {
        try {
            DatabaseConnection db = new DatabaseConnection();
            String query = "SELECT * FROM doctors WHERE doctor_id = ?";
            PreparedStatement pstmt = db.connection.prepareStatement(query);
            pstmt.setString(1, doctorId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Load non-editable fields
                doctorIdField.setText(rs.getString("doctor_id"));
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                usernameField.setText(rs.getString("username"));
                departmentField.setText(rs.getString("department"));
                specializationField.setText(rs.getString("specialization"));
                licenseNumberField.setText(rs.getString("license_number"));
                qualificationField.setText(rs.getString("qualification"));
                experienceField.setText(rs.getString("experience_years"));
                hireDateField.setText(rs.getString("hire_date"));

                // Load editable fields
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                addressField.setText(rs.getString("address") != null ? rs.getString("address") : "");
                statusComboBox.setSelectedItem(rs.getString("status"));
            }

            rs.close();
            pstmt.close();
            db.connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error loading doctor data: " + e.getMessage());
        }
    }

    private void updateProfile() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            DatabaseConnection db = new DatabaseConnection();
            String query = "UPDATE doctors SET email = ?, phone = ?, address = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE doctor_id = ?";
            PreparedStatement pstmt = db.connection.prepareStatement(query);

            pstmt.setString(1, emailField.getText().trim());
            pstmt.setString(2, phoneField.getText().trim());
            pstmt.setString(3, addressField.getText().trim());
            pstmt.setString(4, (String) statusComboBox.getSelectedItem());
            pstmt.setString(5, doctorId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showSuccessDialog("Profile updated successfully!");
                loadDoctorData(); // Refresh data
            } else {
                showErrorDialog("Failed to update profile. Please try again.");
            }

            pstmt.close();
            db.connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Database error: " + e.getMessage());
        }
    }

    private void changePassword() {
        try {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validate passwords
            if (currentPassword.trim().isEmpty()) {
                showErrorDialog("Please enter your current password");
                return;
            }

            if (newPassword.trim().isEmpty()) {
                showErrorDialog("Please enter a new password");
                return;
            }

            if (newPassword.length() < 6) {
                showErrorDialog("New password must be at least 6 characters long");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showErrorDialog("New password and confirm password do not match");
                return;
            }

            // Verify current password
            DatabaseConnection db = new DatabaseConnection();
            String verifyQuery = "SELECT password FROM doctors WHERE doctor_id = ?";
            PreparedStatement verifyStmt = db.connection.prepareStatement(verifyQuery);
            verifyStmt.setString(1, doctorId);

            ResultSet rs = verifyStmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!currentPassword.equals(storedPassword)) {
                    showErrorDialog("Current password is incorrect");
                    rs.close();
                    verifyStmt.close();
                    db.connection.close();
                    return;
                }
            }
            rs.close();
            verifyStmt.close();

            // Update password
            String updateQuery = "UPDATE doctors SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE doctor_id = ?";
            PreparedStatement updateStmt = db.connection.prepareStatement(updateQuery);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, doctorId);

            int result = updateStmt.executeUpdate();
            if (result > 0) {
                showSuccessDialog("Password changed successfully!");
                clearPasswordFields();
            } else {
                showErrorDialog("Failed to change password. Please try again.");
            }

            updateStmt.close();
            db.connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Database error: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty()) {
            showErrorDialog("Email field cannot be empty");
            return false;
        }

        if (!emailPattern.matcher(email).matches()) {
            showErrorDialog("Please enter a valid email address");
            return false;
        }

        if (!phone.isEmpty() && !phone.matches("^[+]?[0-9\\s\\-\\(\\)]{8,15}$")) {
            showErrorDialog("Please enter a valid phone number");
            return false;
        }

        return true;
    }

    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 140, 30);
        label.setFont(new Font("Tahoma", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 82, 164));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 102, 204));
            }
        });
    }

    private void showSuccessDialog(String message) {
        JDialog successDialog = new JDialog();
        successDialog.setUndecorated(true);
        successDialog.setModal(true);
        successDialog.setSize(400, 150);
        successDialog.setLocationRelativeTo(this);
        successDialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

        JPanel successPanel = new JPanel(new BorderLayout());
        successPanel.setBackground(Color.WHITE);
        successPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("✅");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        messagePanel.add(iconLabel);
        messagePanel.add(messageLabel);

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

    private void showErrorDialog(String message) {
        JDialog errorDialog = new JDialog();
        errorDialog.setUndecorated(true);
        errorDialog.setModal(true);
        errorDialog.setSize(400, 150);
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
}