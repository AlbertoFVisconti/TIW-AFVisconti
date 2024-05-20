package it.polimi.tiw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
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
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.*;

/**
 * Servlet implementation class homepageServlet
 */
@WebServlet("/homepage")
public class HomePageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

    public HomePageServlet() {
        super();
    }
    //creates the folder tree using folder as "nodes"
    private UtilFolder createTree(Folder folder, FolderDAO fdao, DocumentDAO dDAO) throws SQLException{
    	List<Folder> subFolder =fdao.getSubFolder(folder.getId());
    	if (subFolder==null) {
    		return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
        			null, dDAO.containedDocuments(folder.getId()));
    	}
    	List<UtilFolder> temp= new ArrayList<UtilFolder>();
    	for (Folder f: subFolder) {
    		temp.add(createTree(f,fdao, dDAO));
    	}
    	return new UtilFolder(folder.getId(), folder.getProprietario(), folder.getNome(), folder.getDate(), folder.getContenitore(),
    			temp, dDAO.containedDocuments(folder.getId()));
    }
    
    //prints the folder tree 
    private void printTree(UtilFolder uf, PrintWriter out, int docid) {
    	if(uf.getDocuments()!=null&& uf.getDocuments().contains(docid)) {
    		out.println("<li style='color: red;'>"+uf.getNome()+"</li>");
    	}
    	else
    		out.println("<li>"+"<a href="+getServletContext().getContextPath()+"/contenuti?folderId=" + uf.getId()+"&docid="+docid+">"+uf.getNome()+"</a>"+"</li>" );
    	if(uf.getSubfolder()!=null&&!uf.getSubfolder().isEmpty()) {
    		out.print("<ul>");
    		for(UtilFolder suf: uf.getSubfolder()) {
    			printTree(suf, out, docid);
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
		//checks if the user is logged in
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("login.html");
			return;
		}
		User user = (User) session.getAttribute("user");
		//checks if the folder id has been deleted/changed					
				boolean redirect=true;
				Integer docid = null;
				try {
					if(request.getParameter("docid")==null) 
						redirect=false;
					else
						docid = Integer.parseInt(request.getParameter("docid"));
				} catch (NumberFormatException | NullPointerException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
					return;
				}
				DocumentDAO dDAO= new DocumentDAO(connection);			
		//checks if a malicious user tries to access others people docs
				if(redirect) {
					try {
						if(!dDAO.accessableDocuments(user.getNick()).contains(docid)) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed to move this file");
							return;
						}
					} catch (SQLException e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
						return;
					}
				}
				
				
		//creates the folder tree using UtilFolder as "node"
				FolderDAO fdao= new FolderDAO(connection); 
				List<UtilFolder> tree = new ArrayList<UtilFolder>();
				try {
					if(fdao.getMainFolder(user.getNick())!=null) {
						for(Folder f: fdao.getMainFolder(user.getNick())) {
							tree.add(createTree(f,fdao,dDAO));	
						}
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue when reading from db");
					return;
				}
				//prints the folder tree 
				PrintWriter out = response.getWriter();
				out.println("<HTML><BODY>");
				for(UtilFolder uf:tree) {
					if(redirect)
						printTree(uf,out, docid);
					else
						printTree(uf,out, 0);
				}

				// renders part of the Home page content( not the tree= 
				String path = "/WEB-INF/homepage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("redirected", redirect);
				if(redirect) {
					try {
						Document d= dDAO.Document(docid);
						ctx.setVariable("x", d.getNome());
						ctx.setVariable("y", fdao.Folder(d.getContenitore()).getNome());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				templateEngine.process(path, ctx, response.getWriter());
				out.println("</BODY></HTML>");
				out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
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
