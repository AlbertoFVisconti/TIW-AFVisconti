package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetDocDetailsData
 */
@WebServlet("/GetDocDetailsData")
public class GetDocDetailsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; 

    public GetDocDetailsData() {
        super();
    }
    public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		}
    
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		Integer docId = null;
		try {
			docId = Integer.parseInt(request.getParameter("docId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		DocumentDAO dDAO= new DocumentDAO(connection);
		Document res;
		//checks if a malicious user tries to access others people folders, if not it gets the folder with that id 
		try {
			if(!dDAO.accessableDocuments(user.getNick()).contains(docId)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println( "User not allowed to see the content of this file");
				return;
			}
			res = dDAO.Document(docId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Issue when reading from db"); 
			return;
		}
		
		//sends the res folder as a json 
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(res);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
