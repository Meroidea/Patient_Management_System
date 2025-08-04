package patient.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserLogin extends JFrame implements ActionListener {

    JTextField textField;
    JPasswordField jpasswordField;
    JButton L1;
    JButton backButton;

    public UserLogin() {

        // Username label
        JLabel nameLabel = new JLabel("Username");
        nameLabel.setBounds(600, 430, 300, 45);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        nameLabel.setForeground(Color.black);
        add(nameLabel);

        // Username text field
        textField = new JTextField();
        textField.setBounds(730, 430, 200, 45);
        textField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        textField.setBackground(new Color(194, 191, 190));
        add(textField);

        // Password label
        JLabel password = new JLabel("Password");
        password.setBounds(600, 500, 300, 45);
        password.setFont(new Font("Tahoma", Font.BOLD, 20));
        password.setForeground(Color.black);
        add(password);

        // Password field
        jpasswordField = new JPasswordField();
        jpasswordField.setBounds(730, 500, 200, 45);
        jpasswordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        jpasswordField.setBackground(new Color(194, 191, 190));
        add(jpasswordField);

        // Login button
        L1 = new JButton("Login");
        L1.setBounds(730, 580, 120, 45);
        L1.setFont(new Font("Tahoma", Font.BOLD, 20));
        L1.setForeground(Color.BLACK);
        L1.setBackground(Color.GREEN);
        L1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        L1.addActionListener(this);
        add(L1);

        // Back button
        backButton = new JButton("Back");
        backButton.setBounds(580, 580, 120, 45);
        backButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(220, 220, 220));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(this);
        add(backButton);

        // Background image
        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("resources/UL.png"));
        Image IM1 = imageIcon.getImage().getScaledInstance(1683, 1000, Image.SCALE_DEFAULT);
        ImageIcon UL1 = new ImageIcon((IM1));
        JLabel label = new JLabel(UL1);
        label.setBounds(0, 0, 1683, 1000);
        add(label);

        getContentPane().setBackground(new Color(255, 238, 230, 255));
        setTitle("PAKENHAM HOSPITAL - Patient Management System - Doctor Login");
        setSize(1683, 1000);
        setLocation(0, 0);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == L1) {
            handleLogin();
        } else if (e.getSource() == backButton) {
            new Login(); // Go back to main login screen
            setVisible(false);
        }
    }

    private void handleLogin() {
        try {
            DatabaseConnection c = new DatabaseConnection();
            String user = textField.getText().trim();
            String pass = new String(jpasswordField.getPassword());

            // Input validation
            if (user.isEmpty() || pass.isEmpty()) {
                showErrorDialog("Please enter both username and password.");
                return;
            }

            // Use PreparedStatement to prevent SQL injection and get complete doctor info
            String query = "SELECT l.*, d.first_name, d.last_name, d.department, d.specialization, d.doctor_id " +
                    "FROM login l " +
                    "JOIN doctors d ON l.user_id = d.doctor_id " +
                    "WHERE l.username = ? AND l.password = ? AND l.status = 'active'";

            PreparedStatement pstmt = c.connection.prepareStatement(query);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                // Login successful - get doctor information
                String doctorId = resultSet.getString("doctor_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String specialization = resultSet.getString("specialization");

                String doctorName = firstName + " " + lastName;

                // Update last login time
                updateLastLogin(c, user);

                // Show success message
                showSuccessDialog("Welcome Dr. " + doctorName + "\nDepartment: " + department);

                // Close login window and open Reception with doctor info
                new Reception(doctorId, doctorName, department, specialization);
                setVisible(false);

            } else {
                // Check if username exists but password is wrong or account is inactive
                String checkUserQuery = "SELECT l.status FROM login l WHERE l.username = ?";
                PreparedStatement checkStmt = c.connection.prepareStatement(checkUserQuery);
                checkStmt.setString(1, user);
                ResultSet userCheck = checkStmt.executeQuery();

                if (userCheck.next()) {
                    String status = userCheck.getString("status");
                    if (!"active".equals(status)) {
                        showErrorDialog("Account is " + status + ". Please contact administrator.");
                    } else {
                        showErrorDialog("Invalid password. Please try again.");
                    }
                } else {
                    showErrorDialog("Invalid username or password. Please try again.");
                }

                userCheck.close();
                checkStmt.close();
            }

            resultSet.close();
            pstmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorDialog("Database connection error. Please try again later.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("An unexpected error occurred. Please try again.");
        }
    }

    // Method to update last login timestamp
    private void updateLastLogin(DatabaseConnection c, String username) {
        try {
            String updateQuery = "UPDATE login SET last_login = CURRENT_TIMESTAMP, login_attempts = 0 WHERE username = ?";
            PreparedStatement updateStmt = c.connection.prepareStatement(updateQuery);
            updateStmt.setString(1, username);
            updateStmt.executeUpdate();
            updateStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to show success dialog
    private void showSuccessDialog(String message) {
        JDialog dialog = new JDialog(this, "Login Successful", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        // Remove title bar buttons
        dialog.setUndecorated(true);

        // Add custom title bar
        JPanel titleBar = new JPanel();
        titleBar.setBounds(0, 0, 400, 30);
        titleBar.setBackground(new Color(40, 167, 69));
        titleBar.setLayout(null);

        JLabel titleLabel = new JLabel("Login Successful");
        titleLabel.setBounds(10, 5, 200, 20);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        titleBar.add(titleLabel);

        dialog.add(titleBar);

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        iconLabel.setBounds(30, 50, 60, 60);
        dialog.add(iconLabel);

        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setBounds(100, 50, 250, 80);
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(0, 100, 0));
        dialog.add(messageLabel);

        JButton closeBtn = new JButton("Continue");
        closeBtn.setBounds(150, 140, 100, 35);
        closeBtn.setBackground(new Color(40, 167, 69));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(ev -> dialog.dispose());
        dialog.add(closeBtn);

        // Disable default close operation
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Method to show error dialog
    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog(this, "Login Error", true);
        dialog.setSize(500, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        // Remove title bar buttons (close, minimize, maximize)
        dialog.setUndecorated(true);

        // Add a custom title bar
        JPanel titleBar = new JPanel();
        titleBar.setBounds(0, 0, 500, 30);
        titleBar.setBackground(new Color(220, 53, 69));
        titleBar.setLayout(null);

        JLabel titleLabel = new JLabel("Login Error");
        titleLabel.setBounds(10, 5, 200, 20);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel);

        dialog.add(titleBar);

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconLabel.setBounds(30, 50, 60, 60);
        dialog.add(iconLabel);

        JLabel messageLabel = new JLabel("<html><b>" + message + "</b></html>");
        messageLabel.setBounds(100, 50, 350, 60);
        messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        messageLabel.setForeground(Color.RED);
        dialog.add(messageLabel);

        JButton closeBtn = new JButton("OK");
        closeBtn.setBounds(200, 130, 100, 35);
        closeBtn.setBackground(new Color(220, 53, 69));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(ev -> dialog.dispose());
        dialog.add(closeBtn);

        // Disable default close operation
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new UserLogin();
    }
}