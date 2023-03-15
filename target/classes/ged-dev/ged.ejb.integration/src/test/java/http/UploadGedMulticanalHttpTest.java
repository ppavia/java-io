package http;

import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.junit.Test;

public class UploadGedMulticanalHttpTest {

	@Test
	public void get() {

		// parametres d'appel serveur
		String url = "http://lcidxs6.cimut.fr:8180/gede/multicanalUpload";
		//		String url = "http://localhost:8080/gede/multicanalUpload";

		// parametres d'appel 
		String env = "CIPV3";
		String cmroc = "9916";
		String demandeur = "spare9916";
		String typeDossier = "Partenaire"; // Personne, Entreprise ou Partenaire
		String idDossier = "7501|ADJ|0000"; // si Personne		=> Numero de dossier : 0000431306
											// si Entreprise	=> 0001|J  (no interne|class)
											// si Section		=> 0001|J|01 (no interne|class|code section)
											// si Partenaire	=> 7501|ADJ|0000  X(14)|X(5)|9(4)=> 0000 (PART_ID|TYPPART_ID|PART_NIV)
		String typeDocument = "TESTOCOM";
		String libelle = "Test d'integration d'une communication";
		String origine = "ged ejb integration UploadGedMulticanalHttpTest";
		String canal = "FAX";
		String destinataire = "0256562900";
		String service = "prestation";
		String mimeType = "application/pdf";
		String isNotification = "true";

		// fichier a transmettre
		File fileToUpload = new File("C:\\tmp\\test.pdf");

		PostMethod filePost = null;
		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);

			Part[] parts = { new StringPart("env", env), new StringPart("cmroc", cmroc), new StringPart("demandeur", demandeur),
					new StringPart("typeDossier", typeDossier), new StringPart("idDossier", idDossier), new StringPart("typeDocument", typeDocument),
					new StringPart("libelle", libelle), new StringPart("origine", origine), new StringPart("canal", canal),
					new StringPart("destinataire", destinataire), new StringPart("service", service), new StringPart("mimeType", mimeType),
					new StringPart("isNotification", isNotification), new FilePart(fileToUpload.getName(), fileToUpload) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int responseCode = httpclient.executeMethod(filePost);
			if (responseCode != 200) {
				throw new Exception("Une erreur est survenue lors du transfert des fichiers: \n\t\t" + filePost.getResponseBodyAsString());
			}

			// recuperation des infos !
			String[] eddocIds = filePost.getResponseBodyAsString().split(",");
			for (String eddocId : eddocIds) {
				System.out.println(eddocId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
		}
	}
}