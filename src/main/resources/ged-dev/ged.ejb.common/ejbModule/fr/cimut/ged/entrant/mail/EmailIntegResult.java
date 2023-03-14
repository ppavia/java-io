package fr.cimut.ged.entrant.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contient les résultats et les statistiques de l'intégration de mails
 */
public class EmailIntegResult {

	/**
	 * Statut global du processus d'intégration
	 */
	public static enum IntegStatus {
		IN_PROGRESS,
		INTERRUPTED_ON_ERROR,
		COMPLETE_ON_ERROR,
		COMPLETE_OK
	}

	/**
	 * Statut d'un email dans le processus d'intégration
	 */
	public static enum EmailStatus {
		NOT_PROCESSED, // N'a pas été traité
		SKIPPED, // A été ignoré, cas de mails en erreur déjà traité récemment
		BLACK_LISTED, // A été traité, passe dans la catégorie SPAM
		ON_ERROR_TO_RETRY, // A été traité mais en erreur, il y aura d'autres tentatives
		ON_ERROR_DISCARDED, // A été traité mais erreur, max de tentatives atteints, mail écarté
		OK // A été traité avec succès
	}

	/** Maps de compteurs par statut. */
	private final Map<EmailStatus, Integer> countersMap;

	/** Timestamp de début d'intégration des mails. */
	private final long startTime;

	/** Temps total d'intégration des mails en secondes. */
	private Long integTimeInS;

	/** Résultat à afficher. */
	private String result;

	/** Eventuels messages d'erreur lors de l'intégration. */
	private List<String> errorMessages;

	/** Si les stats ont changées depuis le dernier affichage. */
	private boolean hasChanged;

	/** Statut de l'intégration de mails. */
	private IntegStatus integStatus;

	/**
	 * Constructeur
	 * 
	 * @param totalCount
	 *            nombre de mails total à intégrer
	 */
	public EmailIntegResult(int totalCount) {
		startTime = System.currentTimeMillis();
		integStatus = IntegStatus.IN_PROGRESS;
		this.countersMap = new HashMap<EmailStatus, Integer>();
		for (EmailStatus status : EmailStatus.values()) {
			countersMap.put(status, 0);
		}
		countersMap.put(EmailStatus.NOT_PROCESSED, totalCount);
		errorMessages = new ArrayList<String>();
	}

	/**
	 * Incrémente un compteur pour un statut d'intégration de mail
	 * 
	 * @param status
	 *            statut de l'intégration de mail
	 * @param incrementValue
	 *            valeur de l'incrément
	 */
	private void increment(EmailStatus status, int incrementValue) {
		countersMap.put(status, countersMap.get(status) + incrementValue);
	}

	/**
	 * @param statusGroup
	 *            ensemble de statuts
	 * @return le nombre de mail pour cet ensemble de statuts
	 */
	private int groupCount(EmailStatus... statusGroup) {
		int count = 0;
		for (EmailStatus status : statusGroup) {
			count += countersMap.get(status);
		}
		return count;
	}

	/**
	 * Calcule le temps total d'intégration des mails en secondes
	 */
	private void computeEndTimeInS() {
		integTimeInS = Long.valueOf((System.currentTimeMillis() - startTime) / 1000);
	}

	/**
	 * Appelée lors d'un changement de statut d'intégration de mail
	 * 
	 * @param status
	 *            statut de l'intégration de mail
	 */
	public void onProcessMailInteg(EmailStatus status) {
		// Incrémente le compteur cible
		increment(status, 1);
		// Décrémente le compteur des mails à traiter
		increment(EmailStatus.NOT_PROCESSED, -1);
		hasChanged = true;
	}

	/**
	 * Appelée lors d'un changement de statut d'intégration de mail
	 * 
	 * @param status
	 *            statut de l'intégration de mail
	 * @param errorMessage
	 *            message d'erreur associé
	 */
	public void onProcessMailInteg(EmailStatus status, String errorMessage) {
		onProcessMailInteg(status);
		errorMessages.add(errorMessage);
	}

	/**
	 * Appelée à la fin de l'intégration de mail
	 */
	public void onComplete() {
		computeEndTimeInS();
		integStatus = errorMessages.isEmpty() ? IntegStatus.COMPLETE_OK : IntegStatus.COMPLETE_ON_ERROR;
	}

	/**
	 * Appelée quand l'intégration de mail est interrompue par une erreur
	 */
	public void onInterrupt(String errorMessage) {
		computeEndTimeInS();
		errorMessages.add(errorMessage);
		integStatus = IntegStatus.INTERRUPTED_ON_ERROR;
	}

	/**
	 * @return le nombre de mail qui ont été traités
	 */
	public long getProcessedEmailCount() {
		return groupCount(EmailStatus.OK, EmailStatus.BLACK_LISTED, EmailStatus.ON_ERROR_TO_RETRY, EmailStatus.ON_ERROR_DISCARDED);
	}

	@Override
	public String toString() {
		if (result == null || hasChanged) {
			result = "Integration " + getStatus().name() + (integTimeInS == null ? "" : (", time=" + integTimeInS)) + ", " + countersMap.toString();
		}
		return result;
	}

	/**
	 * @return le statut de l'intégration des mails
	 */
	public IntegStatus getStatus() {
		return integStatus;
	}

	/**
	 * @return les éventuels messages d'erreur
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * @return la map de conteurs des statuts de chaque mail
	 */
	public Map<EmailStatus, Integer> getCountersMap() {
		return countersMap;
	}

	/**
	 * @return le temps passé pour l'intégration de mail en secondes, = null si le processus n'est pas terminé
	 */
	public Long getIntegTimeInS() {
		return integTimeInS;
	}
}