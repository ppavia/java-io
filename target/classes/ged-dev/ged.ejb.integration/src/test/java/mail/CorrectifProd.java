package mail;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.mail.CompteMailManager;
import fr.cimut.ged.entrant.mail.MailFolderType;
import fr.cimut.ged.entrant.mail.MailerHelper;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.service.CompteMailFileService;

public class CorrectifProd {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

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
		props.setProperty("fr.cimut.ged.entrante.p360.url", "http://lcidxs2.cimut.fr:9601/meslCIP/Search360/");
		props.setProperty("fr.cimut.ged.entrante.cmroc.file.path",
				"C:/SVN/getEntrante/fr.cimut.ged.entrant.serviceIntegration/trunk/src/test/resources/complement_cmroc.csv");
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

	//@Test
	public void addPiecesJointToSude() {

		File folderRoot = new File("c:/tmp/ratrapage");
		String cmroc = "9970";
		String myDaId = "13045";
		Map<String, List<File>> hash = new HashMap<String, List<File>>();
		for (File folder : folderRoot.listFiles()) {
			if (!folder.isDirectory()) {
				continue;
			}
			String daId = folder.getName();

			hash.put(daId, new ArrayList<File>());
			for (File file : folder.listFiles()) {
				if (file.isFile() && file.length() > 0) {
					//System.out.println(daId+" "+ file.getName()+" "+file.length());
					hash.get(daId).add(file);
				}
			}
		}

		Iterator<String> iter = hash.keySet().iterator();
		while (iter.hasNext()) {
			String daId = iter.next();
			if (myDaId.equals(daId)) {
				try {
					System.out.println("DO IT !");
					addDocsToDa(hash.get(daId), cmroc, myDaId);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
			}
			iter.remove();
			//break;
		}
	}

	public void addDocsToDa(List<File> files, String cmroc, String reclamId) throws Exception {

		// parametres d'appel serveur
		String url = "http://lcidxs6.cimut.fr:8180/ged.web.interrogation-0.0.2/selligentUpload";
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

	//	
	//@Test
	@SuppressWarnings("unused")
	public void rattrapagePieceJOinteInvalide() {

		//boolean echec = false;
		Map<String, String> rattrapage = new HashMap<String, String>();
		Connection connection = null;
		try {

			connection = DriverManager.getConnection("jdbc:oracle:thin:@editique.cimut.fr:1521:EDT", "EDITIC", "XXXX");
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@bdcipv3.cimut.fr:1521:CIPV3", "EDITIC",
			//		"EDITIC");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection == null) {
			System.out.println("Failed to make connection!");
			return;
		}

		PreparedStatement rechercheDocID;
		try {
			rechercheDocID = connection.prepareStatement(
					"select ERD_REFDOC.ERD_LBNMFD, ERD_REFDOC.ERD_MIMETYPE from ERD_REFDOC where  ERD_REFDOC.ERD_IDSTAR = ? and ERD_REFDOC.ERD_TSSTAR = ?");
		} catch (SQLException e1) {
			System.out.println("prepareStatement failed !");
			e1.printStackTrace();
			return;
		}

		SimpleDateFormat spf = new SimpleDateFormat("ddMMyyyyHHmmss");

		ObjectMapper mapper = new ObjectMapper();
		InteractionMongo inter = new InteractionMongo();
		BasicDBObject queryDoc = new BasicDBObject();

		try {

			//COMMENTAIRE : {$regex:"</a><br>\\*\\*"},
			//DATE_CREATION: {$gte:new Date("Jan 19, 2016")}

			Pattern john = Pattern.compile("</a><br><br>");
			queryDoc.put("COMMENTAIRE", john);
			//queryDoc.put("MAIL_DESTINATAIRE", "gestion.entreprise@mbamutuelle.com");

			Manager<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> documentManager = MongoManagerFactory.getDocumentManager(envirTeste, "9970",
					inter);
			DBCursor cursor = documentManager.getCursor(queryDoc);
			while (cursor.hasNext()) {
				String json = cursor.next().toString();
				fr.cimut.ged.entrant.beans.mongo.DocumentMongo document = null;

				if (json != null) {
					document = mapper.readValue(json, fr.cimut.ged.entrant.beans.mongo.DocumentMongo.class);
					boolean haveOnlyOneHtml = true;

					System.out.println(document.getSudeId() + " " + document.getDtCreate().toString() + " " + document.getCommentaire());

					//					String key = document.getAttribute("MAIL_SUBJECT") + "_" + document.getAttribute("MAIL_EXPEDITEUR")
					//							+ "_" + spf.format(document.getDtCreate().toDate());
					//
					//					String filenameOk = "";
					//
					//					for (String eddocId : document.getEddocIds()) {
					//
					//						try {
					//							List<Long> ids = DocumentHelper.getIdsFromEddoc(eddocId);
					//							rechercheDocID.setLong(1, ids.get(0));
					//							rechercheDocID.setLong(2, ids.get(1));
					//							ResultSet resultats = rechercheDocID.executeQuery();
					//
					//							boolean encore = resultats.next();
					//							while (encore) {
					//
					//								String filename = resultats.getString(1);
					//								String typeMime = resultats.getString(2);
					//
					//								if (filename.endsWith(".html")) {
					//									if (!rattrapage.containsKey(key)) {
					//										filenameOk = filename + "  => " + typeMime;
					//										rattrapage.put(key, filename);
					//									} else {
					//										System.out.println("multiple");
					//										haveOnlyOneHtml = false;
					//									}
					//								}
					//								encore = resultats.next();
					//							}
					//							resultats.close();
					//						} catch (CimutDocumentException e) {
					//							e.printStackTrace();
					//							return;
					//						} catch (SQLException e) {
					//							e.printStackTrace();
					//							return;
					//						}
					//					}
					//
					//					if (!haveOnlyOneHtml) {
					//						rattrapage.remove(key);
					//						System.out.println("Multiple html found ... " + document.getId() + document.getDa());
					//					} else {
					//						System.out.println(document.getDa()+" DOC_ID : " + filenameOk + " " + document.getCommentaire() + " "
					//								+ document.getDa());
					//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}

			try {
				inter.closeConnexion();
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
		}

		System.out.println("SIZE : " + rattrapage.size());

		//		System.out.println("####################");
		//		System.out.println("INVENTORY : ");
		//		System.out.println("####################");
		//		for (String str : rattrapage.keySet()) {
		//			System.out.println(str + " " + rattrapage.get(str));
		//		}

		//return;

		//				System.out.println("#### END FIRST PHASE ####");
		//		
		//				CompteMailManager compteMailManager = null;
		//				try {
		//					List<CompteMail> listCompte = new MailerRetreiver().getCompteMails();
		//		
		//					int counter = 0;
		//		
		//					for (CompteMail compteMail : listCompte) {
		//						try {
		//							compteMailManager = new CompteMailManager(compteMail);
		//							List<Message> messages = compteMailManager.getMessages();
		//							for (Message message : messages) {
		//								String key = message.getSubject() + "_" + MailerHelper.getFromTo(message) + "_"
		//										+ spf.format(MailerHelper.getDate(message).toDate());
		//								if (rattrapage.containsKey(key)) {
		//									System.out.println("GOT ONE : " + key);
		//									Map<String, String> contents = MailerHelper.getMessage(message);
		//									Writer out = null;
		//									try {
		//										if (contents.containsKey("charset") && !contents.get("charset").isEmpty()) {
		//											out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/tmp/"
		//													+ rattrapage.get(key)), "UTF-8"));
		//										} else {
		//											// ok , je prend le charset du systeme par defaut
		//											out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/tmp/"
		//													+ rattrapage.get(key)), "UTF-8"));
		//										}
		//										out.write(contents.get("content"));
		//									} finally {
		//										if (out != null) {
		//											out.close();
		//										}
		//									}
		//									rattrapage.remove(key);
		//								} else {
		//		
		//									if (counter % 50 == 0) {
		//										System.out.println("counter = " + counter);
		//									}
		//									counter++;
		//		
		//								}
		//							}
		//						} catch (MessagingException e) {
		//							e.printStackTrace();
		//							return;
		//						} finally {
		//							compteMailManager.setxpunge(false);
		//							compteMailManager.disconnect();
		//						}
		//					}
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//					return;
		//				}
		//		
		//				System.out.println("####################");
		//				System.out.println("NOT FOUND  : ");
		//				System.out.println("####################");
		//				for (String str : rattrapage.keySet()) {
		//					System.out.println(str + " " + rattrapage.get(str));
		//				}

	}

	//@Test
	public void rattrapageAucunePieceJointe() {

		SimpleDateFormat spf = new SimpleDateFormat("ddMMyyyyHHmmss");

		ObjectMapper mapper = new ObjectMapper();
		InteractionMongo inter = new InteractionMongo();
		BasicDBObject queryDoc = new BasicDBObject();

		HashMap<String, String> rattrapage = new HashMap<String, String>();
		try {

			queryDoc.put("ID_EDDOCS", new BasicDBObject("$size", 0));
			queryDoc.put("ID_DA", new BasicDBObject("$ne", null));
			queryDoc.put("MAIL_EXPEDITEUR", new BasicDBObject("$ne", "CONTACT@MIND1.MONCLUBPV.COM"));

			Manager<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> documentManager = MongoManagerFactory.getDocumentManager(envirTeste, "9970",
					inter);
			DBCursor cursor = documentManager.getCursor(queryDoc);
			while (cursor.hasNext()) {
				String json = cursor.next().toString();
				fr.cimut.ged.entrant.beans.mongo.DocumentMongo document = null;

				if (json != null) {
					document = mapper.readValue(json, fr.cimut.ged.entrant.beans.mongo.DocumentMongo.class);
					String key = document.getAttribute("MAIL_SUBJECT") + "_" + document.getAttribute("MAIL_EXPEDITEUR") + "_"
							+ spf.format(document.getDtCreate().toDate());
					rattrapage.put(key, document.getSudeId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				inter.closeConnexion();
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
		}

		//System.out.println("SIZE : " + rattrapage.size());

		//		System.out.println("####################");
		//		System.out.println("INVENTORY : ");
		//		System.out.println("####################");

		File folder = new File("c:/tmp/ratrapage/");
		for (String str : rattrapage.keySet()) {
			//	System.out.println(rattrapage.get(str)+" "+str);
			File file = new File(folder, rattrapage.get(str));
			if (file.exists()) {
				//System.out.println(rattrapage.get(str)+" EXIST !"+" "+str);
			} else {
				System.out.println(rattrapage.get(str) + " NOT EXIST !" + " " + str);
			}
		}

		//return;

		//				System.out.println("#### END FIRST PHASE ####");
		//		
		//				CompteMailManager compteMailManager = null;
		//				try {
		//					List<CompteMail> listCompte = new MailerRetreiver().getCompteMails();
		//		
		//					int counter = 0;
		//		
		//					for (CompteMail compteMail : listCompte) {
		//						try {
		//							compteMailManager = new CompteMailManager(compteMail);
		//							List<Message> messages = compteMailManager.getMessages();
		//							for (Message message : messages) {
		//								String key = message.getSubject() + "_" + MailerHelper.getFromTo(message) + "_"
		//										+ spf.format(MailerHelper.getDate(message).toDate());
		//								if (rattrapage.containsKey(key)) {
		//									System.out.println("GOT ONE : " + key);
		//									Map<String, String> contents = MailerHelper.getMessage(message);
		//									Writer out = null;
		//
		//									File folder = new File("ratrapage/"+rattrapage.get(key));
		//									if (!folder.exists()){
		//										folder.mkdirs();
		//									}
		//									File newFile = new File(folder,"email.html");
		//									System.out.println("SAVING "+ newFile.getAbsolutePath());
		//
		//									try {
		//										if (contents.containsKey("charset") && !contents.get("charset").isEmpty()) {
		//											out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), contents.get("charset")));
		//										} else {
		//											// ok , je prend le charset du systeme par defaut
		//											out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));
		//										}
		//										out.write(contents.get("content"));
		//									}catch(Exception e){
		//										e.printStackTrace();
		//									} finally {
		//										if (out != null) {
		//											out.close();
		//										}
		//									}
		//
		//									getAttachment(message, "9970",folder);
		//									
		//									rattrapage.remove(key);
		//		
		//		
		//								}
		//								
		//
		//								if (counter % 500 == 0) {
		//									System.out.println("counter = " + counter);
		//								}
		//								counter++;
		//								
		//							}
		//						} catch (MessagingException e) {
		//							e.printStackTrace();
		//							return;
		//						} finally {
		//							compteMailManager.setxpunge(false);
		//							compteMailManager.disconnect();
		//						}
		//					}
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//					return;
		//				}

		//				System.out.println("####################");
		//				System.out.println("NOT FOUND  : ");
		//				System.out.println("####################");
		//for (String str : rattrapage.keySet()) {
		//	System.out.println(str + " " + rattrapage.get(str));
		//}

	}

	private static void getAttachment(Message message, String cmroc, File folder)
			throws CimutMailException, IOException, MessagingException, CimutFileException, CimutConfException {

		// recuperation du contenu du mail
		Object content = message.getContent();

		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;

			// boucle sur les parts
			for (int j = 0; j < multipart.getCount(); j++) {

				// instancie notre nouveau document

				BodyPart bodyPart = multipart.getBodyPart(j);
				String disposition = bodyPart.getDisposition();

				System.out.println("disposition " + disposition + " " + bodyPart.getContentType());
				System.out.println("Content-Id " + bodyPart.getHeader("Content-ID"));

				// Bon, verifier que la disposition == attachemnt n'est pas suffisant, il y en a en Inline ... 
				// en particulier ceux qui n'ont pas de Content-ID (donc pas injectable dans le HTML)

				// mais pas le text/html vu que je l'ai surement pris en compte dans le body ...
				if (bodyPart != null && disposition != null
						&& (disposition.equalsIgnoreCase("ATTACHMENT")
								|| (bodyPart.getHeader("Content-ID") == null && "INLINE".equalsIgnoreCase(disposition)
										&& !bodyPart.isMimeType("text/plain") && !bodyPart.isMimeType("text/html")))) {

					String filename = "";
					if (bodyPart.isMimeType("message/rfc822")) {
						Message tmpMessage = (Message) bodyPart.getContent();
						String subject = tmpMessage.getSubject();
						if (subject == null || subject.isEmpty()) {
							subject = "aucun sujet";
						}
						filename = subject + ".eml";
					} else {
						filename = MailerHelper.getFileName(bodyPart.getContentType(), bodyPart.getFileName());
					}

					if (!folder.exists()) {
						folder.mkdirs();
					}

					File newFile = new File(folder, filename);
					System.out.println("SAVING " + newFile.getAbsolutePath());
					((MimeBodyPart) bodyPart).saveFile(newFile);

				}
			}
		}
	}

	//@Test
	public void getContentStipped() {
		CompteMailManager compteMailManager = null;
		String DefaultCharset = System.getProperty("file.encoding");
		System.out.println("default file encoding => " + DefaultCharset);
		try {
			List<CompteMail> listCompte = new CompteMailFileService().getAllComptesMailFromFiles();
			for (CompteMail compteMail : listCompte) {
				System.out.println(compteMail.getEmail());
				try {
					compteMailManager = new CompteMailManager(compteMail);
					for (String inBoxFolderName : compteMail.getInBoxes()) {
						List<Message> messages = compteMailManager.getMessages(inBoxFolderName, MailFolderType.INBOX);

						for (Message message : messages) {
							System.out.println(message.getSubject());
							Map<String, String> content = MailerHelper.getMessage(message);
							//						System.out.println("CHARSET : "+content.get("charset"));
							//						System.out.println("CONTENT IS : "+content.get("content"));
							//
							//counter++;
							//

							String daId = "18231";
							File folder = new File("c:/tmp/" + daId);
							if (!folder.exists()) {
								folder.mkdir();
							}

							File newFile = new File(folder, "email.html");
							System.out.println("SAVING " + newFile.getAbsolutePath());

							Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));
							try {
								out.write(content.get("content"));
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								out.close();
							}

							getAttachment(message, "9970", folder);

							//						File file = new File("test_"+counter+".eml");
							//						FileOutputStream fop = null;
							//						try {
							//							fop = new FileOutputStream(file);
							//							message.writeTo(fop);
							//						}catch(Exception e){
							//							e.printStackTrace();
							//						}finally{
							//							try {if (fop != null){fop.close();}}catch(Exception e1){}
							//						}

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

}