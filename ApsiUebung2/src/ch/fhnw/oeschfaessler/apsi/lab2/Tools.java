package ch.fhnw.oeschfaessler.apsi.lab2;

import javax.annotation.Nonnull;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * Tool class for some encoding stuff
 */
public final class Tools {

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
}
