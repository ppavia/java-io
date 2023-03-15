package fr.cimut.ged.entrant.beans;

import java.util.Date;

public class HistoriqueUploadIdoc {

	private Long integrationTotal;
	private Long integrationOK;
	private Long integrationKO;
	private String idDocHistorisation;
	private Date dateUpload;
	private String uploadUser;
	private String zipName;

	public Long getIntegrationTotal() {
		return integrationTotal;
	}
	public void setIntegrationTotal(Long integrationTotal) {
		this.integrationTotal = integrationTotal;
	}
	public Long getIntegrationOK() {
		return integrationOK;
	}
	public void setIntegrationOK(Long integrationOK) {
		this.integrationOK = integrationOK;
	}
	public Long getIntegrationKO() {
		return integrationKO;
	}
	public void setIntegrationKO(Long integrationKO) {
		this.integrationKO = integrationKO;
	}
	public String getIdDocHistorisation() {
		return idDocHistorisation;
	}
	public void setIdDocHistorisation(String idDocHistorisation) {
		this.idDocHistorisation = idDocHistorisation;
	}
	public Date getDateUpload() {
		return dateUpload;
	}
	public void setDateUpload(Date dateUpload) {
		this.dateUpload = dateUpload;
	}
	public String getUploadUser() {
		return uploadUser;
	}
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
	public String getZipName() {
		return zipName;
	}
	public void setZipName(String zipName) {
		this.zipName = zipName;
	}

}
