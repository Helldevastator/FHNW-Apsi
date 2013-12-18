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
