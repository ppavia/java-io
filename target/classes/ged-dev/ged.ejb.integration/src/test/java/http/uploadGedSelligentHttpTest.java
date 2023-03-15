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

public class uploadGedSelligentHttpTest {

	//@Test
	public void get() {

		// parametres d'appel serveur
		//String url = "http://localhost:8080/gede/selligentUpload?env=RECV"; // LOCALRECV
		String url = "http://lcidxs6:8080/gede/selligentUpload?env=PPO";// LOCAL DEV
		//String url = "http://localhost:8080/gede/selligentUpload?env=PPO";
		String username = "giveMeFive";
		String password = "giveMeNiNe";

		// les fichier a transmettre
		File fileToUpload1 = new File("C:\\Users\\gyclon\\Pictures\\cimut_logo.jpg");
		//File fileToUpload2 = new File("C:\\tmp\\2.txt");
		//File fileToUpload3 = new File("C:\\tmp\\4.jpg");

		// parametres d'appel metier 
		String cmroc = "9970"; // organisme ou tutelle

		//String reclamId = "322043105";  // RECV
		String reclamId = "64113"; //DEV

		PostMethod filePost = null;
		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);

			Part[] parts = { new StringPart("cmroc", cmroc), new StringPart("reclamId", reclamId),
					new FilePart(fileToUpload1.getName(), fileToUpload1) };

			//,new FilePart(fileToUpload2.getName(), fileToUpload2 ),
			//new FilePart(fileToUpload3.getName(), fileToUpload3 )

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
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
		}
	}

	//formatage de l'appel + appel a la GED
	//@Test
	public void sendFile() throws Exception {

		String url = "http://lcidxs6.cimut.fr:8080/ged.web.interrogation-0.0.2/selligentUpload";
		//String url = "http://localhost:8080/ged.web.interrogation-0.0.2/selligentUpload";
		String username = "giveMeFive";
		String password = "giveMeNiNe";
		String cmroc = "9017";
		String reclamId = "671573";
		String test = "";
		PostMethod filePost = null;
		try {
			HttpClient httpclient = new HttpClient();

			filePost = new PostMethod(url);
			String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
			filePost.addRequestHeader("Authorization", "Basic " + basic_auth);

			File fileToUpload1 = new File("C:\\tmp\\1.txt");

			Part[] parts = {

					new StringPart("cmroc", cmroc), new StringPart("login", "Selligent"), new StringPart("reclamId", reclamId),
					new FilePart(fileToUpload1.getName(), fileToUpload1) };

			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int responseCode = httpclient.executeMethod(filePost);

			System.out.println(responseCode);

			if (responseCode != 200) {
				if (filePost != null) {
					filePost.releaseConnection();
				}
				throw new Exception("Le code de retour est different de 200 " + filePost.getResponseBodyAsString());
			}

			test = filePost.getResponseBodyAsString();

		} catch (Exception e) {
			if (filePost != null) {
				filePost.releaseConnection();
			}
			throw new Exception("Une erreur est survenue " + e.getMessage(), e);
		} finally {
			if (filePost != null) {
				filePost.releaseConnection();
			}
		}

		System.out.println(test);
		//return filePost.getResponseBodyAsString();

	}

}