package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String DB_URL = "jdbc:hsqldb:file:db/logevents";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS LOG_EVENT (ID VARCHAR(255), DURATION INT, TYPE VARCHAR(255), HOST VARCHAR(255), ALERT BOOLEAN)";

    private static Connection connection;

    static {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            connection = DriverManager.getConnection(DB_URL, "SA", ""); // improvements: database constants should be stored in a properties file to easier make changes and increase security
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }

        try {
            connection.createStatement().execute(CREATE_TABLE);
        } catch (SQLException ex) {
            throw new RuntimeException("Error creating table LOG_EVENT", ex);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}