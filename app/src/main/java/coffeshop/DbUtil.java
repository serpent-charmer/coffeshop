package coffeshop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {

    private static final String DB_URL = "jdbc:sqlite:coffeshop.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void prepare() {

        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("sql.txt"))) {
            while (br.ready()) {
                out.append(br.readLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(Connection conn = getConnection()) {
            Statement statement = conn.createStatement();
            System.out.println(out);
            statement.executeUpdate(out.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
