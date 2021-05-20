import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExample {
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306";
    static final String USER = "Test";
    static final String PASS = "test";

    public static void main(String[] args) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement()
        ) {
            String sql = "CREATE DATABASE TEST";
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");

            sql = "CREATE TABLE `TEST`.`new_table` (\n" +
                    "  `idnew_table` INT NOT NULL,\n" +
                    "  PRIMARY KEY (`idnew_table`));";
            stmt.executeUpdate(sql);
            System.out.println("Table created successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}