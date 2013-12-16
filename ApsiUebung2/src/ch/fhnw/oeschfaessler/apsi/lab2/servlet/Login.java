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

import ch.fhnw.oeschfaessler.apsi.lab2.Tools;
import ch.fhnw.oeschfaessler.apsi.lab2.model.Company;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * Handles requests for the Login page.
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	
	private static final long serialVersionUID = 7624798074308686628L;
	private final static String LOGIN = "rattle_bits/login.jsp";

	/**
	 * Displays the Login page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.setAttribute("username", "");
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}

	/**
	 * Performs login and possible redirects.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		try {
			if (Company.checkLogin(username, password)) {
				request.getSession().setAttribute("username", username);
		        response.sendRedirect("Overview");
		        return;
			} else if (messages.size() == 0) messages.add("username oder passwort ist ungültig");
			
		} catch (SQLException e) {
			messages.add("Fehler bei der Datenbankverbindung ist aufgetreten, bitte versuchen sie es später noch einmal");
		}
		request.setAttribute("messages", messages);
		request.setAttribute("username", Tools.encodeHTML(request.getParameter("username")));
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}

}
