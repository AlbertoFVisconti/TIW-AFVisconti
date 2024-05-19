package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Folder;

public class FolderDAO {
	private Connection con=null;
	public FolderDAO (Connection connection) {
		this.con=connection;
	}
	
	public List<Folder> getSubFolder(int id) throws SQLException{
		List<Folder> subFolder= new ArrayList<Folder>();
		String query = "SELECT * from cartella where contenitore = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					Folder folder = new Folder(result.getInt("cartellaId"), result.getString("proprietario"), result.getString("nome"), 
							result.getDate("data"), result.getInt("contenitore"));
					subFolder.add(folder);
				}
			}
		}
		if(subFolder.isEmpty())
			return null;
		return subFolder;
	}
	public List<Folder> getMainFolder(String username) throws SQLException{
		List<Folder> subFolder= new ArrayList<Folder>();
		String query = "SELECT * from cartella where proprietario = ? and contenitore=1";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					Folder folder = new Folder(result.getInt("cartellaId"), result.getString("proprietario"), result.getString("nome"), 
							result.getDate("data"), result.getInt("contenitore") );
					subFolder.add(folder);
				}
			}
		}
		if(subFolder.isEmpty())
			return null;
		return subFolder;
	}
		
		
}

