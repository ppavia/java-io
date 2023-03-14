package fr.cimut.ged.entrant.dto.oauth;

public class PasswordGrantDto {

	private String user;
	private String password;

	public PasswordGrantDto() {
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
