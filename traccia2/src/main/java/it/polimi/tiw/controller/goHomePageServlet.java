package it.polimi.tiw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.*;

/**
 * Servlet implementation class homepageServlet
 */
@WebServlet("/homepage")
public class goHomePageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

    public goHomePageServlet() {
        super();
    }
    private UtilFolder createTree(Folder folder, FolderDAO fdao) throws SQLException{
    	List<Folder> subFolder =fdao.getSubFolder(folder.getId());
    	if (subFolder==null) {
    		return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
        			null, null);
    	}
    	List<UtilFolder> temp= new ArrayList<UtilFolder>();
    	for (Folder f: subFolder) {
    		temp.add(createTree(f,fdao));
    	}
    	return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
    			temp, null);
    }
    
    private void printTree(UtilFolder uf, PrintWriter out) {
    	
    	out.println("<li>"+uf.getNome()+"</li>" );
    	if(uf.getSubfolder()!=null&&!uf.getSubfolder().isEmpty()) {
    		out.print("<ul>");
    		for(UtilFolder suf: uf.getSubfolder()) {
    			printTree(suf, out);
    		}
    		out.print("</ul>");
    	}
    }
    
    
    public void init() throws ServletException {
    	//create template engine for thymeleaf 
    	templateEngine=HtmlThymeleaf.createEngine(getServletContext());
    	//create connection to the DB
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("login.html");
			return;
		}
		User user = (User) session.getAttribute("user");
		// Redirect to the Home page
				String path = "/WEB-INF/homepage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
		//creates the folder tree using UtilFolder as "node"
				FolderDAO fdao= new FolderDAO(connection); 
				List<UtilFolder> tree = new ArrayList<UtilFolder>();
				try {
					if(fdao.getMainFolder(user.getNick())!=null) {
						for(Folder f: fdao.getMainFolder(user.getNick())) {
							tree.add(createTree(f,fdao));	
						}
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
					return;
				}
				//prints the folder tree 
				PrintWriter out = response.getWriter();
				out.println("<HTML><BODY>");
				for(UtilFolder uf:tree)
					printTree(uf,out);
				out.println("</BODY></HTML>");
				out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

} 
