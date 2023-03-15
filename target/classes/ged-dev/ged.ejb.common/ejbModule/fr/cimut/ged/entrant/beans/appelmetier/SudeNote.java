package fr.cimut.ged.entrant.beans.appelmetier;

import java.util.ArrayList;
import java.util.List;

public class SudeNote {

	private String type;
	private String sens;
	private String libelle;
	private List<SudeNoteDocument> documents = new ArrayList<SudeNoteDocument>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSens() {
		return sens;
	}

	public void setSens(String sens) {
		this.sens = sens;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public List<SudeNoteDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<SudeNoteDocument> documents) {
		this.documents = documents;
	}

	public void addDocument(SudeNoteDocument daNoteDocument) {
		this.documents.add(daNoteDocument);
	}
}
