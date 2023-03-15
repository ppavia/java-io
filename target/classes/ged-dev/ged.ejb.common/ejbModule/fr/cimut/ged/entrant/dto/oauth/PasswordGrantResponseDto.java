package fr.cimut.ged.entrant.dto.oauth;

import java.util.Set;

import static java.util.Collections.emptySet;

public class PasswordGrantResponseDto {

	private String token;
	private Set<String> scopes = emptySet();

	public PasswordGrantResponseDto() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Set<String> getScopes() {
		return scopes;
	}

	public void setScopes(Set<String> scopes) {
		this.scopes = scopes;
	}
}
