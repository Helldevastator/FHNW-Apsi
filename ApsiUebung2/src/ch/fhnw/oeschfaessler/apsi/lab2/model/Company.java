package ch.fhnw.oeschfaessler.apsi.lab2.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Company {
	
	private final Connection con;
	private int id;
	private String username;
	private String password;
	private String name;
	private String address;
	private int zip;
	private String town;
	private String mail;
	private String activation;
	
	public Company(Connection con) {
		this.con = con;
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
		PreparedStatement stm = con.prepareStatement("SELECT `id`, `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? AND activation IS NULL");
		stm.setString(1, user);
		stm.setString(2, hash(password));
		ResultSet rs = stm.executeQuery();
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
		else return false;
	}
	
	public boolean activate(String activationCode) throws SQLException {
		PreparedStatement stm = con.prepareStatement("UPDATE company SET activation = NULL WHERE activation = ?");
		stm.setString(1, activationCode);
		return stm.executeUpdate() == 1 ? true : false;
	}

	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		
		if (username != null) {
			if (username.trim().length() < 4) {
				errors.add("Username zu kurz");
			} else if (username.trim().length() > 64) {
				errors.add("Username zu lang");
			} else if (!username.matches("[èéÈÉäöüÄÖÜß-_.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen im Usernamen");
	    	}
		}
		
		if (password != null) {
			if (password.trim().length() < 8) {
				errors.add("Passwort zu kurz");
			} else if (password.trim().length() > 64) {
				errors.add("Passwort zu lang");
			} else if (!password.matches("[èéÈÉäöüÄÖÜß-_.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen im Passwort");
	    	}
		}
		
	    if (name != null) {
	    	if (name.trim().isEmpty()) {
	    		errors.add("Firma eingeben");
	    	} else if (name.trim().length() > 20) {
	    		errors.add("Firma zu lang (max 20 Zeichen)");
	    	} else if (!name.matches("[èéÈÉäöüÄÖÜß\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen in der Firmennamen");
	    	}
	    }
	    if (address != null) {
	    	if (address.trim().isEmpty()) {
	    		errors.add("Adresse eingeben");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß-.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen in der Adresse");
	    	}
	    }
	    if (zip != 0) {
	    	// TODO: validate ZIP
	    }
	    if (town != null) {
	    	if (town.trim().isEmpty()) {
	    		errors.add("Stadt eingeben");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß-.\\w\\s]+")) {
	    		errors.add("Ungültige Zeichen in der Stadt");
	    	}
	    }
	    if (mail != null) {
	        if (mail.trim().isEmpty()) {
	            errors.add("Please enter email");
	        } else if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
	            errors.add("Invalid email, please try again.");
	        }
	    }
		return errors;
	}
	
	public final Company save() throws SQLException {
		PreparedStatement stm;
		if (id == 0) {
			stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail`, `activation`) VALUES (?,?,?,?,?,?,?,?)");
		} else {
			stm = con.prepareStatement("UPDATE `company` SET `username`=?,`password`=?,`name`=?,`address`=?,`zip`=?,`town`=?,`mail`=?,`activation`=? WHERE id = ?");
			stm.setInt(9, id);
		}
		stm.setString(1, username);
		stm.setString(2, password);
		stm.setString(3, name);
		stm.setString(4, address);
		stm.setInt(5, zip);
		stm.setString(6, town);
		stm.setString(7, mail);
		stm.setString(8, activation);
		stm.execute();
		return this;
	}
	
	public final boolean sendActivationCode() {
		// TODO: implement activation code sending
		return false;
	}
	
	public final boolean sendLoginData() {
		// TODO: implement login data sending
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((con == null) ? 0 : con.hashCode());
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
		if (con == null) {
			if (other.con != null)
				return false;
		} else if (!con.equals(other.con))
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
	
	private static String hash(String s) {
		byte[] data = null;
		try {
			data = MessageDigest.getInstance("SHA-256").digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			data = s.getBytes();
		}
		return new String(data);
	}
}
