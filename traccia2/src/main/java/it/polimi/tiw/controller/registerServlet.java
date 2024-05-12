package it.polimi.tiw.controller;



import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class registerServlet
 */
@WebServlet("/registerServlet")
public class registerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public registerServlet() {super();}
	private Boolean checkEmail(String s) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(emailRegex);

        // Create matcher object
        Matcher matcher = pattern.matcher(s);

        // Check if the email matches the pattern
        return matcher.matches();
	}
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pw1,pw2,user,email;
		pw1= request.getParameter("pw");
		pw2= request.getParameter("rpw");
		user= request.getParameter("username");
		email= request.getParameter("email");
		
		System.out.println(pw1);
        System.out.println(pw2);
        System.out.println(email);
        System.out.println(user);
        if(email==null||user==null||pw1==null||pw2==null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "please fill all the filelds ");
			return;
        }
        if(email.length()==0||user.length()==0||pw1.length()==0||pw2.length()==0||!checkEmail(email)) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "please fill all the filelds with correct values");
			return;
        }
        if(!pw1.equals(pw2)) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the password was not repeated correctly");
        	return;
        }
        System.out.println("account created succesfully");
        response.sendRedirect("login.html");
	}


}

