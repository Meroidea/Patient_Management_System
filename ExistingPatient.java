package patient.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ExistingPatient extends JPanel {

    public ExistingPatient() {
        setLayout(null);
        setBackground(new Color(255, 248, 230));
        setBounds(0, 0, 1673, 700);

        JLabel title = new JLabel("📋 Admitted Patients");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setBounds(20, 20, 300, 30);
        add(title);

        String[] columns = {"Patient ID", "Name", "Room Number", "Disease", "Primary Doctor", "Enrollment Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 70, 1600, 580);
        add(scrollPane);

        try {
            DatabaseConnection db = new DatabaseConnection();
            ResultSet rs = db.statement.executeQuery("SELECT patient_id, CONCAT(first_name, ' ', last_name) AS name, room_number, disease, primary_doctor, enrollment_time FROM patients ORDER BY enrollment_time DESC");
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("patient_id"),
                        rs.getString("name"),
                        rs.getString("room_number"),
                        rs.getString("disease"),
                        rs.getString("primary_doctor"),
                        rs.getString("enrollment_time")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }
}
