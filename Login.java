package patient.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;


public class Login extends JFrame {

    JTextField textField;
    JPasswordField jpasswordField;
    JButton b1;
    JButton b2;

    public Login() {



        b1 = new JButton("Login");
        b1.setBounds(340, 800, 120, 45);
        b1.setFont(new Font("Tahoma", Font.BOLD, 16));
        b1.setForeground(Color.BLACK);
       b1.setBackground(Color.GREEN);
       //b1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        b1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(b1);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserLogin();
            }
        });


        b2 = new JButton("Make Appointments");
        b2.setBounds(100, 800, 220, 45);
        b2.setFont(new Font("Tahoma", Font.BOLD, 16));
        b2.setForeground(Color.BLACK);
       // b2.setBackground(Color.GREEN);
        //b1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        b2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(b2);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("resources/bg123.png"));
        Image i1 = imageIcon.getImage().getScaledInstance(1683, 1000, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon((i1));
        JLabel label = new JLabel(imageIcon1);
        label.setBounds(0, 0, 1683, 1000);
        add(label);


        getContentPane().setBackground(new Color(255, 238, 230, 255));
        setTitle("PAKENHAM HOSPITAL - Patient Management System");
        setSize(1683, 1000);
        setLocation(0, 0);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }
}