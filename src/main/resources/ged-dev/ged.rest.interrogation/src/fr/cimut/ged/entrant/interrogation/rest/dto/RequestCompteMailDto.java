package fr.cimut.ged.entrant.interrogation.rest.dto;

public class RequestCompteMailDto extends BaseCompteMailDto {

	private static final long serialVersionUID = 1L;

	private String password;

	public RequestCompteMailDto() {
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}