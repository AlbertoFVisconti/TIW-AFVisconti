package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class loginDAO {
	private Connection con=null;
	public void loginDao (Connection connection) {
		this.con=connection;
	}
	public List<String> getNicknames() throws SQLException{
		
		List<String> nicknames=new ArrayList<String>();
		String query = "SELECT * FROM user";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);

			result = pstatement.executeQuery();

			while (result.next()) {
				String s= result.getString("nickname");
				nicknames.add(s);
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
		return nicknames;
	}
	

}
