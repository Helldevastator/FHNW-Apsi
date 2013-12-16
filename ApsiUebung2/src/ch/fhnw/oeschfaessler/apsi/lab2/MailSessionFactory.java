package ch.fhnw.oeschfaessler.apsi.lab2;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * This class is a factory for a mail session
 */
public class MailSessionFactory {
	
	/**
	 * Username of the sending mail
	 */
	private static final String MAIL_USER = "apsilab.oeschfaessler";
	
	/**
	 * Password of the sending mails
	 */
	private static final String MAIL_PASS = "apsilab123";
	
	/**
	 * Instance of the factory
	 */
	private static MailSessionFactory instance;
	
	/**
	 * Properities of the smtp server
	 */
	private Properties props;
	
	/**
	 * Constructor of the class
	 */
	private MailSessionFactory() {
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}
	
	/**
	 * Returns a session to a smtp server
	 * @return session
	 */
	public static Session getSession() {
		if (instance == null) instance = new MailSessionFactory();
		return Session.getDefaultInstance(instance.props, new javax.mail.Authenticator() { 
			protected PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(MAIL_USER, MAIL_PASS); 
			}});
	}
}
