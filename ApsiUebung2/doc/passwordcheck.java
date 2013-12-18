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
