package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.loginDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class CheckRegister
 */
@WebServlet("/CheckRegister")
@MultipartConfig
public class CheckRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


    public CheckRegister() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	private Boolean checkEmail(String s) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(emailRegex);

        // Create matcher object
        Matcher matcher = pattern.matcher(s);

        // Check if the email matches the pattern
        return matcher.matches();
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// obtain and escape params
				String usrn = null;
				String email=null;
				String pwd = null;
				String rpwd = null;
				usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
				email=StringEscapeUtils.escapeJava(request.getParameter("email"));
				pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
				rpwd = StringEscapeUtils.escapeJava(request.getParameter("rpwd"));
				if (usrn == null || pwd == null|| email == null|| rpwd == null || usrn.isEmpty() || pwd.isEmpty() || email.isEmpty()||rpwd.isEmpty() ) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Credentials must be not null");
					return;
				}
				if(!checkEmail(email)) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Please check the email");
					return;
				}
				if(!pwd.equals(rpwd)) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Password has not been repeated correctly");
					return;
				}
				
				loginDAO ldao= new loginDAO(connection);
		        List<User> users = new ArrayList<User>();
		        
		        //check if the username is unique
		        try {
		        	users=ldao.getUsers();
		        }
		        catch (SQLException e) {
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println( "Issue when reading from db");
					return;
				}
		        for(User temp: users) {
		        	if(usrn.equals(temp.getNick())) {
		        		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("User already taken");
						return;
		        	}
		        }
		        
		        //adds the user to the db 
		        User u= new User(usrn,pwd, email);
		        try {
		        	ldao.createUser(u);
		        	response.sendRedirect("login.html");
		        }
		        catch (SQLException e) {
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println( "Issue when interacting with db");
					return;
				}
		        
		        
				
	}


	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
