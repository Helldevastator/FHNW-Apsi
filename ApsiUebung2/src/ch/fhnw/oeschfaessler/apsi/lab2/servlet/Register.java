package ch.fhnw.oeschfaessler.apsi.lab2.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.oeschfaessler.apsi.lab2.model.Company;
import ch.fhnw.oeschfaessler.apsi.lab2.util.MailSendErrorException;
import ch.fhnw.oeschfaessler.apsi.lab2.util.Tools;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * Servlet for the registration process
 */
@WebServlet("/Register")
public class Register extends HttpServlet {

	private static final long serialVersionUID = 1463022435720337490L;
	private final static String REGISTER = "rattle_bits/register.jsp";
	private final static String SUCCESS = "rattle_bits/success.jsp";

	/**
	 * Shows register page
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(REGISTER).forward(request, response);
	}

	/**
	 * do the registration
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("firma",  Tools.encodeHTML(request.getParameter("firma")));
		request.setAttribute("address",  Tools.encodeHTML(request.getParameter("address")));
		request.setAttribute("plz",  Tools.encodeHTML(request.getParameter("plz")));
		request.setAttribute("town", Tools.encodeHTML(request.getParameter("town")));
		request.setAttribute("mail",  Tools.encodeHTML(request.getParameter("mail")));
		
		int zip = 0;
		try {
			zip = Integer.parseInt(request.getParameter("plz"));
		} catch (NumberFormatException e) { messages.add("Ungültige PLZ"); }
		
		Company c = new Company(request.getParameter("firma"), request.getParameter("address"), zip, request.getParameter("town"), request.getParameter("mail"));
		messages.addAll(Tools.validateCompany(c));
		if (messages.size() > 0) {
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(REGISTER).forward(request, response);
		} else {
			try {
				c.save();
				request.setAttribute("message", "Registrierung erfolgreich, bitte warten sie auf die Zugangsdaten per Mail");
				request.getRequestDispatcher(SUCCESS).forward(request, response);
			} catch (SQLException e) {
				messages.add("Fehler bei der Datenbankverbindung ist aufgetreten, bitte versuchen sie es später noch einmal");
				request.setAttribute("messages", messages);
				request.getRequestDispatcher(REGISTER).forward(request, response);
			} catch (MailSendErrorException e) {
				messages.add("Zugangsdaten konnten nicht versant werden, bitte wenden sie sich an den Administrator");
				request.setAttribute("messages", messages);
				request.getRequestDispatcher(REGISTER).forward(request, response);
			}
		}
	}
}
