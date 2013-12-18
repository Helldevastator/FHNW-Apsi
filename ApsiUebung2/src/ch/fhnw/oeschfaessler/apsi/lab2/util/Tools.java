package ch.fhnw.oeschfaessler.apsi.lab2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import ch.fhnw.oeschfaessler.apsi.lab2.model.Company;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * Tool class with usefull stuff
 */
public final class Tools {
	
	private static final String VALIDATE_NAME     = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]+)";
	private static final String VALIDATE_ADDRESS  = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\d ]+)";
	private static final String VALIDATE_TOWN     = "([ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\s\\d]+)";
	private static final String VALIDATE_MAIL     = "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)";
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
	
	/**
	 * Validates the fields
	 * @return Error messages of the validation
	 */
	@Nonnull
	@CheckReturnValue
	public static List<String> validateCompany(Company c) {
		List<String> errors = new ArrayList<>();
		String s = c.getName();
	    if (s != null) {
	    	s = s.trim();
	    	if (s.isEmpty()) {                    errors.add("Firmenname eingeben."); } 
	    	else if (s.length() > 20) {           errors.add("Firmenname zu lang (max. 20 Zeichen)."); } 
	    	else if (!s.matches(VALIDATE_NAME)) { errors.add("Ung&uuml;ltige Zeichen im Firmennamen"); }
	    } else 									  errors.add("Firmenname eingeben.");
	    
	    s = c.getAddress();
	    if (s != null) {
	    	s = s.trim();
	    	if (s.isEmpty()) {						       errors.add("Keine Adresse."); } 
	    	else if (!s.matches(VALIDATE_ADDRESS)) { errors.add("Ung&uuml;ltige Adresse."); }
	    } else 											   errors.add("Keine Adresse.");
	    
	    int zip = c.getZip();
	    if (zip < 1000 || zip > 9999 || !Tools.checkZIP(zip))
	    	errors.add("Ung&uuml;ltige Postleitzahl.");
	    
	    s = c.getTown();
	    if (s != null) { 
	    	s = s.trim();
	    	if (s.isEmpty()) {					  errors.add("Keine Stadt."); } 
	    	else if (!s.matches(VALIDATE_TOWN)) { errors.add("Ung&uuml;ltige Stadt."); }
	    } else 									  errors.add("Keine Stadt.");
	    
	    s = c.getMail();
	    if (s != null) {
	    	s = s.trim();
	    	if (s.isEmpty() || !s.matches(VALIDATE_MAIL) || !Tools.checkMail(s)) {
	        	errors.add("Ung&uuml;ltige Email-Adresse.");
	        }
	    } else errors.add("Keine Email-Adresse");
	    
		return errors;
	}
	
    /**
     * Returns a quote
     * @return new Quote
     */
    @CheckReturnValue
    @Nonnull
    public static final String getFortuneQuote() {
        BufferedReader rd = null;
        String line = null;
        String quote = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL("http://www.fullerdata.com/FortuneCookie/FortuneCookie.asmx/GetFortuneCookie")).openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                if(!line.contains("<?xml") && !line.contains("<string") && !line.contains("string>")) {
                        quote += line+"\n";
                }
            }
        } catch (IOException e) {
                return "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former. - Albert Einstein";
        } finally {
        	try { if (rd != null) rd.close(); } catch (IOException e) {  }
        }
        
        return Tools.encodeHTML(quote);
    }
}
