package ch.fhnw.oeschfaessler.apsi.lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * Tool class with usefull stuff
 */
public final class Tools {
	
	private static final String VALIDATE_PASSWORD = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]+)";

	/**
	 * Encodes html tags
	 * @param s string
	 * @return encoded string
	 */
	@Nonnull
	public static String encodeHTML(@Nonnull String s) {
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++) {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>') out.append("&#"+(int)c+";");
	        else out.append(c);
	    }
	    return out.toString();
	}
	
	/**
	 * Checks if a mail is valid
	 * @param mail email adress
	 * @return state if the email is valid
	 */
	@CheckReturnValue
	public static final boolean checkMail(@Nonnull String mail) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		try {
			String[] temp = mail.split("@");
			Attributes attrs = (new InitialDirContext(env)).getAttributes(temp[1], new String[] {"MX"});
			return !(attrs.get("MX") == null);
		} catch (NamingException e) { return false; }
	}
	
	/**
	 * Validates the zip code with the post.ch website
	 * @param zip zip
	 * @return state if the zip is valid
	 */
	@CheckReturnValue
	public static final boolean checkZIP(int zip) {
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
	 * Gets the hash of a string
	 * @param string
	 * @return hash
	 */
	@Nonnull
	@CheckReturnValue
	public static String hash(@Nonnull String s) {
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
	 * Validates the given password
	 * @param pw password
	 * @return state if password is valid
	 */
	public static final String validatePassword(String pw) {
		if (pw != null) {
			String s = pw.trim();
			if (s.length() < 8 || s.length() > 64) {  return "Passwort zu kurz/lang (min. 8 Zeichen/max. 64 Zeichen)."; } 
			else if (!s.matches(VALIDATE_PASSWORD)) { return "Ungültige Zeichen im Passwort."; }
		} else {                                      return "Passwort eingeben."; }
		return null;
	}
}
