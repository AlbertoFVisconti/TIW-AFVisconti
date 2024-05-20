package it.polimi.tiw.controller;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.loginDAO;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.HtmlThymeleaf;

/**
 * Servlet implementation class loginServlet
 */
@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		//create template resolver for HTML file 
		templateEngine= HtmlThymeleaf.createEngine(getServletContext());
		}

	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 // read form fields
        String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        
        
        //check if the input parameter is in a valid form
        if(username==null||password==null||username.length()==0||password.length()==0){
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "username and password required :) ");
			return;
        }
        	
         //check if the user exists and the pw is the correct one 
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
        		//redirect to the home page 
        		String path;
        		request.getSession().setAttribute("user", u);
        		path = getServletContext().getContextPath() + "/homepage";
    			response.sendRedirect(path);
        		return;
        	}
        }
        //prints error if no match is found
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("errorMsg", "Incorrect username or password");
		String path = "/login.html";
		templateEngine.process(path, ctx, response.getWriter());
        
        
	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

