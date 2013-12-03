package ch.fhnw.oeschfaessler.apsi.lab2.servlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.oeschfaessler.apsi.lab2.Controller;

/**
 * Servlet implementation class RattleBits
 */
@WebServlet("/RattleBits")
public class RattleBits extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

	private final Controller controller;
       
    /**
     * @throws SQLException 
     * @see HttpServlet#HttpServlet()
     */
    public RattleBits() throws SQLException {
        super();
        controller = new Controller(DriverManager.getConnection("jdbc:mysql://localhost/apsi_lab?user=root"));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String page = request.getParameter("page");
		if ("login".equals(page)) {
			controller.loginPage(request, response);
		} else if("register".equals(page)) {
			controller.regsiterPage(request, response);
		} else if("activate".equals(page)) {
			controller.activatePage(request, response);
		} else {
			controller.indexPage(request, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getParameter("register") != null) {
			controller.regsiterPage(request, response);
		} else if (request.getParameter("login") != null) {
			controller.loginPage(request, response);
		}
		
	}

}
