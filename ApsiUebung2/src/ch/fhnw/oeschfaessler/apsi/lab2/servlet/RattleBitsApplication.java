package ch.fhnw.oeschfaessler.apsi.lab2.servlet;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jan Fässler <jan.faessler@students.fhnw.ch>
 * @author Fabio Oesch <fabio.oesch@students.fhwn.ch>
 * 
 * Main servlet application stating point
 */
@WebServlet("/")
public class RattleBitsApplication extends HttpServlet {

	private static final long serialVersionUID = 535610773188857639L;
	private final static String INDEX = "rattle_bits/index.jsp";
	
	/**
	 * shows index page
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}

}
