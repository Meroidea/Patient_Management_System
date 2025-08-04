package patient.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Reception extends JFrame implements ActionListener {
    JButton b2;

    // Store logged-in doctor information
    private String loggedInDoctorId;
    private String loggedInDoctorName;
    private String doctorDepartment;
    private String doctorSpecialization;
    private JLabel welcomeMessage;
    private JLabel doctorInfoLabel;

    // Main constructor that accepts doctor information
    public Reception(String doctorId, String doctorName, String department, String specialization) {
        this.loggedInDoctorId = doctorId;
        this.loggedInDoctorName = doctorName;
        this.doctorDepartment = department;
        this.doctorSpecialization = specialization;

        initializeUI();
    }

    // Overloaded constructor for backward compatibility (with just ID and name)
    public Reception(String doctorId, String doctorName) {
        this(doctorId, doctorName, "General", "General Practice");
    }

    // Default constructor for backward compatibility (should be avoided)
    public Reception() {
        this("UNKNOWN", "Unknown Doctor", "General", "General Practice");
    }

    private void initializeUI() {
        // Welcome message with doctor name
        welcomeMessage = new JLabel("Welcome! Dr. " + loggedInDoctorName);
        welcomeMessage.setBounds(10, 5, 300, 25);
        welcomeMessage.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcomeMessage.setForeground(new Color(0, 100, 0));
        add(welcomeMessage);

        // Doctor info label (department and specialization)
        doctorInfoLabel = new JLabel("Department: " + doctorDepartment + " | " + doctorSpecialization);
        doctorInfoLabel.setBounds(10, 25, 400, 20);
        doctorInfoLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        doctorInfoLabel.setForeground(new Color(100, 100, 100));
        add(doctorInfoLabel);

        // Logout button
        b2 = new JButton("Logout");
        b2.setBounds(40, 1000, 80, 35);
        b2.setFont(new Font("Tahoma", Font.BOLD, 16));
        b2.setForeground(Color.BLACK);
        b2.setBackground(Color.GREEN);
        b2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b2.addActionListener(this);
        add(b2);

        // Hospital motto (initially hidden)
        JLabel motto = new JLabel("PAKENHAM HOSPITAL");
        motto.setBounds(100, 160, 600, 40);
        motto.setFont(new Font("Tahoma", Font.BOLD, 40));
        motto.setForeground(Color.black);
        motto.setVisible(false);

        // Hospital logo
        ImageIcon logo = new ImageIcon(ClassLoader.getSystemResource("resources/hlogo1.png"));
        Image l = logo.getImage().getScaledInstance(140, 140, Image.SCALE_DEFAULT);
        ImageIcon hlogo = new ImageIcon((l));
        JLabel label = new JLabel(hlogo);
        label.setBounds(45, 50, 140, 140);
        add(label);

        // Right side panel area
        JPanel panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(251, 0, 1456, 1100);
        panel1.setBackground(new Color(255, 248, 230));
        panel1.add(motto);
        add(panel1);

        // Left side upper panel area
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(1, 50, 250, 580);
        panel.setBackground(new Color(255, 255, 255, 255));
        add(panel);

        // Left side lower panel area
        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setBounds(1, 630, 250, 570);
        panel2.setBackground(new Color(50, 127, 168, 100));
        add(panel2);

        // Add new patient button
        JButton btn1 = new JButton("Add Patient");
        btn1.setBounds(20, 170, 200, 40);
        btn1.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn1.setBackground(new Color(246, 215, 118));
        btn1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn1);
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                // Pass doctor ID to NewPatientForm constructor
                panel1.add(new NewPatientForm(loggedInDoctorId));
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Existing patient button
        JButton btn2 = new JButton("Existing Patient");
        btn2.setBounds(20, 220, 200, 40);
        btn2.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn2.setBackground(new Color(176, 196, 222));
        btn2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn2);
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                panel1.add(new ExistingPatient());
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Departments button
        JButton btn3 = new JButton("Departments");
        btn3.setBounds(20, 270, 200, 40);
        btn3.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn3.setBackground(new Color(221, 160, 221));
        btn3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn3);
        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                panel1.add(new DepartmentPanel()); // You'll need to create this class
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Rooms/Suits button
        JButton btn4 = new JButton("Suits");
        btn4.setBounds(20, 320, 200, 40);
        btn4.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn4.setBackground(new Color(255, 182, 193));
        btn4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn4);
        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                panel1.add(new RoomPanel()); // You'll need to create this class
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Your appointments button
        JButton btn5 = new JButton("Your Appointments");
        btn5.setBounds(20, 370, 200, 40);
        btn5.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn5.setBackground(new Color(255, 218, 185));
        btn5.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn5);
        btn5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                panel1.add(new AppointmentPanel(loggedInDoctorId)); // Pass doctor ID
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Your profile button
        JButton btn6 = new JButton("Your Profile");
        btn6.setBounds(20, 420, 200, 40);
        btn6.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn6.setBackground(new Color(173, 216, 230));
        btn6.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn6);
        btn6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                // Pass doctor ID to DoctorProfile constructor
                panel1.add(new DoctorProfile(loggedInDoctorId));
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Patient Reports button
        JButton btn7 = new JButton("Patient Reports");
        btn7.setBounds(20, 470, 200, 40);
        btn7.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn7.setBackground(new Color(152, 251, 152));
        btn7.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btn7);
        btn7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.removeAll();
                panel1.add(new PatientReportsPanel(loggedInDoctorId)); // Pass doctor ID
                panel1.revalidate();
                panel1.repaint();
            }
        });

        // Initialize with a welcome dashboard
        showWelcomeDashboard(panel1);

        setSize(1683, 1000);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        getContentPane().setBackground(new Color(255, 255, 255, 255));
        setTitle("PAKENHAM HOSPITAL - Patient Management System - Dr. " + loggedInDoctorName);
    }

    // Method to show welcome dashboard
    private void showWelcomeDashboard(JPanel panel1) {
        // Create welcome dashboard
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(null);
        welcomePanel.setBackground(new Color(255, 248, 230));
        welcomePanel.setBounds(0, 0, 1456, 1100);

        // Large welcome message
        JLabel welcomeTitle = new JLabel("Welcome to Pakenham Hospital");
        welcomeTitle.setBounds(450, 150, 600, 50);
        welcomeTitle.setFont(new Font("Tahoma", Font.BOLD, 36));
        welcomeTitle.setForeground(new Color(0, 100, 150));
        welcomePanel.add(welcomeTitle);

        // Doctor info display
        JLabel doctorInfoDisplay = new JLabel("<html><center>Logged in as: Dr. " + loggedInDoctorName +
                "<br>Department: " + doctorDepartment +
                "<br>Specialization: " + doctorSpecialization +
                "<br>Doctor ID: " + loggedInDoctorId + "</center></html>");
        doctorInfoDisplay.setBounds(500, 220, 400, 100);
        doctorInfoDisplay.setFont(new Font("Tahoma", Font.PLAIN, 16));
        doctorInfoDisplay.setForeground(new Color(100, 100, 100));
        doctorInfoDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(doctorInfoDisplay);

        // Instructions
        JLabel instructions = new JLabel("<html><center>Use the menu on the left to navigate through the system.<br>" +
                "You can add new patients, view existing patients, manage appointments, and more.</center></html>");
        instructions.setBounds(400, 350, 600, 80);
        instructions.setFont(new Font("Tahoma", Font.PLAIN, 14));
        instructions.setForeground(new Color(150, 150, 150));
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(instructions);

        panel1.add(welcomePanel);
    }

    @Override
    public void actionPerformed(ActionEvent k) {
        if (k.getSource() == b2) {
            // Show logout confirmation
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout, Dr. " + loggedInDoctorName + "?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                new Login();      // Re-open login screen
                setVisible(false);
            }
        }
    }

    // Getter methods for doctor information
    public String getLoggedInDoctorId() {
        return loggedInDoctorId;
    }

    public String getLoggedInDoctorName() {
        return loggedInDoctorName;
    }

    public String getDoctorDepartment() {
        return doctorDepartment;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    // Method to update doctor session (if needed)
    public void updateDoctorSession(String doctorId, String doctorName, String department, String specialization) {
        this.loggedInDoctorId = doctorId;
        this.loggedInDoctorName = doctorName;
        this.doctorDepartment = department;
        this.doctorSpecialization = specialization;

        welcomeMessage.setText("Welcome! Dr. " + doctorName);
        doctorInfoLabel.setText("Department: " + department + " | " + specialization);
        setTitle("PAKENHAM HOSPITAL - Patient Management System - Dr. " + doctorName);
    }

    public static void main(String[] args) {
        // For testing - replace with actual doctor login
        new Reception("DOC001", "Sam Smith", "Cardiology", "Interventional Cardiology");
    }
}