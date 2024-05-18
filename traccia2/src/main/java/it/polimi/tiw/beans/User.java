package it.polimi.tiw.beans;

public class User {
	private String nick;
	private String pw;
	private String email;
	public User(String nickname, String password, String email) {
		this.nick=nickname;
		this.pw=password;
		this.email=email;
	}
	public String getNick() {
		return this.nick;
	}
	public String getPw() {
		return this.pw;
	}
	public String getEmail() {
		return this.email;
	}
}
