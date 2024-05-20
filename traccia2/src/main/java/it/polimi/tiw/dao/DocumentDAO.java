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

public class DocumentDAO {
	private Connection con=null;
	public DocumentDAO (Connection connection) {
		this.con=connection;
		}
	//returns the list of document inside the folder identified with cartellaId
	public List<Document> getSubDocument(int cartellaId) throws SQLException{
		List<Document> subDocument= new ArrayList<Document>();
		String query = "SELECT * from documento where contenitore = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, cartellaId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					Document document = new Document(result.getInt("documentoId"), result.getString("proprietario"), result.getString("nome"), 
							result.getDate("data"), result.getString("sommario"),result.getString("tipo"),result.getInt("contenitore"));
					subDocument.add(document);
				}
			}
		}
		if(subDocument.isEmpty())
			return null;
		return subDocument;
	}
	//returns the id list of files visible by a user
	public Set<Integer> accessableFolders(String username) throws SQLException{
		Set<Integer> available=new HashSet<Integer>();
		String query = "SELECT * from documento where proprietario = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					available.add(result.getInt("documentoId"));
				}
			}
		}
		if(available.isEmpty())
			return null;
		return available;
	}
	
	//returns the document with this specific Id
	public Document Document(int docId) throws SQLException{
		String query = "SELECT * from documento where documentoId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, docId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					Document document = new Document(result.getInt("documentoId"), result.getString("proprietario"), result.getString("nome"), 
							result.getDate("data"), result.getString("sommario"),result.getString("tipo"),result.getInt("contenitore"));
					return document;
				}
			}
		}
		return null;
	}
	
}
