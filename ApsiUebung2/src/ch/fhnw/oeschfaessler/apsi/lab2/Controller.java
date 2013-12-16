package ch.fhnw.oeschfaessler.apsi.lab2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.istack.internal.NotNull;

import ch.fhnw.oeschfaessler.apsi.lab2.model.Company;
import ch.fhnw.oeschfaessler.apsi.lab2.model.DbConnection;

public class Controller {
	
	private static String REGISTER = "rattle_bits/register.jsp";
	private static String SUCCESS  = "rattle_bits/success.jsp";
	private static String LOGIN    = "rattle_bits/login.jsp";
	private static String INDEX    = "rattle_bits/index.jsp";
	private static String OVERVIEW = "rattle_bits/overview.jsp";
	
	
	/**
     * Displays the index page of the website.
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
	public void indexPage(@NotNull HttpServletRequest request,@NotNull HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}
	
	/**
     * Displays the overview page of the website.
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
	public void overviewPage(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("username") == null) {
	            response.sendRedirect("Login");
	    } else {
	            List<String> messages = new ArrayList<>();
	            request.setAttribute("messages", messages);
	            request.getRequestDispatcher(OVERVIEW).forward(request, response);
	    }
    }
	
    /**
     * Performs a password change and displays the overview page
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
    public static void doChange(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
            if (request.getSession().getAttribute("username") == null) {
                    response.sendRedirect("Login");
            } else {
                    List<String> messages = new ArrayList<>();
                    try {
                            if (Company.changePassword((String)request.getSession().getAttribute("username"),  request.getParameter("oldpassword"), request.getParameter("newpassword")))
                                    messages.add("Password ge&auml;ndert");
                            else
                                    messages.add("Falsches Passwort");
                    } catch (SQLException e) {
                            System.err.println(e.getMessage());
                            response.sendError(500);
                    }
                    
                    request.setAttribute("messages", messages);
                    request.getRequestDispatcher(OVERVIEW).forward(request, response);
            }
    }
	
    /**
     * Displays a registration and shows a result.
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
	public void regsiterPage(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(REGISTER).forward(request, response);
	}
	
    /**
     * Performs a registration and shows a result.
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
	public void doRegister(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company();
		c.setName(request.getParameter("firma"));
		c.setAddress(request.getParameter("address"));
		if (request.getParameter("plz") != null)
			try { c.setZip(Integer.parseInt(request.getParameter("plz"))); } catch (NumberFormatException e) { messages.add("Ungültige PLZ"); }
		c.setTown(request.getParameter("town"));
		c.setMail(request.getParameter("mail"));
		messages.addAll(c.validate());
		request.setAttribute("firma", c.getName());
		request.setAttribute("address", c.getAddress());
		request.setAttribute("plz", String.valueOf(c.getZip()));
		request.setAttribute("town", c.getTown());
		request.setAttribute("mail", c.getMail());
		if (messages.size() > 0 ) {
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(REGISTER).forward(request, response);
		} else {
			try {
				c.setUsername(createUsername(c.getName()));
				c.setPassword(String.valueOf(UUID.randomUUID()), true);
				c.save();
			} catch (SQLException e) {
				messages.add("Datenbankverbindung fehlgeschlagen, bitte versuchen sie es später noch einmal");
				request.setAttribute("messages", messages);
				request.getRequestDispatcher(REGISTER).forward(request, response);
				return;
			}
			c.sendLoginData();
			request.setAttribute("message", "Registrierung erfolgreich, sie erhalten die Logindaten per Mail");
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		}
	}
	
	public void loginPage(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}
	
    /**
     * Performs the login page of the website.
     * @param request request for the website
     * @param response response sent to the website
     * @throws ServletException thrown by RequestDispatcher
     * @throws IOException thrown by RequestDispatcher
     */
	public void doLogin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company();
		boolean login = false;
		try {
			login = c.checkLogin(request.getParameter("user"), request.getParameter("password"));
		} catch (SQLException e) {
			messages.add("Datenbankverbindung fehlgeschlagen, bitte versuchen sie es später noch einmal");
		}
		if (login) {
			request.getSession().setAttribute("userId", c.getId());
			request.getSession().setAttribute("username", c.getUsername());
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		} else {
			if (messages.size() == 0) messages.add("username oder passwort ist ungültig");
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(LOGIN).forward(request, response);
		}
	}
	
	private final String createUsername(String name) throws SQLException {
        String usernameBase = name != null ? name.replace(" ", "") : "";
        int tries = 0;
        
        String newUsername;
        boolean collision;
        try (Connection con = DbConnection.getConnection()) {
                do {
                        newUsername = usernameBase + (tries > 0 ? tries : "");
                        tries++;
                
                        try (PreparedStatement stm = con.prepareStatement("SELECT `username` FROM `company` WHERE `username` = ? ")) {
                                stm.setString(1, newUsername);
                                collision = stm.executeQuery().next();
                        }
                } while (collision);
                
                return newUsername;
        }
	}
}
