package fr.cimut.ged.entrant.beans.p360;

import java.util.List;

/**
 * R�ponse retourn�e par le service web Search360
 * 
 * @author pgarel
 */
public class ReponseSearch360Bis {

	private String message;
	private List<PersonneRORCProspect> resultats;

	public ReponseSearch360Bis() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<PersonneRORCProspect> getResultats() {
		return resultats;
	}

	public void setResultats(List<PersonneRORCProspect> resultats) {
		this.resultats = resultats;
	}

}
