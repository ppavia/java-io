package fr.cimut.ged.entrant.dto.technical;

public class DocumentTypeUpdateDto {
	private String documentId;
	private String typeId;

	public DocumentTypeUpdateDto() {
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
}
