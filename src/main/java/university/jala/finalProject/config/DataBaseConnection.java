package university.jala.finalProject.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static final String DB_URL = System.getenv("DATABASE_URL");
    private static final String DB_USER = System.getenv("DATABASE_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DATABASE_PASSWORD");

    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Successful Connection!!!");
            return connection;
        } catch (SQLException e) {
            System.err.println("CONNECTION ERROR!!!" + e.getMessage());
            throw  new RuntimeException(e);
        }
    }

}
