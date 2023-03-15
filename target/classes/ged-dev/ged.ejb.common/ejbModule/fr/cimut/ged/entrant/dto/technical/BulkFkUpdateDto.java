package fr.cimut.ged.entrant.dto.technical;

import java.util.List;

public class BulkFkUpdateDto {

	private List<DocumentTypeUpdateDto> updates;

	public BulkFkUpdateDto() {
	}

	public List<DocumentTypeUpdateDto> getUpdates() {
		return updates;
	}

	public void setUpdates(List<DocumentTypeUpdateDto> updates) {
		this.updates = updates;
	}
}
