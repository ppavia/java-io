package fr.cimut.ged.entrant.beans.p360;

import java.util.List;

/**
 * reponse retournee par le service web Search360
 * 
 * @author pgarel
 */
public class ReponseSearch360<T extends Search360DtoAbstract> extends ReponseSearch360Abstract {

	private List<T> resultats;

	public ReponseSearch360() {
		super();
	}

	public ReponseSearch360(String message, List<T> resultats) {
		this.message = message;
		this.resultats = resultats;
	}

	public List<T> getResultats() {
		return resultats;
	}

	public void setResultats(List<T> resultats) {
		this.resultats = resultats;
	}

}
