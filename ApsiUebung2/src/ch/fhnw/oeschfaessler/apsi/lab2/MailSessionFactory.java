package ch.fhnw.oeschfaessler.apsi.lab2;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailSessionFactory {
	
	private static final String MAIL_USER = "apsilab.oeschfaessler";
	private static final String MAIL_PASS = "apsilab123";
	
	private static MailSessionFactory instance;
	private Properties props;
	
	private MailSessionFactory() {
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}
	
	public static Session getSession() {
		if (instance == null) instance = new MailSessionFactory();
		return Session.getDefaultInstance(instance.props, new javax.mail.Authenticator() { 
			protected PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(MAIL_USER, MAIL_PASS); 
			}});
	}
}
