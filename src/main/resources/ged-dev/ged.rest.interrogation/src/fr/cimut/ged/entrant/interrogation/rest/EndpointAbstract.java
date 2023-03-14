package fr.cimut.ged.entrant.interrogation.rest;

import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.GedeException;

public abstract class EndpointAbstract {

	/**
	 * clé partagée pour autoriser les échanges clients serveur
	 */
	public static final String AUTH_KEY = "jkdsozfh-4qsdf7-qsdf";

	protected boolean validAuthKey(String authKey) {
		return AUTH_KEY.equals(authKey);
	}

	public void checkIdMatch(Long idInUrl, Long idObject) throws GedeException {
		if (null == idInUrl || null == idObject || !idInUrl.equals(idObject)) {
			throw new BadRequestException("les identifiants ne correspondent pas : " + idInUrl + "/" + idObject);
		}
	}

}
