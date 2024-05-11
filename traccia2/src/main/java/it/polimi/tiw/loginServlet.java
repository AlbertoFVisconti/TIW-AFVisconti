package it.polimi.tiw;



import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class loginServlet
 */
@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 // read form fields
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(username==null||password==null||username.length()==0||password.length()==0){
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "username and password required :) ");
			return;
        }
        	
         
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        response.sendRedirect("fileList.html");

         
	}

}

