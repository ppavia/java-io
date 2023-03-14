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

public class UploadGedBulaHttpTest {

	//@Test
	@SuppressWarnings("unused")
	public void get() {

		// parametres d'appel serveur
		//String url = "http://lcidxs6.cimut.fr:8180/gede/bulaUpload";
		String url = "http://localhost:8080/gede/bulaUpload";
		String username = "giveMeFive";
		String password = "giveMeNiNe";

		// les fichier a transmettre
		File fileToUpload1 = new File("C:\\tmp\\1.txt");
		File fileToUpload2 = new File("C:\\tmp\\2.txt");
		File fileToUpload3 = new File("C:\\tmp\\4.jpg");

		// parametres d'appel metier 
		String cmroc = "8080"; // organisme ou tutelle
		String login = "gyclon8080"; // tracabilité
		String typeDossier = "Personne"; // Personne, Entreprise ou Partenaire
		String identifiant = "0312090891"; // si Personne		=> Numero de dossier
											// si Entreprise		=> 0001|ETAB
											// si Section		=> 0001|C|01
											// si partenaire		=> 0001|Q|01

		PostMethod filePost = null;
		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);

			Part[] parts = { new StringPart("cmroc", cmroc), new StringPart("login", login),
					//new StringPart("typeDossier",typeDossier),
					//new StringPart("identifiant",identifiant),
					new FilePart(fileToUpload1.getName(), fileToUpload1), new FilePart(fileToUpload2.getName(), fileToUpload2),
					new FilePart(fileToUpload3.getName(), fileToUpload3) };
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