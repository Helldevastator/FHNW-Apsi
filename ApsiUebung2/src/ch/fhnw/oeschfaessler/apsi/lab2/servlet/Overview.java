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
 * 
 * Servlet for the overview page after login
 */
@WebServlet("/Overview")
public class Overview extends HttpServlet {
	
	private static final long serialVersionUID = 3762342919845819198L;
	private final static String OVERVIEW = "rattle_bits/overview.jsp";

	/**
	 * Shows overview page
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
		} else {
			List<String> messages = new ArrayList<>();
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		}
	}

	/**
	 * do the passwort change
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
			return;
		}

		List<String> messages = new ArrayList<>();
		String newPassword = request.getParameter("newpassword");
		String pwMessage = Tools.validatePassword(newPassword);
		if (pwMessage != null) {
			messages.add(pwMessage);
		} else {
			try {
				if (Company.changePassword((String)request.getSession().getAttribute("username"), request.getParameter("oldpassword"), newPassword))
					messages.add("Password ge&auml;ndert");
				else
					messages.add("Falsches Passwort");
			} catch (SQLException e) {
				messages.add("Fehler bei der Datenbankverbindung ist aufgetreten, bitte versuchen sie es später noch einmal");
			}
		}
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(OVERVIEW).forward(request, response);
	}
}
