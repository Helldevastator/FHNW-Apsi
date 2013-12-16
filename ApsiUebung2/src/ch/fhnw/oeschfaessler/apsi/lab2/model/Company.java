package ch.fhnw.oeschfaessler.apsi.lab2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import ch.fhnw.oeschfaessler.apsi.lab2.DatabaseFactory;
import ch.fhnw.oeschfaessler.apsi.lab2.MailSendErrorException;
import ch.fhnw.oeschfaessler.apsi.lab2.MailSessionFactory;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * This class represents the company model.
 */
public class Company {

	private static final String VALIDATE_NAME     = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]+)";
	private static final String VALIDATE_ADDRESS  = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\d ]+)";
	private static final String VALIDATE_TOWN     = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\s\\d]+)";
	private static final String VALIDATE_MAIL     = "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)";
	private static final String VALIDATE_PASSWORD = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]+)";
	
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
	 * @param town Town where the company is located
	 * @param mail Email address of the company
	 */
	public Company(@CheckForNull String firma, @CheckForNull String address, int zip, @CheckForNull String town, @CheckForNull String mail) {
		this.name = firma;
		this.address = address;
		this.zip = zip;
		this.town = town;
		this.mail = mail;
	}

	/**
	 * Gets the name of the company.
	 * @return name
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getName() { return name; }

	/**
	 * Gets the address of the company.
	 * @return address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getAddress() { return address; }

	/**
	 * Gets the zip code of the company.
	 * @return zip code
	 */
	@CheckForNull 
	@CheckReturnValue
	public final int getZip() { return zip; }

	/**
	 * Gets the town of the company.
	 * @return town
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getTown() { return town; }

	/**
	 * Gets the email address of the company.
	 * @return email address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getMail() { return mail; }

	/**
	 * Checks if login data is correct and loads the user data.
	 * @param user Username for the company to log in
	 * @param password Password for the company to log in
	 * @return Data of the company
	 * @throws SQLException thrown if problems with database occur
	 */
	public static boolean checkLogin(@CheckForNull String user, @CheckForNull String password) throws SQLException {
		if (user == null || password == null) return false;
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("SELECT  `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? ");
			stm.setString(1, user);
			stm.setString(2, hash(password));
			ResultSet rs = stm.executeQuery();
			return rs.next();
		}
	}

	/**
	 * Validates the fields of the company.
	 * @return Error message of the validation
	 */
	@Nonnull
	@CheckReturnValue
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		String s;
	    if (name != null) {
	    	 s = name.trim();
	    	if (s.isEmpty()) {                    errors.add("Firmenname eingeben."); } 
	    	else if (s.length() > 20) {           errors.add("Firmenname zu lang (max. 20 Zeichen)."); } 
	    	else if (!s.matches(VALIDATE_NAME)) { errors.add("Ung&uuml;ltige Zeichen im Firmennamen"); }
	    } else 									  errors.add("Firmenname eingeben.");
	    
	    if (address != null) {
	    	s = address.trim();
	    	if (s.isEmpty()) {						       errors.add("Keine Adresse."); } 
	    	else if (!address.matches(VALIDATE_ADDRESS)) { errors.add("Ung&uuml;ltige Adresse."); }
	    } else 											   errors.add("Keine Adresse.");
	    
	    if (zip < 1000 || zip > 9999 || !validatePlz(zip))
	    	errors.add("Ung&uuml;ltige Postleitzahl.");
	    
	    if (town != null) { 
	    	s = town.trim();
	    	if (s.isEmpty()) {					  errors.add("Keine Stadt."); } 
	    	else if (!s.matches(VALIDATE_TOWN)) { errors.add("Ung&uuml;ltige Stadt."); }
	    } else 									  errors.add("Keine Stadt.");
	    
	    if (mail != null) {
	    	s = mail.trim();
	    	if (s.isEmpty() || !s.matches(VALIDATE_MAIL) || !mxLookup(s)) {
	        	errors.add("Ung&uuml;ltige Email-Adresse.");
	        }
	    } else errors.add("Keine Email-Adresse");
	    
		return errors;
	}
	
	/**
	 * Saves the company to the database.
	 * @return reference to the company
	 * @throws SQLException thrown on database errors
	 * @throws MailSendErrorException 
	 */
	@Nonnull
	public final void save() throws SQLException, MailSendErrorException {
		String username = createUsername();
		String password = UUID.randomUUID().toString();
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail`) VALUES (?,?,?,?,?,?,?)");
			stm.setString(1, username);
			stm.setString(2, hash(password));
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
	 * Changes the password for the company 
	 * if the credentials are correct.
	 * @param username Username of the company
	 * @param oldPassword old password of the company
	 * @param newPassword new password of the company
	 * @return true if password was changed
	 * @throws SQLException thrown on database errors
	 */
	public static final boolean changePassword(@CheckForNull String username, @CheckForNull String oldPassword, @CheckForNull String newPassword) throws SQLException {
		if (username == null || oldPassword == null|| newPassword == null) return false;
		try (Connection con = DatabaseFactory.getConnection()) {
			PreparedStatement stm = con.prepareStatement("UPDATE `company` SET `password`= ? WHERE `username`= ? AND `password` = ?");
			stm.setString(1, hash(newPassword));
			stm.setString(2, username);
			stm.setString(3, hash(oldPassword));
			stm.execute();
			return stm.getUpdateCount() > 0;
		} 
	}
	
	/**
	 * Gets the SHA-256 hash of a String.
	 * @param s String to hash
	 * @return hash of the String
	 */
	@Nonnull
	@CheckReturnValue
	private static String hash(@Nonnull String s) {
		try {
			byte[] data = null;
			try { data = MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")); } 
			catch (NoSuchAlgorithmException e) { throw new AssertionError("SHA-256 wird nicht unterstützt"); }
			return new String(data, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("Zeichencodierung UTF-8 wird nicht unterstützt");
		}
	}
	
	/**
	 * Checks if a Mailserver is registered for the specified
	 *  email adress.
	 * @param mail email adress to check
	 * @return true if mailserver is registered
	 */
	@CheckReturnValue
	private static final boolean mxLookup(@Nonnull String mail) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		try {
			String[] temp = mail.split("@");
			Attributes attrs = (new InitialDirContext(env)).getAttributes(temp[1], new String[] {"MX"});
			return !(attrs.get("MX") == null);
		} catch (NamingException e) { return false; }
	}
	
	/**
	 * Validates the zip code with the post.ch service.
	 * @param zip zip to validate
	 * @return true if correct code
	 */
	@CheckReturnValue
	private static final boolean validatePlz(int zip) {
		String line;
		BufferedReader rd = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap="+zip)).openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) if(line.contains("Keine PLZ gefunden")) return false;
		} catch (IOException e) { System.err.println(e.getMessage()); } 
		finally { if (rd != null) try { rd.close(); } catch (IOException e) {} }
		return true;
	}

	/**
	 * Validates the given password.
	 * @param pw password
	 * @return true if password is valid
	 */
	public static final String validatePassword(String pw) {
		String error = null;
		if (pw != null) {
			String s = pw.trim();
			if (s.length() < 8 || s.length() > 64) {  error = "Passwort zu kurz/lang (min. 8 Zeichen/max. 64 Zeichen)."; } 
			else if (!s.matches(VALIDATE_PASSWORD)) { error = "Ungültige Zeichen im Passwort."; }
		} else {                                      error = "Passwort eingeben."; }
		return error;
	}
	
	/**
	 * Creates a new Username with the name of the company as a base.
	 * @return new username
	 * @throws SQLException thrown on database error
	 */
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
