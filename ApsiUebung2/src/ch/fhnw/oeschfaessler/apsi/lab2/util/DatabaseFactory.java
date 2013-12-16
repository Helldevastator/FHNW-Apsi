package ch.fhnw.oeschfaessler.apsi.lab2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sun.istack.internal.NotNull;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * This class is a factory for the database connection
 */
public final class DatabaseFactory {
	
	/**
	 * Constructor of the class
	 */
	private DatabaseFactory(){}
	
    /**
     * Returns a connection to the database
     * @return connection
     * @throws SQLException thrown on database error
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.jdbc.Driver"); } 
        catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            throw new AssertionError("MySql driver not installed!");
        }
        return DriverManager.getConnection("jdbc:mysql://localhost/apsi_lab?user=root");
    }
}
