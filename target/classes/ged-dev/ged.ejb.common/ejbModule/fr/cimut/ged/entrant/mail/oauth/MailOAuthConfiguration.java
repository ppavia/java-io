package fr.cimut.ged.entrant.mail.oauth;

/**
 * Configuration fournisseur d'identité OAuth
 */
public class MailOAuthConfiguration {

	private Boolean enabled;
	private OauthFlow flow;
	private String name;
	private String description;

	public MailOAuthConfiguration() {
		enabled = false;
		flow = OauthFlow.ROPC;
		name = "No name";
		description = "No description";
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public OauthFlow getFlow() {
		return flow;
	}

	public void setFlow(OauthFlow flow) {
		this.flow = flow;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
