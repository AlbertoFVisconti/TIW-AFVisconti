package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.UtilFolder;

/**
 * Servlet implementation class FolderData
 */
@WebServlet("/FolderData")
public class FolderData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public FolderData() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    //creates the folder tree using folder as "nodes"
    private UtilFolder createTree(Folder folder, FolderDAO fdao, DocumentDAO dDAO) throws SQLException{
    	List<Folder> subFolder =fdao.getSubFolder(folder.getId());
    	if (subFolder==null) {
    		return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
        			null, dDAO.getSubDocument(folder.getId()));
    	}
    	List<UtilFolder> temp= new ArrayList<UtilFolder>();
    	for (Folder f: subFolder) {
    		temp.add(createTree(f,fdao, dDAO));
    	}
    	return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
    			temp, dDAO.getSubDocument(folder.getId()));
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		FolderDAO fdao= new FolderDAO(connection); 
		DocumentDAO dDAO= new DocumentDAO(connection);
		List<UtilFolder> tree = new ArrayList<UtilFolder>();
		try {
			if(fdao.getMainFolder(user.getNick())!=null) {
				for(Folder f: fdao.getMainFolder(user.getNick())) {
					tree.add(createTree(f,fdao,dDAO));	
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover folders");
			return;
		}

		// Redirect to the Home page and add missions to the parameters
		UtilFolder root= new UtilFolder(0, null, null, null, 0, tree, null);
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(root);
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
