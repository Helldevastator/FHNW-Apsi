package ch.fhnw.oeschfaessler.apsi.lab2.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sun.istack.internal.NotNull;

public final class DbConnection {
	
	private DbConnection(){}
	
    /**
     * Returns a connection to the database
     * @return connection
     * @throws SQLException thrown on database error
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
    
            try {
                    Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                    throw new AssertionError("MySql driver not installed!");
            }
            
            return DriverManager.getConnection("jdbc:mysql://localhost/apsi_lab?user=root");
    }
}
