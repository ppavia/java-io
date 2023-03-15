package fr.cimut.ged.entrant.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntegrationReport {

	public static final DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");

	private String documentName;
	private boolean isOK;
	private Date date;
	private String informations;

	public IntegrationReport() {
	}

	public IntegrationReport(String documentName, boolean isOK, String informations) {
		super();
		this.documentName = documentName;
		this.isOK = isOK;
		this.date = new Date();
		this.informations = informations;
	}

	@Override
	public String toString() {
		return documentName + ";" + (isOK ? "OK" : "KO") + ";" + REPORT_DATE_FORMAT.format(date) + ";" + informations + ";";
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInformations() {
		return informations;
	}

	public void setInformations(String informations) {
		this.informations = informations;
	}

}
