package fr.cimut.ged.entrant.mail;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * Classe représentant un mail en erreur
 * 
 * @author jlebourgocq
 *
 */
public class ErrorMailInfo {

	/**
	 * la boite mail associé au mail en erreur
	 */
	private String mailBox;

	/**
	 * le set contenant les différents messages d'erreurs
	 */
	private Set<String> errorMessages = new HashSet<String>();

	/**
	 * le compteur du nombre d'erreur sur le mail
	 */
	private int nbErrorCount;

	/**
	 * date de la première erreur d'intégration
	 */
	private DateTime dateFisrtError;

	/**
	 * date de la dernière erreur d'intégration
	 */
	private DateTime dateLastError;

	/**
	 * constructeur
	 * 
	 * @param mailBox
	 * @param errorMessage
	 */
	public ErrorMailInfo(String mailBox, String errorMessage) {
		dateFisrtError = DateTime.now();
		dateLastError = dateFisrtError;
		nbErrorCount = 1;
		errorMessages.add(errorMessage);
		this.mailBox = mailBox;
	}

	public void addError(String errorMessage) {
		errorMessages.add(errorMessage);
		nbErrorCount++;
		dateLastError = DateTime.now();
	}

	public Set<String> getErrorMessages() {
		return errorMessages;
	}

	public int getNbErrorCount() {
		return nbErrorCount;
	}

	public DateTime getDateFisrtError() {
		return dateFisrtError;
	}

	public DateTime getDateLastError() {
		return dateLastError;
	}

}
