package ch.fhnw.oeschfaessler.apsi.lab2.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ch.fhnw.oeschfaessler.apsi.lab2.util.*;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * This class represents the company model.
 */
public class Company {

	@CheckForNull private final String name;
	@CheckForNull private final String address;
	@CheckForNull private final String town;
	@CheckForNull private final String mail;
	private final int zip;
	
	
	/**
	 * Constructor of the class
	 * @param rs ResultSet of a SQL query
	 * @throws SQLException 
	 */
	public Company(@CheckForNull ResultSet rs) throws SQLException {
		this.name = rs.getString("name");
		this.address = rs.getString("address");
		this.zip = rs.getInt("zip");
		this.town = rs.getString("town");
		this.mail = rs.getString("mail");
	}
	
	/**
	 * Constructor of the class
	 * @param name Name of the company
	 * @param address Address of the company
	 * @param zip Zip code of the company
	 * @param town Town of the company
	 * @param mail Email of the company
	 */
	public Company(@CheckForNull String firma, @CheckForNull String address, int zip, @CheckForNull String town, @CheckForNull String mail) {
		this.name = firma;
		this.address = address;
		this.zip = zip;
		this.town = town;
		this.mail = mail;
	}

	/**
	 * Returns the name of the company
	 * @return name
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getName() { return name; }

	/**
	 * Returns the address of the company
	 * @return address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getAddress() { return address; }

	/**
	 * Returns the zip code of the company
	 * @return zip code
	 */
	@CheckForNull 
	@CheckReturnValue
	public final int getZip() { return zip; }

	/**
	 * Returns the town of the company
	 * @return town
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getTown() { return town; }

	/**
	 * Returns the email address of the company
	 * @return email address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getMail() { return mail; }

	/**
	 * Checks if login data is correct
	 * @param user Username
	 * @param password Password
	 * @return state if the login date is valid
	 * @throws SQLException thrown if problems with database occur
	 */
	public static boolean checkLogin(@CheckForNull String user, @CheckForNull String password) throws SQLException {
		if (user == null || password == null) return false;
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("SELECT  `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? ");
			stm.setString(1, user);
			stm.setString(2, Tools.hash(password));
			ResultSet rs = stm.executeQuery();
			return rs.next();
		}
	}
	
	/**
	 * Saves the company into the db
	 * @throws SQLException thrown on database errors
	 * @throws MailSendErrorException thrown if the login data could not be sent
	 */
	public final void save() throws SQLException, MailSendErrorException {
		String username = createUsername();
		String password = UUID.randomUUID().toString();
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail`) VALUES (?,?,?,?,?,?,?)");
			stm.setString(1, username);
			stm.setString(2, Tools.hash(password));
			stm.setString(3, name);
			stm.setString(4, address);
			stm.setInt(5, zip);
			stm.setString(6, town);
			stm.setString(7, mail);
			stm.execute();
			
			try {
				Message message = new MimeMessage(MailSessionFactory.getSession());
				message.setFrom(new InternetAddress("apsilab.oeschfaessler@gmail.com"));
				message.setRecipients(RecipientType.TO, InternetAddress.parse(mail));
				message.setSubject("Ihr Username/Passowrt");
				message.setText("Ihre Zugangsdaten:\nBenutzername: " + username + "\nPasswort: " + password);
				Transport.send(message); 
			} catch (MessagingException e) {
				throw new MailSendErrorException("Zugangsdaten konnten nicht versendet werden");
			}
		}		
	}
	
	/**
	 * Changes the password of a user
	 * @param username Username
	 * @param oldPassword old password
	 * @param newPassword new password
	 * @return state if the password could be changed
	 * @throws SQLException thrown on database errors
	 */
	public static final boolean changePassword(@CheckForNull String username, @CheckForNull String oldPassword, @CheckForNull String newPassword) throws SQLException {
		if (username == null || oldPassword == null|| newPassword == null) return false;
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("UPDATE `company` SET `password`= ? WHERE `username`= ? AND `password` = ?");
			stm.setString(1, Tools.hash(newPassword));
			stm.setString(2, username);
			stm.setString(3, Tools.hash(oldPassword));
			stm.execute();
			return stm.getUpdateCount() > 0;
		} 
	}

	/**
	 * Creates a new Username 
	 * @return new username
	 * @throws SQLException thrown on database error
	 */
	@Nonnull
	@CheckReturnValue
	private final String createUsername() throws SQLException {
		String usernameBase = name != null ? name.replace(" ", "") : "user";
		int c = 0;
		String newUsername;
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("SELECT `username` FROM `company` WHERE `username` = ? ");
			do {
				newUsername = usernameBase + (c > 0 ? "_"+c++ : "");
				stm.setString(1, newUsername);
			} while (stm.executeQuery().next());
			
			return newUsername;
		}
	}
}
