package fr.cimut.ged.entrant.service;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.NotFoundException;
import fr.cimut.ged.entrant.mail.CompteMail;

@Stateless(mappedName = "MailAdmin")
public class MailAdminService {

	@EJB
	CompteMailFileService filesService;

	/**
	 * Permet de récupérer tous les comptes mail d'une mutuelle
	 * 
	 * @param cmroc
	 *            l'identifiant de la mutuelle
	 * @return la liste des comptes mails
	 * @throws CimutConfException
	 */
	public List<CompteMail> getComptesMail(String cmroc) throws CimutConfException {
		return filesService.getComptesMailFromFiles(cmroc);
	}

	/**
	 * Permet de récupérer un compte mail d'une mutuelle
	 *
	 * @param email
	 *            l'adresse mail du compte mail
	 * @param cmroc
	 *            l'identifiant de la mutuelle
	 * @return le compte mail
	 * @throws CimutConfException
	 * @throws BadRequestException
	 */
	public CompteMail getCompteMail(String email, String cmroc) throws NotFoundException, CimutConfException {
		CompteMail currentCompteMail = filesService.getCompteMailFromFileByEmail(email);
		if (currentCompteMail == null || !currentCompteMail.getCmroc().equals(cmroc)) {
			throw new NotFoundException("Le compte mail " + email + " n'existe pas pour la mutuelle " + cmroc);
		} else {
			return currentCompteMail;
		}
	}

	/**
	 * Ajout d'un nouveau compte mail.
	 * 
	 * @param newCompteMail
	 *            : nouveau compte mail à ajouter.
	 * @throws BadRequestException
	 * @throws CimutConfException
	 */
	public void createCompteMail(CompteMail newCompteMail) throws BadRequestException, CimutConfException {
		if (filesService.isCompteMailFileExist(newCompteMail.getEmail())) {
			throw new BadRequestException("Le compte mail " + newCompteMail.getEmail() + " existe deja");
		} else {
			newCompteMail.setLastUpdateDate(new Date());
			filesService.createCompteMailFile(newCompteMail);
		}
	}

	/**
	 * Mise à jour d'un compte mail existant.
	 * 
	 * @param email
	 *            l'email du compte mail
	 * @param cmroc
	 *            l'identifiant de la mutuelle
	 * @param login
	 *            le login du compte mail
	 * @param password
	 *            le nouveau mot de passe
	 * @param inBoxes
	 *            les répertoires de traitement des mails
	 * @param lastUpdateAuthor,
	 *            le nouvel auteur de la modification
	 * @param reportEmails,
	 *            les nouveaux emails de rapport
	 * @return le compte mail mis à jour
	 * @throws CimutConfException
	 * @throws NotFoundException
	 */
	public CompteMail updateCompteMail(String email, String cmroc, String login, String password, List<String> inBoxes, String lastUpdateAuthor,
			List<String> reportEmails) throws CimutConfException, NotFoundException {
		if (filesService.isCompteMailFileExist(email)) {

			// 1 - Récupération de l'ancienne version du compte mail existant
			CompteMail compteMailToUpdate = this.getCompteMail(email, cmroc);

			// 2 - Copie de sauvegarde du compte mail existant dans le répertoire "backup"
			filesService.saveCompteMailFile(compteMailToUpdate);

			// 3 - Mise à jour du compte mail existant
			if (StringUtils.isNotEmpty(login)) {
				compteMailToUpdate.setLogin(login);
			}
			if (StringUtils.isNotEmpty(password)) {
				compteMailToUpdate.setPassword(password);
			}
			if (inBoxes != null) {
				compteMailToUpdate.setInBoxes(inBoxes);
			}
			if (reportEmails != null) {
				compteMailToUpdate.setRapportEmails(reportEmails);
			}
			compteMailToUpdate.setLastUpdateAuthor(lastUpdateAuthor);
			compteMailToUpdate.setLastUpdateDate(new Date());

			// 4 - Création du nouveau fichier qui écrasera le fichier existant
			filesService.createCompteMailFile(compteMailToUpdate);

			return compteMailToUpdate;
		} else {
			throw new NotFoundException("Le compte mail " + email + " n'existe pas");
		}
	}

	/**
	 * Supprime le compte mail existant
	 * 
	 * @param email
	 *            l'email du compte mail
	 * @throws CimutConfException
	 * @throws NotFoundException
	 */
	public void deleteCompteMail(String email) throws CimutConfException, NotFoundException {
		if (filesService.isCompteMailFileExist(email)) {
			filesService.deleteCompteMailFile(email);
		} else {
			throw new NotFoundException("Le compte mail " + email + " n'existe pas");
		}
	}
}