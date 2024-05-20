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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.HtmlThymeleaf;

/**
 * Servlet implementation class courseDetails
 */
@WebServlet("/contenuti")
public class ContenutiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       

    public ContenutiServlet() {
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

		//checks if the folder id has been deleted/changed
		Integer folderId = null;
		Integer docid =null;
		try {
			folderId = Integer.parseInt(request.getParameter("folderId"));
			docid= Integer.parseInt(request.getParameter("docid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		FolderDAO fdao= new FolderDAO(connection);
		DocumentDAO dDAO = new DocumentDAO(connection);
		//checks if a malicious user tries to access others people folders
		try {
			if(!fdao.accessableFolders(user.getNick()).contains(folderId) || (docid!=0 && !dDAO.accessableDocuments(user.getNick()).contains(docid))) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed to see the content of this file");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
			return;
		}
		//moves the folder if needed
		if(docid!=0) {
			try {
				dDAO.moveDoc(folderId, docid);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
				return;
			}
		}
		

			//gets the list of the subfolder  
		List<Folder> folders;
		try {
			folders = fdao.getSubFolder(folderId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
			return;
		}
			//gets the list of the documents   
		List<Document> docs;
		DocumentDAO ddao= new DocumentDAO(connection);
		try {
			docs=ddao.getSubDocument(folderId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
			return;
		}
		
		//renders the page content 
		String path = "/WEB-INF/contenuti.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("folderId",folderId );
		ctx.setVariable("folders",(folders==null)?  new ArrayList<Folder>() :folders );
		ctx.setVariable("doc", (docs==null)?  new ArrayList<Document>() :docs ) ;
		templateEngine.process(path, ctx, response.getWriter());
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
