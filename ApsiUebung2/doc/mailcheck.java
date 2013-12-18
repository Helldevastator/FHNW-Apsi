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
