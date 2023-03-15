package fr.cimut.ged.entrant.beans.starwebdao;


public class SimpleUpdateResponseDto {

	private String id;

	private Boolean success;

	public SimpleUpdateResponseDto() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}
}
