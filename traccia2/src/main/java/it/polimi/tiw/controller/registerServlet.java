package it.polimi.tiw.controller;



import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.dao.loginDAO;
import it.polimi.tiw.object.User;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.HtmlThymeleaf;

/**
 * Servlet implementation class registerServlet
 */
@WebServlet("/registerServlet")
public class registerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	
	public void init() throws ServletException {
			// create database connection 
			connection = ConnectionHandler.getConnection(getServletContext());
			//create template resolver for HTML file 
			templateEngine= HtmlThymeleaf.createEngine(getServletContext());
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
		String pw1,pw2,user,email;
		pw1= request.getParameter("pw");
		pw2= request.getParameter("rpw");
		user= request.getParameter("username");
		email= request.getParameter("email");
		
		//check the input parameters 
        if(email==null||user==null||pw1==null||pw2==null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "please fill all the filelds ");
			return;
        }
        if(email.length()==0||user.length()==0||pw1.length()==0||pw2.length()==0||!checkEmail(email)) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "please fill all the filelds with correct values");
			return;
        }
        if(!pw1.equals(pw2)) {
        	final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
    		ctx.setVariable("errorMsg", "the password was not repeated correctly");
    		String path = "/register.html";
    		templateEngine.process(path, ctx, response.getWriter());
        	return;
        }
        
        //interact with the DB to get the list of users 
        User u= new User(user,pw1, email);
        loginDAO ldao= new loginDAO(connection);
        List<User> users = new ArrayList<User>();
        
        //check if the username is unique
        try {
        	users=ldao.getUsers();
        }
        catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
			return;
		}
        for(User temp: users) {
        	if(user.equals(temp.getNick())) {
            	final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        		ctx.setVariable("errorMsg", "too late :) user already taken");
        		String path = "/register.html";
        		templateEngine.process(path, ctx, response.getWriter());
        		return;
        	}
        }
        
      //adds the user to the DB
        try {
        	ldao.createUser(u);
        	response.sendRedirect("login.html");
        }
        catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when writing to db");
			return;
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
		}
	}
}

