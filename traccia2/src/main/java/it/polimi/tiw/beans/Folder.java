package it.polimi.tiw.beans;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Folder {
	private int folderId;
	private String proprietario;
	private String nome;
	private Date date;
	private int contenitore;

	
	public Folder(int folderId, String proprietario, String nome, Date date, int contenitore) {
        this.folderId = folderId;
        this.proprietario = proprietario;
        this.nome = nome;
        this.date = date;
        this.contenitore = contenitore;

    }

	public int getId() {
		return this.folderId;
	}
	public void setId(int x) {
		this.folderId=x;
	}
	public String getProprietario() {
		return this.proprietario;
	}
	public void setProprietario(String s) {
		this.proprietario=s;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String s) {
		this.nome=s;
	}
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date d) {
		this.date=d;
	}
    public int getContenitore() {
        return contenitore;
    }
    
    
}
