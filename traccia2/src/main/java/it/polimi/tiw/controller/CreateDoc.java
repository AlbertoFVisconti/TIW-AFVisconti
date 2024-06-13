package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/CreateDoc")
@MultipartConfig
public class CreateDoc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public CreateDoc() {
        super();
    }
	private Date getToday() {
		return new Date(System.currentTimeMillis());
	}
	public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		//creates all the connections and get the user
		FolderDAO fdao= new FolderDAO(connection);
		DocumentDAO dDAO = new DocumentDAO(connection);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		//create the default values 
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
			fatherD = Integer.parseInt(request.getParameter("folderid"));
			System.out.println("nome "+ nomeD+" data "+dateD+" sommario "+summaryD+" tipo "+typeD+" padre "+fatherD);
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			if(fatherD==null)
				return;
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
			System.out.println("nome "+ nomeD+" data "+dateD+" sommario "+summaryD+" tipo "+typeD+" padre "+fatherD);
			return;
		}
		
		//checks if the values make sense
		try {
			if(nomeD.isEmpty()|| summaryD.isEmpty()||typeD.isEmpty()||dateD.after(getToday())||!fdao.accessableFolders(user.getNick()).contains(fatherD)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values ");
				return;
			}
			else {
				//add the document to the DB
				Document d = new Document(0, user.getNick(), nomeD, dateD, summaryD, typeD, fatherD);
				dDAO.addDoc(d);
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db1");
			return;
		} 
		
		
		
	}

}
