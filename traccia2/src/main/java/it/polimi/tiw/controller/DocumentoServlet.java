package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.HtmlThymeleaf;

/**
 * Servlet implementation class DocumentoServlet
 */
@WebServlet("/documento")
public class DocumentoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       

    public DocumentoServlet() {
        super();
    }
    public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		//create template resolver for HTML file 
		templateEngine= HtmlThymeleaf.createEngine(getServletContext());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//checks if the user is correctly logged in 
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("login.html");
			return;
		}
		User user = (User) session.getAttribute("user");
		
		//checks if the document id has been deleted/changed
				Integer docid = null;
				try {
					docid = Integer.parseInt(request.getParameter("docid"));
				} catch (NumberFormatException | NullPointerException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
					return;
				}
				DocumentDAO dDAO= new  DocumentDAO(connection);
				
		//checks if a malicious user tries to access others people folders
				try {
					if(!dDAO.accessableDocuments(user.getNick()).contains(docid)) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed to see the content of this file");
						return;
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db 1");
					return;
				}
		//gets the document info 
				Document doc;
				try {
					doc = dDAO.Document(docid);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db 2");
					return;
				}
		
		//renders the page content 
		String path = "/WEB-INF/documento.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("doc", doc );
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
