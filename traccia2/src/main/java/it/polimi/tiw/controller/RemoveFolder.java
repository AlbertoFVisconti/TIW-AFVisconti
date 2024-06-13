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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/RemoveFolder")
public class RemoveFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	
    public RemoveFolder() {
        super();
    }
	public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
	}

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//gets the user username
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		//reads the id of the document that needs to be moved and the id of the folder which will be the destination 
		Integer folderId = null;
		try {
			folderId = Integer.parseInt(request.getParameter("folderId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}
		
		//checks if the use is allowed to move the file in the directory 
		FolderDAO fdao= new FolderDAO(connection);
		try {
			if(!fdao.accessableFolders(user.getNick()).contains(folderId)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed to delete the content of this file");
				return;
			}
			fdao.removeFolder(folderId);
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Issue when reading from db");
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
