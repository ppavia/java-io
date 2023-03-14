package fr.cimut.ged.entrant.mail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.util.BASE64DecoderStream;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.FileHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.multicanal.web.client.beans.Destinataire;
import fr.cimut.multicanal.web.client.beans.Emetteur;
import fr.cimut.multicanal.web.client.beans.Job;
import fr.cimut.multicanal.web.client.beans.Mail;
import fr.cimut.multicanal.web.client.beans.PieceJointe;

/**
 * Class util pour recuperer les infos contenu d'un email
 * 
 * @author gyclon
 *
 */
public class MailerHelper {

	/**
	 * date de dernière intégration de mail - pour la supervision pooler
	 */
	public static Long dateDerniereIntegrationMail = new Date().getTime();

	/**
	 * le loggeur
	 */
	private static final Logger LOGGER = Logger.getLogger(MailerHelper.class);

	//http://www.w3.org/Protocols/rfc1341/7_1_Text.html
	/**
	 * Le default charset pris de la machine !
	 */
	private static final String DEFAULT_CHARSET = System.getProperty("file.encoding");//"US-ASCII";

	/**
	 * methode recuperant le corps du mail
	 * 
	 * @param multipart
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws CimutMailException
	 */
	private static Map<String, String> getMessageContent(Multipart multipart) throws MessagingException, IOException, CimutMailException {

		//Double d = 100 * Math.random();
		//int id = d.intValue();
		//
		//System.out.println("#######################");
		//System.out.println("getMessageContent   " + id);
		//System.out.println(multipart.getContentType());
		//System.out.println("#######################");

		boolean firstPlainPartIsFound = false;
		boolean firstHtmlPartIsFound = false;

		Map<String, String> contents = new HashMap<String, String>();
		Map<String, String> inline = new HashMap<String, String>();

		for (int j = 0; j < multipart.getCount(); j++) {

			// on recupere les données interresante du header !
			BodyPart bodyPart = multipart.getBodyPart(j);
			String disposition = bodyPart.getDisposition();

			//System.out.println( " (" + j + ") " + bodyPart.getContentType() + " " + firstHtmlPartIsFound+" "+disposition);

			inline.putAll(getInline(bodyPart));

			//################################
			// skip phase 
			//################################

			// attention, j'ai des cas d'email dont le corps text/html est en disposition inline !!!!
			if (firstHtmlPartIsFound) {
				// je sort de la, vu que j'ai deja gerer le inline, et que le attachment, je ne le gere pas ici
				// pas la peine de continuer, vu que j'ai ce que je recherche (le corps html du mail).
				continue;
			}

			if (bodyPart.isMimeType("message/delivery-status")) {
				throw new CimutMailException("Je ne gere pas les message/delivery-status");
			}
			// on gere le charset ici comme l'on peut
			String charset = getCharset(bodyPart.getContentType());
			// suivant le typemime, on gere ca differement
			if (bodyPart.isMimeType("message/rfc822")) {
				// c'est un cas qui doit pouvoir exister. je le traite donc ...
				Message nestedMsg = (Message) bodyPart.getContent();
				contents = getMessage(nestedMsg);
			} else if (bodyPart.isMimeType("multipart/alternative")) {
				// most encore un multipart/* inception lvl X
				Multipart mPart = (Multipart) bodyPart.getContent();
				contents = getMessageContent(mPart);
				if (contents.containsKey("content")) {
					firstHtmlPartIsFound = true;
				}
			} else if (bodyPart.isMimeType("multipart/related")) {
				// most encore un multipart/* inception lvl X
				// j'ai un cas ou il y a un null pointer exception ici ...
				Multipart mPart = (Multipart) bodyPart.getContent();
				contents = getMessageContent(mPart);
				if (contents.containsKey("content")) {
					firstHtmlPartIsFound = true;
				}
			} else if (bodyPart.isMimeType("multipart/*")) {
				// most encore un multipart. inception lvl X
				Multipart mPart = (Multipart) bodyPart.getContent();
				contents = getMessageContent(mPart);
				if (contents.containsKey("content")) {
					firstHtmlPartIsFound = true;
				}
			} else if (bodyPart.isMimeType("text/plain")) {
				if (!firstPlainPartIsFound) {
					contents.put("content", bodyPart.getContent().toString());
					contents.put("charset", charset);
					contents.put("mime", "text/plain");
					firstPlainPartIsFound = true;
				}
			} else if (bodyPart.isMimeType("text/html")) {
				// most common case
				contents.put("content", bodyPart.getContent().toString());
				contents.put("charset", charset);
				// ok, je triche un peu ici. je ne veux pas revoir ces inline en pieces jointes vu que je les ai ajouter dans le body ...
				// donc je force sa disposition a null ...
				firstHtmlPartIsFound = true;
				contents.put("mime", "text/html");
				//break; // no need, cause i want the inline with
			}
		}

		// ok, j'ai des images embedded, et un corps en html. je remplace donc les images avec du embedded base64.
		if (firstHtmlPartIsFound && !inline.isEmpty()) {
			String content = contents.get("content");
			for (String contentId : inline.keySet()) {
				content = Pattern.compile("cid:" + contentId, Pattern.DOTALL).matcher(content).replaceAll(inline.get(contentId));
			}
			contents.put("content", content);
		}
		return contents;
	}

	/**
	 * Recupere les pieces jointes, enregistre sur le syteme de fichier
	 * 
	 * @param message
	 * @param cmroc
	 * @return
	 * @throws CimutMailException
	 * @throws CimutConfException
	 * @throws CimutMetierException
	 * @throws CimutFileException
	 */
	public static List<Document> getPiecesJointes(Message message, String cmroc)
			throws CimutMailException, CimutConfException, CimutMetierException, CimutFileException {

		List<Document> output = new ArrayList<Document>();
		try {
			// recupere les pieces jointes
			for (Document doc : getAttachment(message, cmroc)) {
				// verifie l'existence du fichier dans le plan de classement gede (throw 404)
				DocumentHelper.getFile(doc);
				output.add(doc);
			}
		} catch (MessagingException e) {
			throw new CimutMailException("Impossible de recuperer le contenu du mail ", e);
		} catch (UnsupportedEncodingException e) {
			throw new CimutMailException("Impossible de recuperer le contenu du mail ", e);
		} catch (IOException e) {
			throw new CimutMailException("Impossible de recuperer le contenu des pieces jointes du mail ", e);
		}
		return output;
	}

	/**
	 * recupere les envoyeurs
	 * 
	 * @param message
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static String getFromTo(Message message) throws UnsupportedEncodingException, MessagingException {
		Address[] froms = message.getFrom();
		if (froms != null && froms.length > 0 && froms[0] != null) {
			return formatMailAddr(MimeUtility.decodeText(froms[0].toString()));
		} else {
			return "";
		}
	}

	/**
	 * Recupere les destinataires
	 * 
	 * @param message
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static String getToList(Message message) throws UnsupportedEncodingException, MessagingException {
		return formatMailAddr(parseAddresses(message.getRecipients(RecipientType.TO)));
	}

	private static List<Document> getFromMultipart(Multipart multipart, String cmroc)
			throws MessagingException, IOException, CimutMailException, CimutFileException, CimutConfException {
		ArrayList<Document> output = new ArrayList<Document>();

		// boucle sur les parts
		for (int j = 0; j < multipart.getCount(); j++) {

			// instancie notre nouveau document

			BodyPart bodyPart = multipart.getBodyPart(j);
			String disposition = bodyPart.getDisposition();

			//System.out.println(bodyPart.getHeader("Content-ID"));
			//System.out.println(bodyPart.getContent());

			if (bodyPart.getContent() instanceof Multipart) {
				output.addAll(getFromMultipart((Multipart) bodyPart.getContent(), cmroc));
				continue;
			} else {

				// Bon, verifier que la disposition == attachemnt n'est pas suffisant, il y en a en Inline ... 
				// en particulier ceux qui n'ont pas de Content-ID (donc pas injectable dans le HTML)
				// mais pas le text/html vu que je l'ai surement pris en compte dans le body ...
				if (bodyPart != null && disposition != null
						&& (disposition.equalsIgnoreCase("ATTACHMENT")
								|| (bodyPart.getHeader("Content-ID") == null && "INLINE".equalsIgnoreCase(disposition)
										&& !bodyPart.isMimeType("text/plain") && !bodyPart.isMimeType("text/html")))) {

					String filename = "";
					// bon, evidemment, y'en a qui ont reussi à me mettre un mail en piece jointe ...
					if (bodyPart.isMimeType("message/rfc822")) {
						Message tmpMessage = (Message) bodyPart.getContent();
						String subject = tmpMessage.getSubject();
						if (subject == null || subject.isEmpty()) {
							subject = "aucun sujet";
						}
						filename = subject + ".eml";
					} else {
						// que faire si bodyPart.getFileName() est null ?
						// dans ce cas, je prefere que ca plante ici ...

						// cas non gerer comme il se doit par la lib ...
						// je fais ca à la main ...
						String contentType = null;
						try {
							contentType = bodyPart.getContentType();
						} catch (MessagingException e2) {
							throw new CimutMailException("impossible de recuperer le content-Type");
						}

						filename = getFileName(contentType, bodyPart.getFileName());
					}

					Document document = setDocument(filename, cmroc);
					String path = DocumentHelper.getPlanDeClassement(document);
					path += "/" + document.getId();
					// sauvegarde notre fichier dans le plan de classement de la ged
					((MimeBodyPart) bodyPart).saveFile(path);
					LOGGER.info("file saved into " + path);
					// on ajout notre document a la liste
					output.add(document);
				}
			}
		}
		return output;
	}

	/**
	 * recupere une list de document depuis les pieces jointes du mail (les fichiers sont dans le plan de classement de
	 * la GEDE apres cette etape)
	 * 
	 * @param message
	 * @param cmroc
	 * @return
	 * @throws CimutMailException
	 * @throws IOException
	 * @throws MessagingException
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */
	private static List<Document> getAttachment(Message message, String cmroc)
			throws CimutMailException, IOException, MessagingException, CimutFileException, CimutConfException {

		// instanie notre tableau
		ArrayList<Document> output = new ArrayList<Document>();

		// recuperation du contenu du mail
		Object content = message.getContent();

		if (content instanceof Multipart) {

			output.addAll(getFromMultipart((Multipart) content, cmroc));

		} else if (content instanceof InputStream) {

			String filename = getFileName(message.getContentType(), message.getFileName());
			Document document = setDocument(filename, cmroc);
			String path = DocumentHelper.getPlanDeClassement(document);
			path += "/" + document.getId();
			// sauvegarde notre fichier dans le plan de classement de la ged
			FileOutputStream out = null;
			InputStream inStream = null;
			try {
				out = new FileOutputStream(path);
				inStream = (InputStream) content;
				int ch;
				while ((ch = inStream.read()) != -1) {
					out.write(ch);
				}
			} finally {
				if (out != null)
					out.close();
				if (inStream != null)
					inStream.close();
			}
			LOGGER.info("file saved into " + path);
			// on ajout notre document a la liste
			output.add(document);

		}
		return output;
	}

	private static Document setDocument(String filename, String cmroc) throws CimutFileException, CimutConfException {
		Document document = new Document();
		// libelle dans EDDM (le nom du fichier)
		document.setLibelle(filename);

		String extension = "";
		try {
			extension = FileHelper.getExtension(filename);
		} catch (CimutFileException e) {
			LOGGER.error("impossible de determine l'extension dupuis le nom de fichier suivant : " + filename);
			extension = ".unk";
		}

		// set les attr necesaire a l'obtetion du plan de classement
		document.setDtcreate(new Date());
		// recuperation du plan de classement
		document.setCmroc(cmroc);
		// set le nouveau nom du fichier
		document.setId(DocumentHelper.generateSudeNewId(extension));
		document.setJson(new Json());
		return document;
	}

	/**
	 * recupere le contenu du message (content et charset)
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 * @throws CimutMailException
	 */
	public static Map<String, String> getMessage(Message message) throws MessagingException, IOException, CimutMailException {

		Map<String, String> outputMap = new HashMap<String, String>();

		try {

			Object content = message.getContent();

			if (content instanceof Multipart) {
				Multipart multipart = (Multipart) content;
				outputMap = getMessageContent(multipart);
			} else if (content instanceof String) {
				outputMap.put("charset", getCharset(message.getContentType()));
				outputMap.put("content", content.toString());
			}
		} catch (Exception ex) {
			throw new CimutMailException("impossible de recuperer le contenu de l'email", ex);
		}
		return outputMap;
	}

	/**
	 * recupere le charset (par defaut si rien)
	 * 
	 * @param contentType
	 * @return
	 */
	private static String getCharset(String contentType) {
		String charset = DEFAULT_CHARSET;
		try {
			if (contentType != null && contentType.toUpperCase().contains("CHARSET=")) {
				charset = Pattern.compile("^.*CHARSET=([^;]+).*$", Pattern.DOTALL).matcher(contentType.toUpperCase()).replaceAll("$1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return charset.trim();
	}

	/**
	 * Recupere le contenu du message en mode trext sur 500 charctere max
	 * 
	 * @param messageContent
	 * @param sender
	 * @param subject
	 * @return
	 */
	public static String getStrippedContent(String messageContent, String sender, String subject) {

		if (messageContent == null) {
			messageContent = "";
		}

		String content = "";

		org.jsoup.nodes.Document document = Jsoup.parse(messageContent);
		document.outputSettings(new org.jsoup.nodes.Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
		// on tente de preserver les sauts de ligne comme on peux ...
		document.select("br").append("\\n");
		document.select("p").prepend("\\n\\n");
		String s = document.html().replaceAll("\\\\n", "\n");
		// on strip le html
		content = Jsoup.clean(s, "", Whitelist.none(), new org.jsoup.nodes.Document.OutputSettings().prettyPrint(false));
		// trop d'espace du au html
		content = content.replaceAll("\\s+\\n", "<br>");
		content = content.replaceAll("\\s+", " ");
		content = content.replaceAll("\\u20AC", "&euro;");
		content = content.replaceAll("[\\u2018|\\u2019|\\u201A|\\u0060|\\u0091|\\u0092]", "'");
		content = content.replaceAll("[\\u201C|\\u201D|\\u201E|\\u00BB|\\u00AB|\\u0093|\\u0094]", "\"");
		content = content.replaceAll("\\u2026|\u0085", "...");
		content = content.replaceAll("[\u2013|\u2014]", "-");
		content = content.replaceAll("[\u02DC|\u00A0]", " ");
		content = content.replaceAll("[\u2020|\u2022|\u00B7]", ".");

		// on insert l email de l'expediteur

		//?subject=Re: "+subject+" // peut etre le sujet avec
		content = "Expediteur : " + "<a href=\"mailto:" + sender + "\">" + sender + "</a>" + "<br>" + content;
		return content;
	}

	/**
	 * recupere les adresses email sous forme de string (separator , )
	 * 
	 * @param address
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String parseAddresses(Address[] address) throws UnsupportedEncodingException {
		/* Passage des addresses en List */
		String listAddress = "";
		if (address != null) {
			for (int i = 0; i < address.length; i++) {
				listAddress += MimeUtility.decodeText(address[i].toString()) + ", ";
			}
		}
		if (listAddress.length() > 1) {
			listAddress = listAddress.substring(0, listAddress.length() - 2);
		}
		return listAddress;
	}

	private final static String ENCODED_PART_REGEX_PATTERN = "=\\?([^?]+)\\?([^?]+)\\?([^?]+)\\?=";

	private static String decode(String s) {
		Pattern pattern = Pattern.compile(ENCODED_PART_REGEX_PATTERN);

		Matcher m = pattern.matcher(s);

		ArrayList<String> encodedParts = new ArrayList<String>();

		while (m.find()) {
			encodedParts.add(m.group(0));
		}

		if (encodedParts.size() > 0) {
			try {
				for (String encoded : encodedParts) {
					s = s.replace(encoded, MimeUtility.decodeText(encoded));
				}
				return s.replaceAll("\t", "");
			} catch (Exception ex) {
				return s;
			}
		} else
			return s;

	}

	private static String extractTypeMime(String contentType) {
		String typeMime = "";
		Pattern regexTypeMime = Pattern.compile("^([^;]+)", Pattern.DOTALL);
		Matcher regexTypeMimeMatcher = regexTypeMime.matcher(contentType);

		if (regexTypeMimeMatcher.find()) {
			typeMime = regexTypeMimeMatcher.group(1);
		}
		return typeMime;
	}

	/**
	 * Recupere le filename de la pieces jointe|inline
	 * 
	 * @param contentType
	 * @param filename
	 * @return
	 * @throws CimutMailException
	 */
	public static String getFileName(String contentType, String filename) throws CimutMailException {

		String typeMime = "";

		if (contentType != null) {
			// probleme de librarie, ne gere pas ce cas ci ...
			Pattern regex = Pattern.compile("name=\"([^\"]+)\"", Pattern.DOTALL);
			Matcher regexMatcher;
			regexMatcher = regex.matcher(contentType);

			typeMime = extractTypeMime(contentType);

			if (regexMatcher.find()) {
				filename = decode(regexMatcher.group(1));
			} else if (filename != null) {
				filename = decode(filename);
			}

			if (filename == null || filename.isEmpty()) {
				// je ne trouve rien ... et j'ai un null ici ...
				// je set un filename par defaut pour ne pas etre bloquant ...
				filename = "ATTR" + FileHelper.getExtensionFromMime(contentType.toLowerCase());
			}
		} else {
			// je ne trouve rien ... et j'ai un null ici ...
			// je set un filename par defaut pour ne pas etre bloquant ...
			if (filename == null || filename.isEmpty()) {
				filename = "ATTR.unk";
				//throw new CimutMailException("impossible de recuperer le nom de la piece jointe [null]");
			}
			filename = decode(filename);
		}

		// patch pour recuperer l'extention qui convient au mime type
		if (!typeMime.equals("") && !filename.endsWith(".unk") && filename.indexOf(".") <= 0) {
			filename += FileHelper.getExtensionFromMime(typeMime);
		}
		return filename;
	}

	private static Map<String, String> getInline(BodyPart bodyPart)
			throws CimutMailException, MessagingException, UnsupportedEncodingException, IOException {
		//##########################################
		// INLINE
		//##########################################
		String[] contentIds = bodyPart.getHeader("Content-ID");
		String contentId = "";

		Map<String, String> inline = new HashMap<String, String>();

		//System.out.println(bodyPart.getContentType() + " " + bodyPart.getFileName());

		// c'est de cette facon que je gere les inlines. (certain client mail ne specifie pas le disposition = INLINE !!!!)
		if (contentIds != null && contentIds.length == 1) {

			contentId = contentIds[0];
			String filename = getFileName(bodyPart.getContentType(), bodyPart.getFileName());

			try {

				// certain client mail ne fournissent pas le bon mimeTYPE. je le recalcule donc ...
				// vu que je gere la plus part des type mime depuis l'extension
				// attention cependant a bien gerer le fileName (parfois un peu compliquer)
				String typeMime = "";
				// plein de probleme ici :
				//     il arrive que le type mime fournit par le client ne soit pas bon.
				//     je me base donc sur l'extrension du nom de l'image
				//     il arrive que ce nom soit null.
				//     du coup, je remet le type mime,
				//     mais si null aussi, je set par default le type mime a "application/octet-stream";
				if ("ATTR.unk".equals(filename)) {
					try {
						typeMime = bodyPart.getContentType();
					} catch (Exception e) {
						typeMime = "application/octet-stream";
					}
				} else {
					typeMime = FileHelper.getTypeMime(filename);
				}

				// en gros, je ne gere pas les trucs autre que base64 encoded !!!!
				if (bodyPart.getContent() instanceof BASE64DecoderStream) {
					// c'est bete, mais c'est comme cela : base64 => bytes => base64 ... 
					BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) bodyPart.getContent();
					byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
					byte[] encodeBase64 = Base64.encodeBase64(byteArray);
					// je met ca en memoire. j'espere que ca va pas monter trop haut ...
					typeMime = extractTypeMime(typeMime);
					inline.put(contentId.replaceAll("[<>]", ""), "data:" + typeMime + ";base64," + new String(encodeBase64, "UTF-8"));
				} else {
					LOGGER.warn("impossible de recuperer l'image en inline [n'est pas base64 !!]");
				}
			} catch (CimutFileException e) {
				LOGGER.warn("impossible de recuperer l'image en inline ");
			}
		}
		return inline;
	}

	/**
	 * recupere la date, renvoie la date du jour si probleme (ne devrait pas arriver)
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	public static DateTime getDate(Message message) throws MessagingException {
		// pas encore de nullPointerException
		if (message.getReceivedDate() != null) {
			return new DateTime(message.getReceivedDate().getTime());
		} else {
			// je ne veux pas etre bloquant le dessus.
			// je prends la date 
			return new DateTime();
		}
	}

	/**
	 * Format les adresses email
	 * 
	 * @param addr
	 * @return
	 */
	private static String formatMailAddr(String addr) {
		String output = "";
		Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{1,10}\\b", Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(addr);
		Set<String> emails = new HashSet<String>();
		while (matcher.find()) {
			emails.add(matcher.group());
		}
		for (String string : emails) {
			output = string.toUpperCase() + ", ";
		}
		if (!output.isEmpty()) {
			output = output.substring(0, output.length() - 2);
		}
		return output;
	}

	/**
	 * 
	 * @param cmroc
	 * @param destinataires
	 * @param message
	 * @param boiteMail
	 * @throws CimutMailException
	 */
	public static void sendMail(String cmroc, List<String> destinataires, String text, String html, String boiteMail) throws CimutMailException {

		try {
			Emetteur emit = new Emetteur(cmroc, cmroc, cmroc, "nepasrepondre@cimut.fr", "ORGANISME");
			emit.setModeDistribution("INTERNE");

			Map<String, String> map = new HashMap<String, String>();
			List<Destinataire> destList = new ArrayList<Destinataire>();

			for (String destinataire : destinataires) {
				Destinataire dest = new Destinataire(destinataire, map, "", cmroc, "ORGANISME");
				destList.add(dest);
			}

			Job job = new Job();
			job.setDestinataires(destList);
			job.setEmetteur(emit);
			Set<PieceJointe> piecesJointes = new HashSet<PieceJointe>();

			//System.out.println("subj : " + GlobalVariable.getMailEntrantSujetMessage() + " " + boiteMail);
			//System.out.println("text : " + text);
			//System.out.println("html : " + html);
			Mail mail = new Mail(GlobalVariable.getMailEntrantSujetMessage() + " " + boiteMail, text, html, piecesJointes);
			job.setMail(mail);

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
				throw new CimutDocumentException("Fail to send email, code statut : " + status + " email: " + boiteMail);
			}
		} catch (Exception e) {
			throw new CimutMailException(e);
		}
	}

	/**
	 * Recupere le sujet du mail
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	public static String getSubject(Message message) throws MessagingException {

		String subject = DocumentHelper.sanitize(message.getSubject());
		if (StringUtils.isBlank(subject)) {
			subject = "Aucun sujet";
		}
		return subject;
	}

}
