package fr.cimut.ged.entrant.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.mail.oauth.MailOAuthConfiguration;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.multicanal.web.client.beans.*;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;

import javax.mail.*;
import java.util.*;

public class CompteMailManager {

	private static final Logger LOGGER = Logger.getLogger(CompteMailManager.class);

	private boolean expunge = false;

	private final Store store;
	private final boolean debug;
	private final CompteMail compte;
	private final Map<String, Map<MailFolderType, Folder>> foldersMap;

	/**
	 * Constructeur Réalise la connection a la boite mail distance
	 * 
	 * @param compteMail
	 * @throws CimutMailException
	 */

	public CompteMailManager(CompteMail compteMail) throws CimutMailException {
		this(compteMail, true);
	}

	/**
	 * Constructeur Réalise la connection a la boite mail distance
	 * 
	 * @param compteMail
	 * @param debug
	 *            mode debug paramétrable
	 * @throws CimutMailException
	 */
	public CompteMailManager(CompteMail compteMail, boolean debug) throws CimutMailException {
		this.debug = debug;
		this.compte = compteMail;

		MailOAuthConfiguration authProvider = this.compte.getAuthProvider();

		Session session;
		if (authProvider.getEnabled()) {
			session = Session.getInstance(this.compte.getPropertiesForOAuth());
		} else {
			session = Session.getDefaultInstance(this.compte.getProperties());
		}

		if (debug) {
			session.setDebug(true);
			session.setDebugOut(System.out);
			LOGGER.debug("safe => " + session.getProperties().get("mail.imaps.ssl.trust"));
		}

		String token = null;
		if (authProvider.getEnabled()) {
			try {
				token = OAuthClient.getToken(compteMail.getCmroc(), compteMail.getLogin(), compte.getPassword());
			} catch (Exception e) {
				LOGGER.error("error => ", e);
				throw new CimutMailException("Auth Azure KO au compte " + compte.getEmail(), e);
			}
		}

		try {
			store = session.getStore(this.compte.getProtocole());
		} catch (NoSuchProviderException e) {
			throw new CimutMailException("Connexion KO au compte " + compte.getEmail() + " avec le protocole : " + this.compte.getProtocole(), e);
		}
		try {
			String pwd = authProvider.getEnabled() ? token : compte.getPassword();

			store.connect(compte.getHost(), compte.getPort(), compte.getLogin(), pwd);
		} catch (MessagingException e) {
			throw new CimutMailException("Impossible de se connecter au compte " + compte.getEmail() + ", " + e.getMessage());
		}

		// Initialise la map de tous les répertoires de traitements de mails
		foldersMap = new HashMap<String, Map<MailFolderType, Folder>>();
		for (String inBox : compte.getInBoxes()) {
			foldersMap.put(inBox, new HashMap<MailFolderType, Folder>());
		}
	}

	private Store getStore() throws MessagingException {
		if (store == null || !store.isConnected()) {
			throw new MessagingException("la boite mail n'est pas connecter ou deconnecté");
		}
		return store;
	}

	private Folder retieveOrCreateFolder(Folder folder, String folderName) throws MessagingException {
		try {
			folder = getStore().getFolder(folderName);
		} catch (Exception e) {
			LOGGER.warn("probleme de récupération du répertoire : " + folderName);
		}
		if (!folder.exists()) {
			folder.create(Folder.READ_WRITE);
			folder = getStore().getFolder(folderName);
		}
		return folder;
	}

	private Folder getFolder(String inBoxFolderName, MailFolderType folderType) throws MessagingException {
		Folder folder = foldersMap.get(inBoxFolderName).get(folderType);
		if (folder == null) {
			folder = retieveOrCreateFolder(folder, folderType.getFolderName(inBoxFolderName));
			foldersMap.get(inBoxFolderName).put(folderType, folder);
			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
		}
		return folder;
	}

	private void closeFolder(Folder folder) {
		if (folder != null) {
			try {
				if (folder.isOpen()) {
					folder.close(this.expunge);
				}
			} catch (MessagingException e1) {
				LOGGER.error("Erreur lors de la fermeture du repertoire " + folder.getFullName() + " pour le compte mail : " + compte.getEmail());
			}
		}
	}

	/**
	 * Ferme tous les répertoires puis la boite mail
	 */
	public void disconnect() {
		for (Map<MailFolderType, Folder> inBoxFoldersMap : foldersMap.values()) {
			for (Folder folder : inBoxFoldersMap.values()) {
				closeFolder(folder);
			}
		}
		if (store != null && store.isConnected()) {
			try {
				store.close();
			} catch (MessagingException e) {
				LOGGER.error("Erreur lors de la fermeture du compte mail : " + compte.getEmail());
			}
		} else {
			LOGGER.warn("store is null or already disconnected : " + compte.getEmail());
		}
	}

	/**
	 * recupere la liste des messages
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public List<Message> getMessages(String inBoxFolderName, MailFolderType folderType) throws MessagingException {
		Folder folder = getFolder(inBoxFolderName, folderType);
		return Arrays.asList(folder.getMessages());
	}

	/**
	 * Déplace les mails traités issus du répertoire principal vers un répertoire secondaire (BACKUP, ERROR ou SPAM)
	 * 
	 * @param inBoxFolderName
	 * @param message
	 * @param destFolderType
	 * @throws MessagingException
	 */
	public void moveProcessedMessage(String inBoxFolderName, Message message, MailFolderType destFolderType) throws MessagingException {
		Folder mainFolder = getFolder(inBoxFolderName, MailFolderType.INBOX);
		Folder destFolder = getFolder(inBoxFolderName, destFolderType);
		mainFolder.copyMessages(new Message[] { message }, destFolder);
		message.setFlag(Flags.Flag.DELETED, true);
	}

	public void listFolder() throws MessagingException {
		Folder[] listFolder = getStore().getDefaultFolder().list();
		for (Folder folder : listFolder) {
			LOGGER.info(folder.getFullName());
		}
	}

	public boolean hasAccuseReception() {
		if (compte.getAccusReceptionTemplateId() == null || compte.getAccusReceptionTemplateId().isEmpty()
				|| compte.getAccusReceptionExpediteur() == null || compte.getAccusReceptionExpediteur().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Envoie l'accusé de reception si configurer pour
	 * 
	 * @param sender
	 * @param daId
	 */

	public void sendAccuseReception(String sender, String daId) {

		if (!hasAccuseReception()) {
			return;
		}

		Map<String, String> map = new HashMap<String, String>();
		List<Destinataire> destList = new ArrayList<Destinataire>();

		Map<String, String> variables = new HashMap<String, String>();
		variables.put("sude_id", daId);

		Emetteur emit = new Emetteur(compte.getCmroc(), compte.getCmroc(), "", compte.getAccusReceptionExpediteur().toLowerCase(), "ORGANISME");
		emit.setModeDistribution("INTERNE");

		Destinataire dest = new Destinataire(sender.toLowerCase(), map, "", "", "COURTIER");
		dest.setVariables(variables);
		destList.add(dest);

		Mail mail = new Mail(compte.getAccusReceptionTemplateId(), new HashSet<PieceJointe>());

		Job job = new Job();
		job.setDestinataires(destList);
		job.setEmetteur(emit);
		job.setMail(mail);

		try {
			ObjectMapper mapper = new ObjectMapper();
			String stringigyJob = "";
			stringigyJob = mapper.writeValueAsString(job);
			byte ptext[] = stringigyJob.getBytes();
			stringigyJob = new String(ptext, "UTF-8");

			ClientRequest request = new ClientRequest(GlobalVariable.getMulticanalUrl());
			request.accept("application/json");
			request.body("application/json", stringigyJob);
			int status = request.post().getStatus();
			if (status != 201) {
				throw new CimutDocumentException("code statut : " + status);
			}
		} catch (Exception e) {
			// je ne veux pas planté l'integration reussi du mail a cause d'une erreur ici.
			// je ne bloque pas mais j'envoie un rapport d'erreur
			LOGGER.fatal("Impossible d'envoyer l'accusé de récéption à l'email suivant : " + sender, e);
		}
	}

	/**
	 * efface les mail avec le flag a delete lors de la fermeture du reprtoire
	 * 
	 * @param expunge
	 */
	public void setxpunge(boolean expunge) {
		this.expunge = expunge;
	}

	public CompteMail getCompteMail() {
		return compte;
	}
}