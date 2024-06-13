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

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/CreateFolder")
@MultipartConfig

public class CreateFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public CreateFolder() {
        super();
    }
	private Date getToday() {
		return new Date(System.currentTimeMillis());
	}
	public void init() throws ServletException {
		// create database connection 
		connection = ConnectionHandler.getConnection(getServletContext());
		}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			FolderDAO fdao= new FolderDAO(connection);
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			
			
			String nome= null;
			Date date = null;
			Integer parentId = null;
			
			try {
				nome = StringEscapeUtils.escapeJava(request.getParameter("nomeF"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = (Date) sdf.parse(request.getParameter("dateF"));
				parentId = Integer.parseInt(request.getParameter("folderid"));
			} catch (NumberFormatException | NullPointerException | ParseException e) {
				if(parentId==null)
					return;
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect param values 1 ");
				System.out.println(parentId+" " + nome+ " " + date);
				return;
			}
			//checks that all the values make sense
			try {
				if(nome.isEmpty()|| date.after(getToday())||(parentId!=1 &&!fdao.accessableFolders(user.getNick()).contains(parentId))) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Incorrect or missing param values ");
					return;
				}
				Folder f= new Folder(0,user.getNick(),nome,date, parentId);
				fdao.addFolder(f);
				
			} catch (SQLException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Issue when reading from db");
				return;
			} 
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
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
