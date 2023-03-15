package fr.cimut.ged.entrant.beans;

import java.util.ArrayList;
import java.util.Collection;

import fr.cimut.ged.entrant.beans.db.Document;

/**
 * réponse envoyés suite à un appel de listing de documents
 * 
 * @author jlebourgocq
 *
 */
public class SearchDocResponse {

	private Collection<Document> documents = new ArrayList<Document>();

	public Collection<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Collection<Document> documents) {
		this.documents = documents;
	}

}
