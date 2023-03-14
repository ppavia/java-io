package fr.cimut.ged.entrant.beans.p360;

/**
 * reponse retournee par le service web Search360 lorsqu'une exception est levee
 * 
 * @author pgarel
 */
public class ReponseExceptionSearch360 extends ReponseSearch360Abstract {

	private String messageAdditionnel;

	public ReponseExceptionSearch360() {
		super();
	}

	public ReponseExceptionSearch360(String message, String messageAdditionnel) {
		this.message = message;
		this.messageAdditionnel = messageAdditionnel;
	}

	public String getMessageAdditionnel() {
		return messageAdditionnel;
	}

	public void setMessageAdditionnel(String messageAdditionnel) {
		this.messageAdditionnel = messageAdditionnel;
	}

}
