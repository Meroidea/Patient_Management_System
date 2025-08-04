package patient.management.system;

import java.sql.*;

public class DatabaseConnection {
    public Connection connection;
    public Statement statement;
    public PreparedStatement preparedStatement;

    public DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Patient_Management_System",
                    "root",
                    "123456789"
            );
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
