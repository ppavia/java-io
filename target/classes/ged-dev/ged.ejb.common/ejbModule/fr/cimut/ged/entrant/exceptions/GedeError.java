package fr.cimut.ged.entrant.exceptions;

import java.io.Serializable;

/**
 * l'erreur gede à retourner au client
 * 
 * @author jlebourgocq
 *
 */
public class GedeError implements Serializable {

	private String code;
	private String message;
	private String technicalDetail;

	public GedeError() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTechnicalDetail() {
		return technicalDetail;
	}

	public void setTechnicalDetail(String technicalDetail) {
		this.technicalDetail = technicalDetail;
	}

}
