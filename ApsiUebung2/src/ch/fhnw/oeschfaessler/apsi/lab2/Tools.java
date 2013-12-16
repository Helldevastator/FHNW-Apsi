package ch.fhnw.oeschfaessler.apsi.lab2;

import javax.annotation.Nonnull;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * This class provides useful methods for the application.
 */
public final class Tools {
	/**
	 * No instances should be crated.
	 */
	private Tools(){}
	
	/**
	 * Encodes possible dangerous characters in the String 
	 * for usage in a HTML page.
	 * @param s String to encode
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
}
