package fr.cimut.ged.entrant.interrogation.rest.dto;

public class ResponseCompteMailDto extends BaseCompteMailDto {

	private static final long serialVersionUID = 1L;

	private String lastUpdateDate;

	public ResponseCompteMailDto() {
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
}