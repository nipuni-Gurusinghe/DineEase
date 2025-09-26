import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3308/restaurant_queue";
    private static final String USERNAME = "root";

    private static final String PASSWORD = "root";


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "MySQL Driver not found: " + e.getMessage(),
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean testConnection() {
        String[] passwords = {"root", "password", "", "1234", "MySQL80"};

        for (String pwd : passwords) {
            try {
                Connection conn = DriverManager.getConnection(URL, USERNAME, pwd);
                System.out.println("Connected successfully with password: " +
                        (pwd.isEmpty() ? "[empty]" : pwd));
                conn.close();
                return true;
            } catch (SQLException e) {
                System.out.println("Failed with password '" + pwd + "': " + e.getMessage());
            }
        }
        return false;
    }

    public static void initializeDatabase() {
        if (!testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to MySQL database!\n\n" +
                            "Tried common passwords: root, password, [empty], 1234\n\n" +
                            "Please check your MySQL Workbench password and update DatabaseUtil.java",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Connected but error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}