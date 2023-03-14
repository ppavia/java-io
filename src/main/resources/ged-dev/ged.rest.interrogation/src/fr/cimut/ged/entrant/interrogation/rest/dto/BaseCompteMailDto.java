package fr.cimut.ged.entrant.interrogation.rest.dto;

import java.io.Serializable;
import java.util.List;

abstract class BaseCompteMailDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String login;
	private List<String> inBoxes;
	private List<String> reportEmails;
	private String lastUpdateAuthor;
	private String host;
	private Integer port;
	private String protocole;

	public BaseCompteMailDto() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<String> getReportEmails() {
		return reportEmails;
	}

	public List<String> getInBoxes() {
		return inBoxes;
	}

	public void setInBoxes(List<String> inBoxes) {
		this.inBoxes = inBoxes;
	}

	public void setReportEmails(List<String> reportEmails) {
		this.reportEmails = reportEmails;
	}

	public String getLastUpdateAuthor() {
		return lastUpdateAuthor;
	}

	public void setLastUpdateAuthor(String lastUpdateAuthor) {
		this.lastUpdateAuthor = lastUpdateAuthor;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getProtocole() {
		return protocole;
	}

	public void setProtocole(String protocole) {
		this.protocole = protocole;
	}
}