package fr.cimut.ged.entrant.beans.p360;

/**
 * Classe abstraite representant une reponse retournee par les services de Search360
 * 
 * @author pgarel
 */
public abstract class ReponseSearch360Abstract {

	protected String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
