package ch.fhnw.oeschfaessler.apsi.lab2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.sun.istack.internal.NotNull;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Company {
	
	private static String USERNAME_VALID = "[èéÈÉäöüÄÖÜß-_.\\w\\s]+";
	private static String PASSWORD_VALID = "[èéÈÉäöüÄÖÜß-_.\\w\\s]+";
	private static String NAME_VALID = "[èéÈÉäöüÄÖÜß\\w\\s]+";
	
	private int id;
	private String username;
	private String password;
	private String name;
	private String address;
	private int zip;
	private String town;
	private String mail;
	private String activation;
	
	public Company() {}
	
	public Company( String username,  String name,  String address,  int zip,  String town,  String mail) {
	    this.username = username;
	    this.name = name;
	    this.address = address;
	    this.zip = zip;
	    this.town = town;
	    this.mail = mail;
	}

	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final String getPassword() {
		return password;
	}
	public final void setPassword(String password, boolean hash) {
		this.password = hash ? hash(password) : password;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getAddress() {
		return address;
	}
	public final void setAddress(String address) {
		this.address = address;
	}
	public final int getZip() {
		return zip;
	}
	public final void setZip(int zip) {
		this.zip = zip;
	}
	public final String getTown() {
		return town;
	}
	public final void setTown(String town) {
		this.town = town;
	}
	public final String getMail() {
		return mail;
	}
	public final void setMail(String mail) {
		this.mail = mail;
	}
	public final String getActivation() {
		return activation;
	}

	public final void setActivation(String activation) {
		this.activation = activation;
	}
	
	public boolean checkLogin(String user, String password) throws SQLException {
		try (Connection con = DbConnection.getConnection()) {
			PreparedStatement stm = con.prepareStatement("SELECT  `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? ");
			stm.setString(1, user);
			stm.setString(2, hash(password));
			try (ResultSet rs = stm.executeQuery()) {
				if (rs.next()) {
					id = rs.getInt(1);
					username = rs.getString(2);
					name = rs.getString(3);
					address = rs.getString(4);
					zip = rs.getInt(5);
					town = rs.getString(6);
					mail = rs.getString(7);
					return true;
				}
			}
		}
		return false;
	}
	
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		
		if (username != null) {
			if (username.trim().length() < 4) {
				errors.add("Username zu kurz");
			} else if (username.trim().length() > 64) {
				errors.add("Username zu lang");
			} else if (!username.matches(USERNAME_VALID)) {
	    		errors.add("Ungültige Zeichen im Usernamen");
	    	}
		}
		
		if (password != null) {
			if (password.trim().length() < 8) {
				errors.add("Passwort zu kurz");
			} else if (password.trim().length() > 64) {
				errors.add("Passwort zu lang");
			} else if (!password.matches(PASSWORD_VALID)) {
	    		errors.add("Ungültige Zeichen im Passwort");
	    	}
		}
		
	    if (name != null) {
	    	if (name.trim().isEmpty()) {
	    		errors.add("Firma eingeben");
	    	} else if (name.trim().length() > 20) {
	    		errors.add("Firma zu lang (max 20 Zeichen)");
	    	} else if (!name.matches(NAME_VALID)) {
	    		errors.add("Ungültige Zeichen in der Firmennamen");
	    	}
	    }
	    if (address != null) {
	    	if (address.trim().isEmpty()) {
	    		errors.add("Adresse eingeben");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß\\-\\.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen in der Adresse");
	    	}
	    }
	    
	    if (zip < 1000 && zip > 9999 && !validatePlz(zip)) {
                errors.add("Ung&uuml;ltige Postleitzahl.");
	    }
	    
	    if (town != null) {
	    	if (town.trim().isEmpty()) {
	    		errors.add("Stadt eingeben");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß\\-\\.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen in der Stadt");
	    	}
	    }
	    if (mail != null) {
	        if (mail.trim().isEmpty()) {
	            errors.add("Please enter email");
	        } else if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
	            errors.add("Invalid email, please try again.");
	        } /* else if(!mxLookup(mail)) {
                errors.add("Ung&uuml;ltige Email-Adresse.");
	        }*/
	    }
		return errors;
	}
	
	public final Company save() throws SQLException {
		try (Connection con = DbConnection.getConnection()) {
			PreparedStatement stm;
			if (id == 0) {
				stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail` VALUES (?,?,?,?,?,?,?)");
			} else {
				stm = con.prepareStatement("UPDATE `company` SET `username`=?,`password`=?,`name`=?,`address`=?,`zip`=?,`town`=?,`mail`=? WHERE id = ?");
				stm.setInt(8, id);
			}
			stm.setString(1, username);
			stm.setString(2, password);
			stm.setString(3, name);
			stm.setString(4, address);
			stm.setInt(5, zip);
			stm.setString(6, town);
			stm.setString(7, mail);
			stm.execute();
			return this;
		}
	}

	public final boolean sendLoginData() {

	    Properties props = new Properties();
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.port", "465");
	
	    Session session = Session.getDefaultInstance(props,
	            new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                            return new PasswordAuthentication("username","password");
	                    }
	            });
	
	    try {
	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("rattlebits2013@gmail.com"));
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.mail));
	            message.setSubject("Your temporary username and password");
	            message.setText("Dear "+ this.username + ","
	                            +"\n\nThank you for your registration on RattleBits. Your registration data are:\n"
	                            +"\nCompany: " + this.name
	                            +"\nAddress: " + this.address
	                            +"\nZipCode: " + this.zip
	                            +"\nCity: " + this.town
	                            +"\n\nYou can login with this data:"
	                            + "\nUsername: " + this.username + "\nPassword: " + this.password
	                            +"\n\nPlease change your password after your first login.\n\nRattleBits AG");
	
	            Transport.send(message); 
	    } catch (MessagingException e) {
	            throw new RuntimeException(e);
	    }
	    return true;
	}
	
	 public final static boolean changePassword(String username, String oldPassword, String newPassword) throws SQLException {
		 if (username == null || oldPassword == null|| newPassword == null) return false;
     
     	 try (Connection con = DbConnection.getConnection()) {
             try (PreparedStatement stm = con.prepareStatement("UPDATE `company` SET `password`= ? WHERE `username`= ? AND `password` = ?")) {
             
                     stm.setString(1, hash(newPassword));
                     stm.setString(2, username);
                     stm.setString(3, hash(oldPassword));
                     
                     stm.execute();
                     return stm.getUpdateCount() > 0;
             }
     	 } 
	 }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + id;
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((town == null) ? 0 : town.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + zip;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Company other = (Company) obj;
		if (activation == null) {
			if (other.activation != null)
				return false;
		} else if (!activation.equals(other.activation))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id != other.id)
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (town == null) {
			if (other.town != null)
				return false;
		} else if (!town.equals(other.town))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (zip != other.zip)
			return false;
		return true;
	}
	
	private static String hash(@NotNull String s) {
		byte[] data = null;
		try {
			data = MessageDigest.getInstance("SHA-256").digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("SHA-256 not installed");
		}
		return new String(data);
	}
	
	private static final boolean mxLookup(@NotNull String mail) {
        String[] temp = mail.split("@");
        String hostname = temp[1];
        Hashtable<String, String> env = new Hashtable<String, String>();
        
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        try {
                DirContext ictx = new InitialDirContext(env);
                Attributes attrs = ictx.getAttributes(hostname, new String[] {"MX"});
                Attribute attr = attrs.get("MX");
                return !(attr == null);
        } catch (NamingException e) { return false; }
	}
	
	private static final boolean validatePlz(int zip) {
        URL url;
        HttpURLConnection conn;
        
        String line;
        try {
                url = new URL("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap="+zip);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                String encoding = conn.getContentEncoding();
                InputStreamReader reader = new InputStreamReader(conn.getInputStream(), encoding == null ? "UTF-8" : encoding); 
                BufferedReader rd = new BufferedReader(reader);
                try {
                        while ((line = rd.readLine()) != null) {
                                if(line.contains("Keine PLZ gefunden"))
                                        return false;
                        }
                } finally { rd.close(); }
                return true;
        } catch (IOException e) {
                System.err.println(e.getMessage());
        }
        return false;
	}
}
