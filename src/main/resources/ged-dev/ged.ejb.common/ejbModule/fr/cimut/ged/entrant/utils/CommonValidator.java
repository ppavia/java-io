package fr.cimut.ged.entrant.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.GedeException;

/**
 * validateur d'objet
 * 
 * @author jlebourgocq
 *
 */
public class CommonValidator {

	/**
	 * verification de non nullité
	 * 
	 * @param object
	 * @param objectName
	 * @throws GedeException
	 */
	public static void assertObjectNotNull(Object object, String objectName) throws GedeException {
		if (null == object) {
			throw new BadRequestException("le champ " + objectName + " ne devrait pas etre null");
		}
	}

	/**
	 * 
	 * verification de non vide pour les collection
	 * 
	 * @param collection
	 * @param objectName
	 * @throws GedeException
	 */
	public static void assertCollectionNotEmpty(Collection<?> collection, String objectName) throws GedeException {
		if (null == collection || collection.size() < 1) {
			throw new BadRequestException("le champ " + objectName + " ne devrait pas etre null ou vide");
		}
	}

	public static void assertCollectionEmpty(Collection<?> collection, String objectName) throws GedeException {
		if (null != collection && collection.size() > 0) {
			throw new BadRequestException("le champ " + objectName + " ne devrait pas etre null ou vide");
		}
	}

	/**
	 * 
	 * verification de non vide pour les String
	 * 
	 * @param collection
	 * @param objectName
	 * @throws GedeException
	 */
	public static void assertStringNotBlank(String st, String objectName) throws GedeException {
		if (StringUtils.isBlank(st)) {
			throw new BadRequestException("le champ " + objectName + " ne devrait pas etre null ou vide");
		}
	}

	/**
	 * envoi du message d'erreur si l'assertion n'est pas vérifiée
	 * 
	 * @param b
	 * @param string
	 */
	public static void assertTrue(boolean booleanAssert, String msgError) throws GedeException {
		if (!booleanAssert) {
			throw new BadRequestException(msgError);

		}

	}

}
