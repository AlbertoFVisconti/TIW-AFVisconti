package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;

public class FolderDAO {
	private Connection con=null;
	public FolderDAO (Connection connection) {
		this.con=connection;
	}
	//subfolder of the folder with the same id 
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
	//first lvl folder of a user 
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
	//folder accessable by a user 
	public Set<Integer> accessableFolders(String username) throws SQLException{
		Set<Integer> available=new HashSet<Integer>();
		String query = "SELECT * from cartella where proprietario = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					available.add(result.getInt("cartellaId"));
				}
			}
		}
		if(available.isEmpty())
			return null;
		return available;
	}
	//returns the folder with the same folder id
	public Folder Folder(int folderId) throws SQLException {
		String query = "SELECT * from cartella where cartellaId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, folderId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					Folder folder = new Folder(result.getInt("cartellaId"), result.getString("proprietario"), result.getString("nome"), 
							result.getDate("data"),result.getInt("contenitore"));
					return folder;
				}
			}
		}
		return null;
	}
	
	//add a file to the db as the one that has been passed
	public void addFolder(Folder f) throws SQLException {
		String query = "INSERT INTO cartella (proprietario, nome, data,contenitore) VALUES (?,?,?,?)";

		// disable autocommit
		con.setAutoCommit(false);
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);

			pstatement.setString(1, f.getProprietario());
			pstatement.setString(2, f.getNome());
			pstatement.setDate(3, new java.sql.Date(f.getDate().getTime()));
			pstatement.setInt(4, f.getContenitore());

			int code = pstatement.executeUpdate();
			
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
		return;
	}
		
		
}

