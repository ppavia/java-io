package fr.cimut.ged.entrant.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.exceptions.CimutMailBlackListedException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.mail.CompteMailManager;
import fr.cimut.ged.entrant.mail.EmailIntegResult;
import fr.cimut.ged.entrant.mail.EmailIntegResult.EmailStatus;
import fr.cimut.ged.entrant.mail.ErrorMailInfo;
import fr.cimut.ged.entrant.mail.MailFolderType;
import fr.cimut.ged.entrant.mail.MailerHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

@Stateless(mappedName = "MailIntegration")
@TransactionAttribute(value = TransactionAttributeType.NEVER)
public class MailIntegrationService {

	private static final Logger LOGGER = Logger.getLogger(MailIntegrationService.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private static Map<String, ErrorMailInfo> mapMailInError = new HashMap<String, ErrorMailInfo>();

	@EJB
	Manager manager;

	@EJB
	Rules ruleManager;

	/**
	 * Recupere la liste des regle de creation automatique des demande SUDE pour un cmroc donné
	 * 
	 * @param environnement
	 * @param cmroc
	 * @return
	 * @throws CimutMetierException
	 */
	private List<Rule> getRules(String environnement, String cmroc) throws CimutMetierException {
		List<Rule> listRules = null;
		try {
			// recupere la liste des regles pour l'organisme en question
			Map<String, List<String>> parameters = new HashMap<String, List<String>>();
			parameters.put("actif", Arrays.asList("true"));
			listRules = ruleManager.list(environnement, cmroc, parameters);
			Iterator<Rule> iter = listRules.iterator();
			// supprime les entrees qui n'ont pas de service affectées
			while (iter.hasNext()) {
				Rule rule = iter.next();
				if (rule.getService() == null || rule.getService().getId() == null) {
					iter.remove();
				}
			}

		} catch (Exception e) {
			throw new CimutMetierException(
					"Impossible de recupere les regle de creation de DA pour le cmroc suivant : " + cmroc + " dans l'environnement " + environnement,
					e);
		}
		return listRules;
	}

	/**
	 * Vérifie la boîte mail, consulte et intègre tous les mails
	 * 
	 * @param compteMail
	 *            informations de la boîte mail
	 * @param emailMaxCount
	 *            nombre de mail maximum à intégrer
	 * @return la map de résultat des mails traités par répertoire
	 */
	public Map<String, EmailIntegResult> checkMailBox(CompteMail compteMail, final long emailMaxCount) {
		LOGGER.debug("Traitement de la boite mail : " + compteMail.getEmail() + ", nombre de mails max : " + emailMaxCount);
		Map<String, EmailIntegResult> resultsMap = new HashMap<String, EmailIntegResult>();
		try {
			// recupere les regles de creation de DA automatique
			List<Rule> rules = this.getRules(compteMail.getEnvironnement(), compteMail.getCmroc());
			if (rules == null || rules.isEmpty()) {
				throw new CimutMetierException("aucune regle pour le compte mail en cours : " + compteMail.getEmail());
			}
			// recupere la boite mail pour le compte courant
			CompteMailManager compteMailManager = new CompteMailManager(compteMail);

			// Boucle sur les répertoires d'intégration de mails
			long remainingEmailCount = emailMaxCount;
			for (String inboxFolderName : compteMail.getInBoxes()) {
				EmailIntegResult result = null;
				if (remainingEmailCount > 0) {
					result = checkInboxFolder(inboxFolderName, compteMailManager, rules, remainingEmailCount);
					if (result != null) {
						remainingEmailCount -= result.getProcessedEmailCount();
					}
				}
				resultsMap.put(inboxFolderName, result);
			}
			// J'ai choisi de ne pas ouvrir la boite a chaque mail, ce qui implique une probleme 
			// lors du expunge en cas de timeout (imap), d'ou la limitation du nombre d'email à 10 
			compteMailManager.setxpunge(true);
			compteMailManager.disconnect();

		} catch (CimutMailException e) {
			LOGGER.error(e.getMessage());
		} catch (CimutMetierException e) {
			LOGGER.fatal(e.getMessage());
		}
		return resultsMap;
	}

	/**
	 * Vérifie la répertoire cible de traitement des mails d'une boîte mail, consulte et intègre tous les mails
	 * 
	 * @param inBoxFolderName
	 * @param compteMailManager
	 * @param rules
	 * @param emailMaxCount
	 * @return le résultat de traitement des mails pour ce répertoire
	 */
	public EmailIntegResult checkInboxFolder(String inBoxFolderName, CompteMailManager compteMailManager, List<Rule> rules, long emailMaxCount) {
		LOGGER.debug("Traitement de la boite mail : " + compteMailManager.getCompteMail().getEmail() + ", répertoire : " + inBoxFolderName
				+ ", nombre de mails restant : " + emailMaxCount);
		EmailIntegResult result = null;
		try {
			List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.INBOX);
			result = new EmailIntegResult(messages.size());

			Iterator<Message> iterator = messages.listIterator();
			while (iterator.hasNext() && result.getProcessedEmailCount() < emailMaxCount) {
				Message message = iterator.next();

				// SOL-1760: Eviter NullPointerException dans checkMailHasToBeIterate
				if (!isMessageIdentifiable(message)) {
					LOGGER.warn("Message impossible a identifier envoye dans spams : message number "+message.getMessageNumber());
					result.onProcessMailInteg(EmailStatus.BLACK_LISTED);
					compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.SPAM);
					continue;
				}

				// on check que le mail doit bien être traité
				if (checkMailHasToBeIterate(message)) {
					try {
						manager.addEmail(message, inBoxFolderName, rules, compteMailManager, compteMailManager.getCompteMail().getEnvironnement());
						result.onProcessMailInteg(EmailStatus.OK);
						deleteErrorMessageInfoIfExist(message);
						// on flague le fait qu'on a bien intégré un mail
						MailerHelper.dateDerniereIntegrationMail = new Date().getTime();
						compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.BACKUP);
					} catch (CimutMailBlackListedException e) {
						LOGGER.warn(e.getMessage());
						result.onProcessMailInteg(EmailStatus.BLACK_LISTED);
						compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.SPAM);
					} catch (Exception e) {
						String errorMessage = "Erreur pour le compte : " + compteMailManager.getCompteMail().getEmail();
						LOGGER.error(errorMessage, e);
						// appel a une méthode de gestion des mails d'erreur avec rejeu
						EmailStatus status = dealWithErrorMessage(compteMailManager, message, inBoxFolderName, e);
						result.onProcessMailInteg(status, errorMessage + " : " + e.getMessage());
					}
				} else {
					result.onProcessMailInteg(EmailStatus.SKIPPED);
				}
			}
			result.onComplete();
		} catch (MessagingException e) {
			String errorMessage = "Interruption de l'intégration de mails, erreur sur : " + compteMailManager.getCompteMail().getEmail() + ", "
					+ MailFolderType.INBOX.getFolderName(inBoxFolderName) + " : " + e.getMessage();
			if (result != null) {
				result.onInterrupt(errorMessage);
			}
			LOGGER.error(errorMessage);
		} finally {
			if (result != null) {
				LOGGER.info("Resultats de l'intégration de mails : " + compteMailManager.getCompteMail().getCmroc() + ", "
						+ compteMailManager.getCompteMail().getEmail() + ", " + MailFolderType.INBOX.getFolderName(inBoxFolderName) + " : "
						+ result.toString());
			}
		}
		return result;
	}

	/**
	 * Supprime l'entrée dans la map d'erreur s'il y en avait une
	 * 
	 * @param message
	 * @throws MessagingException
	 */
	private void deleteErrorMessageInfoIfExist(Message message) throws MessagingException {
		String messageKey = computeMessageKey(message);
		ErrorMailInfo messageRemove = mapMailInError.remove(messageKey);
		if (null != messageRemove) {
			LOGGER.info(
					"le message " + messageKey + " a bien été traité, le rejeu a fonctionné après : " + messageRemove.getNbErrorCount() + " échecs");
			mapMailInError.remove(messageKey);
		}
	}

	/**
	 * Déterminer si on peut calculer une clef de message pour identifier l'email
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	private boolean isMessageIdentifiable(Message message) throws MessagingException {
		try {
			return (message.getSentDate() != null); // Sans ce critère impossible d'identifier le message
		} catch (MessagingException exc) {
			LOGGER.trace("Unable to check sent date : consider message not identifiable", exc);
			return false;
		}
	}

	/**
	 * Détermine si un mail doit être traité ou non Cas des mails en erreur
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	private boolean checkMailHasToBeIterate(Message message) throws MessagingException {
		String messageKey = computeMessageKey(message);
		ErrorMailInfo mailInfo = mapMailInError.get(messageKey);
		if (null == mailInfo || mailInfo.getDateLastError().plusMinutes(GlobalVariable.getNbMinutesTryInterval()).isBeforeNow()) {
			return true;
		}
		return false;
	}

	private String computeMessageKey(Message message) throws MessagingException {
		return new StringBuilder()
				.append(MailerHelper.getSubject(message))
				.append("_")
				.append(message.getSentDate().getTime())
				.append("_")
				.append(getAddressesAsHashString(message.getFrom())) // distinguer entre deux adhérents différents
				.toString();
	}

	private String getAddressesAsHashString(Address[] addresses) {
		if (null == addresses) { // de devrais jamais arriver
			return "?";
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i< addresses.length; i++) {
			result.append(addresses[i].hashCode());
			if (i != addresses.length - 1) {
				result.append("-");
			}
		}
		return result.toString();
	}

	/**
	 * Méthode de gestion des mails en erreur : - on gère les rejeux comme défini en configuration : nombre de
	 * tentatives, nombre d'heures depuis le premier échec
	 * 
	 * @param compteMailManager
	 * @param message
	 * @param inBoxFolderName
	 * @param e
	 * @return le statut d'intégration de ce message en erreur
	 * @throws MessagingException
	 */
	private EmailStatus dealWithErrorMessage(CompteMailManager compteMailManager, Message message, String inBoxFolderName, Exception e)
			throws MessagingException {
		String email = compteMailManager.getCompteMail().getEmail();
		// calcul de la clé permettant d'identifier un mail dans le temps
		String messageKey = computeMessageKey(message);

		ErrorMailInfo mailInfo = mapMailInError.get(messageKey);
		final String errorMessage = e.getMessage();
		if (null == mailInfo) {
			mailInfo = new ErrorMailInfo(email, errorMessage);
			mapMailInError.put(messageKey, mailInfo);
			LOGGER.fatal("\n\nIntégration mail KO \r\n" + "boite : " + email + " \n\n" + "identifiant message : " + messageKey + "\n\n" + "Raison : "
					+ e.getMessage() + "\n\n" + "Rejeu à venir : " + GlobalVariable.getNbMaxTryMailIntegration() + " essais toutes les "
					+ GlobalVariable.getNbMinutesTryInterval() + " minutes \n\n");
		} else {
			mailInfo.addError(errorMessage);
			LOGGER.error("nouvel échec de l'intégration du mail " + messageKey + " total d'échecs : " + mailInfo.getNbErrorCount());
		}

		// check du nombre d'échecs si on a épuisé tous les rejeux alors déplacement dans la boite erreur
		boolean messageIsDiscarded = mailInfo.getNbErrorCount() >= GlobalVariable.getNbMaxTryMailIntegration();
		if (messageIsDiscarded) {
			compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.ERROR);
			LOGGER.fatal("\n\nIntégration mail KO même avec les rejeux => déplacement dans la boite erreur \n\n" + "boite : " + email + " \n\n"
					+ "identifiant message : " + messageKey + " \n\n" + "Raison(s) : \n\n" + StringUtils.join(mailInfo.getErrorMessages(), "\n\n - ")
					+ "\n\n");
		}
		return messageIsDiscarded ? EmailStatus.ON_ERROR_DISCARDED : EmailStatus.ON_ERROR_TO_RETRY;
	}

	/**
	 * Envoi par mail des rapports d'erreur d'intégration d'une boîte mail
	 * 
	 * @param compteMail
	 *            les informations de la boîte mail
	 */
	public void sendIntegrationErrorsByEmail(CompteMail compteMail) {
		try {
			if (compteMail.getRapportEmails() != null && !compteMail.getRapportEmails().isEmpty()) {
				CompteMailManager compteMailManager = new CompteMailManager(compteMail);
				for (String inBoxFolderName : compteMail.getInBoxes()) {
					try {
						LOGGER.info("RAPPORT ROUTINE boite : " + compteMail.getEmail() + " - Folder : " + inBoxFolderName);
						List<Message> errorMessages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.ERROR);
						sendErrorReport(compteMail.getCmroc(), compteMail.getEmail(), compteMail.getRapportEmails(), errorMessages,
								MailFolderType.ERROR.getFolderName(inBoxFolderName));
					} catch (MessagingException e) {
						LOGGER.fatal("Erreur sur la récupération des mails d'erreurs", e);
					} finally {
						compteMailManager.setxpunge(false);
						compteMailManager.disconnect();
						LOGGER.info("END RAPPORT ROUTINE");
					}
				}
			}
		} catch (CimutMailException e1) {
			LOGGER.error("Erreur lors de la recuperation du compte pour la boite email : " + compteMail.getEmail());
		}
	}

	/**
	 * Construit le mail de rapport pour un répertoire d'une boîte mail puis envoi aux destinataires
	 * 
	 * @param cmroc
	 *            identifiant de mutuelle
	 * @param email
	 *            adresse du compte mail
	 * @param destEmail
	 *            liste de destinataire des rapports
	 * @param errorMessages
	 *            les messages d'erreurs à transmettre
	 * @param errorFolderName
	 *            nom du répertoire des mails où l'intégration est en erreur
	 */
	private void sendErrorReport(String cmroc, String email, List<String> destEmails, List<Message> errorMessages, String errorFolderName) {
		// recupere les regle de creation de DA automatique
		try {

			StringBuilder outputMailText = new StringBuilder();
			StringBuilder outputMailHtml = new StringBuilder();

			StringBuilder listingText = new StringBuilder();
			StringBuilder listingHtml = new StringBuilder();

			int counter = 0;

			listingHtml.append("<table>");
			listingHtml.append("<tr><th>Sujet</th><th>Expediteur</th><th>Date</th></tr>");
			listingText.append("\tSujet \t Expediteur \tDate \n");
			for (Message message : errorMessages) {

				String sender = MailerHelper.getFromTo(message);
				String subject = message.getSubject();

				if (subject == null || subject.trim().isEmpty()) {
					subject = "Aucun sujet";
				}

				DateTime dateEmail = null;
				try {
					dateEmail = MailerHelper.getDate(message);
				} catch (Exception e) {
				}

				listingText
						.append("\t" + subject + "\t" + sender + "\t" + ((dateEmail == null) ? "" : DATE_FORMAT.format(dateEmail.toDate())) + "\n");
				listingHtml.append("<tr><td>" + subject + "</td><td>" + sender + "</td><td>"
						+ ((dateEmail == null) ? "" : DATE_FORMAT.format(dateEmail.toDate())) + "</td></tr>");
				counter++;
			}
			listingHtml.append("</table>");

			outputMailHtml.append(GlobalVariable.getHeaderMailMessage());

			if (counter == 0) {
				return;
			} else if (counter == 1) {
				outputMailHtml.append(counter + " email n'a pas été intégré et a été déplacé dans le repertoire " + errorFolderName
						+ " de la boite email suivante : " + email + "." + "<br>");
				outputMailHtml.append("Message n'ayant pu être traité : <br>");
				outputMailText.append(counter + " email n'a pas été intégré et a été déplacé dans le repertoire " + errorFolderName
						+ " de la boite email suivante : " + email + ".\n");
				outputMailText.append("Message n'ayant pu être traité : \n");
			} else {
				outputMailHtml.append(counter + " emails n'ont pas été intégrés et ont été déplacés dans le repertoire " + errorFolderName
						+ " de la boite email suivante : " + email + ".<br>");
				outputMailHtml.append("Liste des messages n'ayant pu être traités : <br><br>");
				outputMailText.append(counter + " emails n'ont pas été intégrés et ont été déplacés dans le repertoire " + errorFolderName
						+ " de la boite email suivante : " + email + ".\n");
				outputMailText.append("Liste des messages n'ayant pu être traités : \n\n");
			}

			outputMailText.append(listingText.toString() + "\n");
			outputMailHtml.append(listingHtml.toString() + "<br>");

			if ("9970".equals(cmroc)) {
				outputMailText.append("Lien vers la boite email: ");
				outputMailHtml.append("Lien : ");

				outputMailText.append("https://mail.cimut.net/owa/\n\n");
				outputMailHtml.append("<a href=\"https://mail.cimut.net/owa/\">" + "https://mail.cimut.net/owa/</a><br><br><br><br>");
			}

			MailerHelper.sendMail(cmroc, destEmails, outputMailText.toString(), outputMailHtml.toString(), email);
		} catch (CimutMailException e) {
			LOGGER.fatal("Erreur lors de l'envoi des rapports d'erreur", e);
		} catch (UnsupportedEncodingException e1) {
			LOGGER.fatal("Erreur sur le traitement des mails d'erreurs", e1);
		} catch (MessagingException e2) {
			LOGGER.fatal("Erreur sur le traitement des mails d'erreurs", e2);
		}
	}
}