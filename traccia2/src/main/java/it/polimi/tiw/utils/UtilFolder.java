package it.polimi.tiw.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;

public class UtilFolder extends Folder{
	private List<UtilFolder> subfolder;
	private Set<Integer> documents;

	public UtilFolder(int folderId, String proprietario, String nome, Date date, int contenitore,
			List<UtilFolder> subfolder, Set<Integer> documents) {
		super(folderId, proprietario, nome, date, contenitore);
        this.subfolder = subfolder;
        this.documents = documents;
	}
	public List<UtilFolder> getSubfolder() {
        return subfolder;
    }
    public void setSubfolder(List<UtilFolder> subfolder) {
        this.subfolder = subfolder;
    }
    public Set<Integer> getDocuments() {
        return documents;
    }
    public void setDocuments(Set<Integer> documents) {
        this.documents = documents;
    }

}
