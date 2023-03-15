package http;

import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.junit.Test;

public class UploadSUDEHttpTest {

	@Test
	public void uploadCourtier() {
		System.out.println("uploadCourtier");
		// parametres d'appel serveur [DEV]
		String url = "http://lcidxs6.cimut.fr:8180/gede/sudeUpload";
		//String url = "http://localhost:8080/webDummy/gede";
		//String url = "http://localhost:8080/ged.web.interrogation-0.0.2/bulaUpload";
		String username = "giveMeFive";
		String password = "giveMeNiNe";

		// les fichier a transmettre
		File fileToUpload1 = new File("C:\\tmp\\test.pdf");

		// parametres d'appel metier 
		String sens = "R"; // organisme ou tutelle
		String cmroc = "9916"; // organisme ou tutelle
		String login = "spare9916"; // tracabilité
		//String typeDossier = "Courtier"; // Personne, Entreprise ou Partenaire
		//String identifiant = "ref_courtier"; // si Personne		=> Numero de dossier
		// si Entreprise		=> 0001|ETAB
		// si Section		=> 0001|C|01
		// si partenaire		=> 0001|Q|01
		String typeDossier = "Personne";
		String identifiant = "0000431306";
		PostMethod filePost = null;
		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);
			StringPart myString = new StringPart("filename", fileToUpload1.getName());
			myString.setCharSet("UTF-8");
			Part[] parts = { new StringPart("cmroc", cmroc), new StringPart("login", login), new StringPart("typeDossier", typeDossier),
					new StringPart("identifiant", identifiant), new FilePart(fileToUpload1.getName(), fileToUpload1), new StringPart("sens", sens),
					myString };
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