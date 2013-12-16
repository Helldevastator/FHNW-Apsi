package ch.fhnw.oeschfaessler.apsi.lab2.servlet;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.oeschfaessler.apsi.lab2.Controller;

/**
 * Servlet implementation class RattleBits
 */
@WebServlet("/")
public class RattleBits extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Controller controller;

	public RattleBits() { super(); controller = new Controller(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String page = request.getParameter("page");
		if ("login".equals(page)) {
			controller.loginPage(request, response);
		} else if ("register".equals(page)) {
			controller.regsiterPage(request, response);
		} else {
			controller.indexPage(request, response);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("register") != null) {
			controller.doRegister(request, response);
		} else if (request.getParameter("login") != null) {
			controller.doLogin(request, response);
		}

	}

}
