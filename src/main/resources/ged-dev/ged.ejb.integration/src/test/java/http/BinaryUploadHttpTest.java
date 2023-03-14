package http;

import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class BinaryUploadHttpTest {

	//@Test
	public void uploadCourtier() {

		// parametres d'appel serveur [DEV]
		String url = "http://lcidxs6.cimut.fr:8180/gede/binaryUpload";
		String username = "giveMeFive";
		String password = "giveMeNiNe";

		// les fichier a transmettre
		File fileToUpload1 = new File("C:\\Users\\gyclon\\demo\\test\\Hydéràanègeas.jpg");

		// parametres d'appel metier 
		String cmroc = "8080"; // organisme ou tutelle
		String typeDossier = "Courtier"; // Personne, Entreprise ou Partenaire
		PostMethod filePost = null;

		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);
			Part[] parts = { new StringPart("cmroc", cmroc), new StringPart("typeDossier", typeDossier),
					new StringPart("binaryString", "sdfgsdfgsdfgsdfgsdfg"), new StringPart("fileName", fileToUpload1.getName()),
					new StringPart("typeDocument", "TEST_U") };
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