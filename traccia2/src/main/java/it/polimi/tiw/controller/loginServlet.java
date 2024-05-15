package it.polimi.tiw.controller;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.it.objects.Course;
import it.polimi.tiw.dao.loginDAO;
import it.polimi.tiw.object.User;

/**
 * Servlet implementation class loginServlet
 */
@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 // read form fields
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        
        //check the input parameter 
        if(username==null||password==null||username.length()==0||password.length()==0){
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "username and password required :) ");
			return;
        }
        	
         //check if the user exists
        loginDAO ldao= new loginDAO(connection);
        List<User> users = new ArrayList<User>();
        
        try {
        	users=ldao.getUsers();
        }
        catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
			return;
		}
        for(User u: users) {
        	if(username.equals(u.getNick())&& password.equals(u.getPw())) {
        		response.sendRedirect("homepage.html");
        		return;
        	}
        }
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "username and pw are not correct");
        
        
	}
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}

