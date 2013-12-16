package ch.fhnw.oeschfaessler.apsi.lab2;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * This class represents a exception that could be thrown when the login data
 * could not sent to a user
 */
public class MailSendErrorException extends Exception {

	private static final long serialVersionUID = -1874644762415161505L;

	public MailSendErrorException(String s) {
		super(s);
	}

}
