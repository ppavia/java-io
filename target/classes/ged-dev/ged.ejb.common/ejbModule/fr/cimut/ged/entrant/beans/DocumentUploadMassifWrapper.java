package fr.cimut.ged.entrant.beans;

import fr.cimut.ged.entrant.beans.db.Document;

public class DocumentUploadMassifWrapper {

	private int rowNum;
	private Document document;
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	
	
}
