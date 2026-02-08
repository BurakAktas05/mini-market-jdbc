package db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper {
    private static final String url = System.getenv("DB_URL");
    private static final String user = System.getenv("DB_USER");
    private static String password = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    public static void showErrorMessage(Exception e){
        System.out.println("Error : "+ e.getMessage());
        System.out.println("Error Code :" +e.getMessage());
    }
}
