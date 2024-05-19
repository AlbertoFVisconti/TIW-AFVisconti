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
	public void setNick(String nick) {
		this.nick=nick;
	}
	public void setPw(String pw) {
		this.pw=pw;
	}
	public void setEmail(String email) {
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
