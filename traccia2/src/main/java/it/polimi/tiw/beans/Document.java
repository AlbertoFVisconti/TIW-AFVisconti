package it.polimi.tiw.beans;

import java.sql.Date;
import java.time.LocalDate;

public class Document {
	private int docId;
	private String proprietario;
	private String nome;
	private Date date;
	private String sommario;
	private String tipo;
	private int contenitore;
	// Constructor
    public Document(int docId, String proprietario, String nome, Date date, String sommario, String tipo, int contenitore) {
        this.docId = docId;
        this.proprietario = proprietario;
        this.nome = nome;
        this.date = date;
        this.sommario = sommario;
        this.tipo = tipo;
        this.contenitore = contenitore;
    }

    // Getters
    public int getDocId() {
        return docId;
    }

    public String getProprietario() {
        return proprietario;
    }

    public String getNome() {
        return nome;
    }

    public Date getDate() {
        return date;
    }

    public String getSommario() {
        return sommario;
    }

    public String getTipo() {
        return tipo;
    }

    public int getContenitore() {
        return contenitore;
    }

    // Setters
    public void setDocId(int docId) {
        this.docId = docId;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSommario(String sommario) {
        this.sommario = sommario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setContenitore(int contenitore) {
        this.contenitore = contenitore;
    }
	
}
