package fr.cimut.ged.entrant.beans;

import java.io.Serializable;

/**
 * @author spare
 */
public class DocumentTechniqueBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -212434125161269816L;

	public String bidon;
	public boolean succes;

	public boolean isSucces() {
		return succes;
	}

	public void setSucces(boolean succes) {
		this.succes = succes;
	}

	public String getBidon() {
		return bidon;
	}

	public void setBidon(String bidon) {
		this.bidon = bidon;
	}

}
