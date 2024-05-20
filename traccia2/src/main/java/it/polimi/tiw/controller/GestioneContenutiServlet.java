package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
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
 * Servlet implementation class GestioneContenutiServlet
 */
@WebServlet("/GestioneContenutiServlet")
public class GestioneContenutiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       

    public GestioneContenutiServlet() {
        super();     
    }
	private Date getToday() {
		return new Date(System.currentTimeMillis());
	}

	public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		//create template resolver for HTML file 
		templateEngine= HtmlThymeleaf.createEngine(getServletContext());
		}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//checks if the user is logged in
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("login.html");
			return;
		}
		User user = (User) session.getAttribute("user");
		
		
		// renders the gestione contenuti page content
		String path = "/WEB-INF/gestioneContenuti.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//checks if the user is logged in
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("login.html");
			return;
		}
		User user = (User) session.getAttribute("user");
		//gets the value from the folder 
		 String formName = request.getParameter("formName");
			FolderDAO fdao= new FolderDAO(connection);

		 //does the logic to create a new folder
		 if("folderForm".equals(formName)) {
			 	String nomeF = null;
				Date dateF = null;
				Integer fatherF = null;
		//read the values from the form
				try {
					fatherF = Integer.parseInt(request.getParameter("fatherF"));
					nomeF = StringEscapeUtils.escapeJava(request.getParameter("nomeF"));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dateF = (Date) sdf.parse(request.getParameter("dateF"));
				} catch (NumberFormatException | NullPointerException | ParseException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
					return;
				}
		//checks that all the values make sense
				try {
					if(nomeF.isEmpty()|| dateF.after(getToday())||(fatherF!=1 &&!fdao.accessableFolders(user.getNick()).contains(fatherF))) {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
						return;
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
					return;
				} 
		//creates the folder and adds it to the db 
				Folder f= new Folder(0,user.getNick(),nomeF,dateF, fatherF);
				try {
					fdao.addFolder(f);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
					return;
				}
			 
			 
		 }
		 
		 //does the logic to create a new doc
		 else if("docForm".equals(formName)) {
			 	String nomeD = null;
				Date dateD = null;
				String summaryD = null;
				String typeD = null;
				Integer fatherD = null;
				//read the values from the form
				try {					
					nomeD = StringEscapeUtils.escapeJava(request.getParameter("nomeD"));					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dateD = (Date) sdf.parse(request.getParameter("dateD"));
					summaryD= StringEscapeUtils.escapeJava(request.getParameter("summaryD"));
					typeD= StringEscapeUtils.escapeJava(request.getParameter("typeD"));
					fatherD = Integer.parseInt(request.getParameter("fatherD"));
				} catch (NumberFormatException | NullPointerException | ParseException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
					return;
				}
				//checks if the values make sense
				try {
					if(nomeD.isEmpty()|| summaryD.isEmpty()||typeD.isEmpty()||dateD.after(getToday())||!fdao.accessableFolders(user.getNick()).contains(fatherD)) {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
						return;
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db1");
					return;
				} 
				//add the document to the DB
				Document d = new Document(0, user.getNick(), nomeD, dateD, summaryD, typeD, fatherD);
				DocumentDAO dDAO = new DocumentDAO(connection);
				try {
					dDAO.addDoc(d);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db 2");
					return;
				}
				
		 }
		 else {
			 doGet(request, response);
		 }
		String path = getServletContext().getContextPath() + "/homepage";
		response.sendRedirect(path);
 		return;
	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
