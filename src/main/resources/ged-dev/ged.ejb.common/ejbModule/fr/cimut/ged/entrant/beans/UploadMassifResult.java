package fr.cimut.ged.entrant.beans;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadMassifResult {

	private Map<DocumentUploadMassifWrapper, File> documentToUploadMap;
	// Map numéro de row --> Statut du row
	private Map<Integer, UploadStatut> uploadStatutMap;
	private File rapportIntegration;

	public Map<DocumentUploadMassifWrapper, File> getDocumentToUploadMap() {
		return documentToUploadMap;
	}

	public void setDocumentToUploadMap(Map<DocumentUploadMassifWrapper, File> documentToUploadMap) {
		this.documentToUploadMap = documentToUploadMap;
	}

	public File getRapportIntegration() {
		return rapportIntegration;
	}

	public void setRapportIntegration(File rapportIntegration) {
		this.rapportIntegration = rapportIntegration;
	}

	public Map<Integer, UploadStatut> getUploadStatutMap() {
		return uploadStatutMap;
	}

	public void setUploadStatutMap(Map<Integer, UploadStatut> uploadStatutMap) {
		this.uploadStatutMap = uploadStatutMap;
	}

	public void init() {
		documentToUploadMap = new HashMap<DocumentUploadMassifWrapper, File>();
		uploadStatutMap = new HashMap<Integer, UploadStatut>();
	}

}
