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
	public Set<Integer> accessableDocuments(String username) throws SQLException{
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
	
	//returns the id set of file inside a folder 
	public Set<Integer> containedDocuments(int folderID) throws SQLException{
		Set<Integer> docId=new HashSet<Integer>();
		String query = "SELECT * from documento where contenitore = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, folderID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) 
				{
					docId.add(result.getInt("documentoId"));
				}
			}
		}
		if(docId.isEmpty())
			return null;
		return docId;
	}
	
	//moves the doc with that id to the folder with the same id as the destination
	public void moveDoc(int destination, int docId) throws SQLException{
		String query = "UPDATE documento SET contenitore = ? WHERE documentoId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) 
		{
			pstatement.setInt(1, destination);
			pstatement.setInt(2, docId);
			pstatement.executeUpdate();

		}
	}
	//add to the DB a document with the same param
	public void addDoc(Document d) throws SQLException {
		String query = "INSERT INTO documento (proprietario, nome, data, sommario, tipo,contenitore) VALUES (?,?,?,?,?,?)";

		// disable autocommit
		con.setAutoCommit(false);
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);

			pstatement.setString(1, d.getProprietario());
			pstatement.setString(2, d.getNome());
			pstatement.setDate(3, new java.sql.Date(d.getDate().getTime()));
			pstatement.setString(4, d.getSommario());
			pstatement.setString(5, d.getTipo());
			pstatement.setInt(6, d.getContenitore());

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
	
	public void removeDoc(int docid) throws SQLException{
		String query="DELETE FROM documento WHERE documentoId = ?";
			// disable autocommit
				con.setAutoCommit(false);
				PreparedStatement pstatement = null;
				try {
					pstatement = con.prepareStatement(query);

					pstatement.setInt(1,docid);

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
