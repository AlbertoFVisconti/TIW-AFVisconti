package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.tiw.beans.User;

public class loginDAO {
	private Connection con=null;
	public loginDAO (Connection connection) {
		this.con=connection;
	}
	public List<User> getUsers() throws SQLException{
		
		List<User> users=new ArrayList<User>();
		String query = "SELECT * FROM user";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);

			result = pstatement.executeQuery();

			while (result.next()) {
				User u= new User(result.getString("nickname"),result.getString("password"),result.getString("email"));
				users.add(u);
			}
				
		} 
		catch (SQLException e) {
			throw new SQLException(e);
			}
		finally{
			try {
				if (result != null)
					result.close();
			} 
			catch (SQLException e1) {
				throw e1;
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} 
			catch (SQLException e2) {
				throw e2;
			}
		}
		return users;
	}
	public int createUser(User user) throws SQLException {
		int code = 0;
		String query = "INSERT into user (nickname, password, email)   VALUES(?, ?, ?)";

		// disable autocommit
		con.setAutoCommit(false);
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);

			pstatement.setString(1, user.getNick());
			pstatement.setString(2, user.getPw());
			pstatement.setString(3, user.getEmail());

			code = pstatement.executeUpdate();
			
			// commit if everything is ok
			con.commit();
		} catch (SQLException e) {
			// rollback if some exception occurs
			con.rollback();
			throw e;
		} finally {
			try {
				pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
			// enable autocommit again
			con.setAutoCommit(true);
		}
		return code;
	}

}
