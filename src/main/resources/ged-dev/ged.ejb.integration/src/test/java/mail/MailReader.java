package mail;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import fr.cimut.ged.entrant.utils.GedeIdHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.cimut.ged.entrant.appelP360.P360Manager;
import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.beans.p360.ReponseSearch360Agregee;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.mail.CompteMailManager;
import fr.cimut.ged.entrant.mail.MailFolderType;
import fr.cimut.ged.entrant.mail.MailerHelper;
import fr.cimut.ged.entrant.service.CompteMailFileService;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class MailReader {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "C:/SVN/getEntrante/fr.cimut.ged.entrant.serviceIntegration/trunk/src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.environnement", "DEV");
		//props.setProperty("fr.cimut.ged.entrante.environnement", "MAINT");

		props.setProperty("javax.net.ssl.trustStore", "C:/Users/gyclon/workspace/DUMMYEMAIL/src/test/resources/myTrustStore.jks");
		props.setProperty("javax.net.ssl.trustStorePassword", "guitch");
		props.setProperty("javax.net.ssl.trustType", "JKS");
		props.setProperty("fr.cimut.ged.entrante.p360.url", "http://lcidxs1.cimut.fr:8502/meslCIP/Search360");
		props.setProperty("fr.cimut.ged.entrante.cmroc.file.path", "src/test/resources/complement_cmroc.csv");
		props.setProperty("fr.cimut.ged.entrante.destination.dir", "C:/tmp/");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.password", "quimper");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.user", "rootcimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.nom_database_collection", "documents");

		//props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");
		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcip-mongodb:27017,lcip-mongodb-01:27017,lcip-mongodb-02:27017");
		System.setProperties(props);

	}

	public void addDocsToDa(List<File> files, String cmroc, String reclamId) throws Exception {

		// parametres d'appel serveur
		String url = "http://lcidxs6.cimut.fr:8080/ged.web.interrogation-0.0.2/selligentUpload";
		//String url = "http://localhost:8080/ged.web.interrogation-0.0.2/selligentUpload";
		String username = "giveMeFive";
		String password = "giveMeNiNe";

		PostMethod filePost = null;

		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);

			List<Part> listParts = new ArrayList<Part>();
			listParts.add(new StringPart("cmroc", cmroc));
			listParts.add(new StringPart("reclamId", reclamId));
			for (File file : files) {
				listParts.add(new FilePart(file.getName(), file));
			}

			Part[] parts = new Part[listParts.size()];
			parts = listParts.toArray(parts);

			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int responseCode = httpclient.executeMethod(filePost);
			if (responseCode != 200) {
				System.out.println("Une erreur est survenue lors du transfert des fichiers: \n\t\t" + filePost.getResponseBodyAsString());
				throw new Exception("Une erreur est survenue lors du transfert des fichiers: \n\t\t" + filePost.getResponseBodyAsString());
			}

			// recuperation des infos !
			String[] eddocIds = filePost.getResponseBodyAsString().split(",");
			for (String eddocId : eddocIds) {
				System.out.println(eddocId);
			}
		} finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
		}
	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

	//@Test
	@SuppressWarnings("unused")
	public void attachementToEddm() {
		InputStream is = null;
		try {

			EddmManager eddmManager = new EddmManager();

			is = new FileInputStream("src/test/resources/mails/normal_6.eml");
			List<Document> documents = MailerHelper.getPiecesJointes(new MimeMessage(null, is), "9970");
			for (Document document : documents) {
				System.out.println(document.getLibelle());

				if (document.getLibelle().startsWith("Prestations")) {

					document.setLibelle(StringUtils.stripAccents(document.getLibelle()));

					//document.setLibelle(document.getLibelle().replaceAll("[\\(\\)àô]", ""));
					System.out.println(document.getLibelle());
					document.setTypeDocument(GlobalVariable.TYPE_PARTENAIRE);
					ObjectMapper mapper = new ObjectMapper();
					ObjectNode jsonColumn = mapper.createObjectNode();
					jsonColumn.put(GlobalVariable.ATTR_ID_PROF, "290000017|ETAB|0000");
					//jsonColumn.put(GlobalVariable.ATTR_ID_ENTREPRISE, "0001|Q|00");
					//jsonColumn.put(GlobalVariable.ATTR_CLASSE_ENTRPRISE, "S");
					// jsonColumn.put(GlobalVariable.ATTR_NUM_ADHERENT,"427760");
					jsonColumn.put(GlobalVariable.ATTR_TYPE_DOSSIER, GlobalVariable.TYPE_PARTENAIRE + " - test Unitaire (jUnit)");
					jsonColumn.put(GlobalVariable.ATTR_ID_ORGANISME, "9970");
					jsonColumn.put(GlobalVariable.ATTR_TUTELLE, "9970");
					jsonColumn.put(GlobalVariable.ATTR_TYPEDOC, document.getTypeDocument());
					jsonColumn.put("EDDM_LIBELLE", document.getLibelle());
					Json json = new Json();
					json.setData(mapper.writeValueAsString(jsonColumn));
					document.setJson(json);
					fr.cimut.ged.entrant.beans.appelmetier.Eddm eddm = eddmManager.create(document, new Metier(), envirTeste);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void getMailFromFile() {

		InputStream is = null;

		try {
			// cas ou piece jointe inline dans text/plain
			is = new FileInputStream("src/test/resources/mails/CimutFailedUnknown_1.eml");
			Message message = new MimeMessage(null, is);

			System.out.println(MailerHelper.getFromTo(message));

			P360Manager managerP360 = new P360Manager();
			ReponseSearch360Agregee response = managerP360.get(MailerHelper.getFromTo(message), "9970");

			Map<String, String> content = MailerHelper.getMessage(message);
			if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
				fail();
			} else {
				System.out.println(content.get("content"));
			}

			//			
			List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
			//						if (documents.size() != 1) {
			//							fail();
			//						}
			for (Document document : documents) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain
			is = new FileInputStream("src/test/resources/mails/CimutFailedParseExpediteur_1.eml");
			Message message = new MimeMessage(null, is);

			System.out.println(MailerHelper.getFromTo(message));

			P360Manager managerP360 = new P360Manager();
			ReponseSearch360Agregee response = managerP360.get(MailerHelper.getFromTo(message), "9970");
			System.out.println("message : " + response.getMessage());

			List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
			if (documents.size() != 1) {
				fail();
			}
			for (Document document : documents) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain
			is = new FileInputStream("src/test/resources/mails/test_inlineOnly.eml");
			Message message = new MimeMessage(null, is);
			List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
			if (documents.size() != 1) {
				fail();
			}
			for (Document document : documents) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain
			is = new FileInputStream("src/test/resources/mails/test_inlineAsBody.eml");
			Message message = new MimeMessage(null, is);
			Map<String, String> content = MailerHelper.getMessage(message);
			if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
				fail();
			}
			for (Document document : MailerHelper.getPiecesJointes(message, "9970")) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain
			is = new FileInputStream("src/test/resources/mails/test_htmlPlainAsInline.eml");
			Message message = new MimeMessage(null, is);
			Map<String, String> content = MailerHelper.getMessage(message);
			if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
				fail();
			}
			List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
			for (Document document : documents) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			is = new FileInputStream("src/test/resources/mails/test_PiecesJointesSansExtension.eml");
			Message message = new MimeMessage(null, is);
			Map<String, String> content = MailerHelper.getMessage(message);
			if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
				fail();
			}
			List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
			for (Document document : documents) {
				System.out.println(document.getId() + " " + document.getLibelle());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain
			for (int i = 1; i < 7; i++) {
				is = new FileInputStream("src/test/resources/mails/CimutFailedSended_" + i + ".eml");
				Message message = new MimeMessage(null, is);
				Map<String, String> content = null;
				try {

					content = MailerHelper.getMessage(message);
					if (content == null) {
						fail();
					}
					//System.out.println(content.get("content"));
				} catch (CimutMailException cmail) {
					continue;
				}
				if (i == 1) {
					fail(); // on doit jeter une exception ici
				}

				if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
					fail();
				}
				//System.out.println(content.get("content"));
				List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
				for (Document document : documents) {
					System.out.println(document.getId() + " " + document.getLibelle());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			for (int i = 1; i < 6; i++) {
				is = new FileInputStream("src/test/resources/mails/normal_" + i + ".eml");
				Message message = new MimeMessage(null, is);
				Map<String, String> content = null;
				content = MailerHelper.getMessage(message);
				if (content == null) {
					fail();
				}
				if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
					fail();
				}
				//System.out.println(content.get("content"));
				List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
				for (Document document : documents) {
					System.out.println(document.getId() + " " + document.getLibelle());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// cas ou piece jointe inline dans text/plain

			for (int i = 0; i < 6; i++) {
				is = new FileInputStream("src/test/resources/mails/test_" + i + ".eml");
				Message message = new MimeMessage(null, is);
				Map<String, String> content = MailerHelper.getMessage(message);
				if (!content.containsKey("content") || content.get("content") == null || content.get("content").isEmpty()) {
					fail();
				}
				//System.out.println(content.get("content"));
				List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
				for (Document document : documents) {
					System.out.println(i + " " + document.getId() + " " + document.getLibelle());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	//@Test
	public void getMailIntoFile() {
		CompteMailManager compteMailManager = null;
		String DefaultCharset = System.getProperty("file.encoding");
		System.out.println("default file encoding => " + DefaultCharset);
		try {

			List<CompteMail> listCompte = new CompteMailFileService().getAllComptesMailFromFiles();

			for (CompteMail compteMail : listCompte) {

				System.out.println(compteMail.getEmail());
				System.out.println(compteMail.getAccusReceptionExpediteur());
				System.out.println(compteMail.getAccusReceptionTemplateId());
				System.out.println(compteMail.getCmroc());
				System.out.println(compteMail.getEnvironnement());
				System.out.println(compteMail.getHost());
				System.out.println(compteMail.getInBoxes().toString());
				System.out.println(compteMail.getPassword());
				System.out.println(compteMail.getEmail());
				System.out.println(compteMail.getPort());
				System.out.println(compteMail.getProtocole());
				System.out.println(compteMail.getRapportEmails().toString());

				try {
					compteMailManager = new CompteMailManager(compteMail);
					for (String inBoxFolderName : compteMail.getInBoxes()) {
						List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.INBOX);
						//
						int counter = 0;
						for (Message message : messages) {
							System.out.println("(" + counter + ") => " + message.getSubject());
							//Map<String, String> content = MailerHelper.getMessage(message);
							//						System.out.println("CHARSET : "+content.get("charset"));
							//						System.out.println("CONTENT IS : "+content.get("content"));
							//						//
							counter++;
							//						//
							//						//						String daId = "test_"+counter;
							//						//						File folder = new File(daId);
							//						//						if (!folder.exists()){
							//						//							folder.mkdir();
							//						//						}
							//						//
							//						//						File newFile = new File(folder,"email.html");
							//						//						System.out.println("SAVING "+ newFile.getAbsolutePath());
							//						//
							//						//						Writer out = new BufferedWriter(
							//						//								new OutputStreamWriter(
							//						//										new FileOutputStream(newFile), "UTF-8"));
							//						//						try {
							//						//							out.write(content.get("content"));
							//						//						}catch (Exception e){
							//						//							e.printStackTrace();
							//						//						} finally {
							//						//							out.close();
							//						//						}
							//						//
							//						//						List<Document> documents = MailerHelper.getPiecesJointes(message, "9970");
							//						//						for (Document document : documents) {
							//						//							System.out.println(document.getId() + " " + document.getLibelle());
							//}
							//
							File file = new File("CimutFailedUnknown_" + counter + ".eml");
							FileOutputStream fop = null;
							try {
								fop = new FileOutputStream(file);
								message.writeTo(fop);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									if (fop != null) {
										fop.close();
									}
								} catch (Exception e1) {
								}
							}

							//break;
						}
					}
				} catch (MessagingException e) {
					throw new CimutMailException("Impossible de recuperer la list des dossier de : " + compteMail.getEmail(), e);
				} finally {
					compteMailManager.setxpunge(false);
					compteMailManager.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//@Test 
	@SuppressWarnings("unused")
	public void beanTest() {

		try {

			Document document = new Document();
			document = DocumentHelper.setDefaultValue(document, envirTeste);
			document.setDtcreate(new Date());
			document.setTypeDocument("PERSONNE - test unitaire (Junit)");

			fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = new fr.cimut.ged.entrant.beans.mongo.DocumentMongo();
			json.setTypeEntiteRattachement(TypeEntite.PERSONNE);
			json.setRegime("RO");
			json.setAssuInsee("2281548756987");
			json.setRang("01");
			json.setNumAdherent("01254874");
			json.setTutelle("8080");
			json.setCmroc("8080");
			json.setCodePostal("35700");
			json.setVille("Rennes");
			json.setNom("YCLON");
			json.setPrenom("Guillaume");

			document.setCmroc(json.getTutelle());

			// on instancie notre bean json (mongoDB + oracle)
			json.getAttributes().put(GlobalVariable.ATTR_DESTINATAIRE, "yclon.guillaume@gmail.com");
			json.getAttributes().put("sujet", "sujet du mail");
			json.getAttributes().put("expediteur", "guillaume.yclon@gmail.com");
			json.setCommentaire("Corp du mail en TEXT");
			json.setDtCreate(new DateTime());
			json.setDtIntegration(new DateTime());
			json.setTypeDocument(document.getTypeDocument());

			// on rattache notre json avec notre bean
			document.setJson(new Json());
			document.getJson().setData(DocumentHelper.stringify(json));

			// on boucle sur les pieces jointes

			//##################################
			// ADD 2 doc 
			//##################################
			List<Document> output = new ArrayList<Document>();
			Document document_1 = new Document();
			document_1.setDtcreate(new Date());
			document_1.setCmroc("8080");
			document_1.setLibelle("FileName");
			document_1.setId(DocumentHelper.generateSudeNewId(".jpg"));
			document_1.setJson(new Json());
			output.add(document_1);

			Document document_2 = new Document();
			document_2.setDtcreate(new Date());
			document_2.setCmroc("8080");
			document_2.setLibelle("FileName");
			document_2.setId(DocumentHelper.generateSudeNewId(".jpg"));
			document_2.setJson(new Json());
			output.add(document_2);

			//##################################
			int counter = 1;

			ArrayList<Document> listDoc = new ArrayList<Document>();

			EddmManager EddmManager = new EddmManager();

			for (Document newDoc : output) {

				System.out.println("----------------------");

				// on reset notre ID
				document.setId(newDoc.getId());
				newDoc.setTypeDocument(document.getTypeDocument());

				// pour le libelle EDDM (meme que pour la SUDE)
				json.addAttribute("EDDM_LIBELLE", newDoc.getLibelle());
				newDoc.setLibelle("Ged Entrante");

				//if (sens != null && "E".equals(sens)){
				//	json.addAttribute("EDDM_SENS", "E");
				//}

				// AFFECATION DES DOCUMENTS VIA EDDM
				// besoin de ca pour l'appel EDDM
				newDoc.setJson(document.getJson());
				newDoc.getJson().setData(DocumentHelper.stringify(json));

				// on realise enfin l'appel

				fr.cimut.ged.entrant.beans.appelmetier.Eddm eddm = EddmManager.createEmpty();

				// on extrait l'eddoID fournit en retour d'appel EDDM
				List<Long> ids = GedeIdHelper.getIdsFromEddoc(eddm.getDocId());
				newDoc.setTsstar(ids.get(1));
				newDoc.setIdstar(ids.get(0));

				System.out.println("EDDOC ID : " + eddm.getDocId());

				json.setEddocId(eddm.getDocId());

				//json.getEddocIds().add(eddm.getDocId());

				// on reaffect, pas de reference puisque c'est une  String, donc OK
				newDoc.setJson(new Json());
				newDoc.getJson().setData(DocumentHelper.stringify(json));
				// on ajoute a la liste
				listDoc.add(DocumentHelper.merge(document, newDoc));
			}
			System.out.println("----------------------");
			System.out.println("----------------------");

			String daId = "54854122";

			// on periste ces document dans l'editique
			for (Document doc : listDoc) {
				fr.cimut.ged.entrant.beans.mongo.DocumentMongo json_ = DocumentHelper.getDocMongoFromJson(doc);
				json_.setSudeId(daId);
				doc.getJson().setData(DocumentHelper.stringify(json_));
				System.out.println(doc.getId() + " " + json_.getEddocId());
				json.getEddocIds().add(json_.getEddocId());
			}

			if (listDoc != null && !listDoc.isEmpty()) {
				json.setId(listDoc.get(0).getId());
				System.out.println(json.getId());
				for (String string : json.getEddocIds()) {
					System.out.println("\t" + string);
				}
			}

			System.out.println(DocumentHelper.stringify(json));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	//@Test 
	public void matchRule() {

		List<Rule> listing = new ArrayList<Rule>();
		Rule rule1 = new Rule();
		rule1.setId("rule N°1");

		List<RuleCriteres> criteres = new ArrayList<RuleCriteres>();

		RuleCriteres critere1 = new RuleCriteres();
		critere1.setId("REGION");
		critere1.addParameter("Basse-Normandie");
		critere1.addParameter("Haute-Normandie");
		critere1.addParameter("Bretagne");

		RuleCriteres critere2 = new RuleCriteres();
		critere2.setId("TYPE_DOSSIER");
		critere2.addParameter("EMAIL");
		critere2.addParameter("SUDE");

		RuleCriteres critere3 = new RuleCriteres();
		critere3.setId("TYPE_COURRIER");
		critere3.addParameter("Personne");
		critere3.addParameter("Partenaire");

		criteres.add(critere1);
		criteres.add(critere2);
		criteres.add(critere3);
		rule1.setCriteres(criteres);

		listing.add(rule1);

		fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = new fr.cimut.ged.entrant.beans.mongo.DocumentMongo();

		json.getAttributes().put(GlobalVariable.ATTR_DESTINATAIRE, "A");
		json.getAttributes().put("sujet", "B");
		json.getAttributes().put("expediteur", "C");
		json.setCommentaire("D");
		json.setDtCreate(new DateTime());
		json.setTypeDocument("EMAIL");
		json.setRegion("Bretagne");
		json.setTypeEntiteRattachement(TypeEntite.PERSONNE);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> obj = null;
		try {
			obj = objectMapper.readValue(objectMapper.writeValueAsString(json), new TypeReference<HashMap<String, Object>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Rule rule : listing) {

			boolean found = false;

			for (RuleCriteres ruleCriteres : rule.getCriteres()) {
				Object item = obj.get(ruleCriteres.getId());
				if (item instanceof String) {
					if (!ruleCriteres.getParameters().contains(item)) {
						found = false;
						break;
					} else {
						found = true;
					}
				}
			}

			if (found) {
				System.out.println(rule.getId());
				System.out.println(rule.getService().getId());
				System.out.println(rule.getService().getName());
				System.out.println(rule.getService().getSujet());
				System.out.println(rule.getService().getSupport());
				System.out.println(rule.getService().getType());
				break;
			} else {
				System.out.println("no rules match");
			}
		}
	}

	//@Test 
	public void openImapMail() {

		Properties properties = new Properties();

		/* Connexion Cimut */
		String protocol = "imaps";
		String host = "mail.cimut.net";
		String port = "993";
		String userName = "guillaume.yclon@cimut.fr";
		String password = "Quimper29";

		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);
		properties.put(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
		properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
		properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));
		properties.put(String.format("mail.%s.ssl.trust", protocol), host);
		Session session = Session.getDefaultInstance(properties);

		Store store = null;

		try {

			store = session.getStore(protocol);
			store.connect(host, userName, password);
			System.out.println("OK");

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			fail();
		} catch (MessagingException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (store != null) {
				try {
					store.close();
					System.out.println("Store closed !");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Test
	public void getMailComptesParameters() throws ParseException {
		try {
			List<CompteMail> compteMailList = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail iterable_element : compteMailList) {
				System.out.println("###########################");
				assertTrue("mail.cimut.net".equals(iterable_element.getHost()));
				assertTrue("993".equals(iterable_element.getPort()));
				assertTrue("imaps".equals(iterable_element.getProtocole()));
				assertTrue("guillaume.yclon@cimut.fr".equals(iterable_element.getEmail()));
				assertTrue("29000Quimper".equals(iterable_element.getPassword()));
				System.out.println("Emails rapport : ");
				for (String email : iterable_element.getRapportEmails()) {
					System.out.println("\t" + email);
				}
				assertTrue("noreply@mbamutuelle.fr".equals(iterable_element.getAccusReceptionExpediteur()));
				assertTrue("9970MP0001".equals(iterable_element.getAccusReceptionTemplateId()));
			}
		} catch (CimutConfException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void exportMailComptesParameters() throws ParseException {
		try {
			List<CompteMail> compteMailList = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail iterable_element : compteMailList) {

				if (iterable_element == null) {
					continue;
				}

				System.out.println("###########################");
				System.out.println(iterable_element.getEmail());
				System.out.println("###########################");
				assertTrue(iterable_element.getHost() != null);
				assertTrue(iterable_element.getPort() != 0);
				assertTrue(iterable_element.getProtocole() != null);
				assertTrue(iterable_element.getEmail() != null);
				assertTrue(iterable_element.getPassword() != null);
				assertTrue(iterable_element.getAccusReceptionExpediteur() != null);
				assertTrue(iterable_element.getAccusReceptionTemplateId() != null);
				for (String inBox : iterable_element.getInBoxes()) {
					System.out.println("\t" + inBox);
				}
				for (String email : iterable_element.getRapportEmails()) {
					System.out.println("\t" + email);
				}
			}
		} catch (CimutConfException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void listErrorFolder() {
		CompteMailManager compteMailManager = null;
		try {
			List<CompteMail> compteMailList = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail compteMail : compteMailList) {
				try {
					compteMailManager = new CompteMailManager(compteMail);
					for (String inBoxFolderName : compteMail.getInBoxes()) {
						List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.ERROR);
						System.out.println(compteMail.getEmail() + " (" + messages.size() + ")");
						for (Message message : messages) {
							System.out.println(message.getSubject());
						}
					}

				} catch (MessagingException e) {
					throw new CimutMailException("Impossible de recuperer la list des dossier de : " + compteMail.getEmail(), e);
				} finally {
					compteMailManager.setxpunge(false);
					compteMailManager.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//@Test
	public void listFolder() {
		CompteMailManager compteMailManager = null;
		try {
			List<CompteMail> compteMailList = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail compteMail : compteMailList) {
				try {
					compteMailManager = new CompteMailManager(compteMail);
					for (String inBoxFolderName : compteMail.getInBoxes()) {
						List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.INBOX);
						for (Message message : messages) {
							compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.ERROR);
							System.out.println(message.getSubject());
							compteMailManager.moveProcessedMessage(inBoxFolderName, message, MailFolderType.BACKUP);
						}
					}
				} catch (MessagingException e) {
					throw new CimutMailException("Impossible de recuperer la list des dossier de : " + compteMail.getEmail(), e);
				} finally {
					compteMailManager.setxpunge(true);
					compteMailManager.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Test 
	@SuppressWarnings("unused")
	public void moveToErrorMail() {
		CompteMailManager compteMailManager = null;
		try {
			List<CompteMail> compteMailList = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail compteMail : compteMailList) {
				try {
					compteMailManager = new CompteMailManager(compteMail);
					for (String inBoxFolderName : compteMail.getInBoxes()) {
						List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.INBOX);
						for (Message message : messages) {
							//compteMailManager.
						}
					}
				} catch (MessagingException e) {
					throw new CimutMailException("Impossible de recuperer les email de : " + compteMail.getEmail(), e);
				} finally {
					compteMailManager.setxpunge(true);
					compteMailManager.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
