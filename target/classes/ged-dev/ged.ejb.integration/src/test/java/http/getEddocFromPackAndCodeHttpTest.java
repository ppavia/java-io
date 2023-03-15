package http;

import static org.junit.Assert.fail;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import fr.cimut.ged.entrant.exceptions.CimutMetierException;

public class getEddocFromPackAndCodeHttpTest {

	//@Test
	public void getDocBis() {

		// parametres d'appel serveur [DEV]
		//String url = "http://lcid-gedoc-cons.cimut.fr:8080/gede/rest/recherche";
		//String url = "http://lcid-gedoc-cons.cimut.fr:8080/gede/rest/recherche?cmroc=9997&codeProduit=ZENTA&identifiantPack=02";
		String url = "http://lcid-gedoc-cons.cimut.fr:8080/gede/rest/recherche?env=RECV&cmroc=9997&codeProduit=VRSP&identifiantPack=04";
		//String url = "http://localhost:8080/gede/rest/recherche";
		String authentification = "jkdsozfh-4qsdf7-qsdf";

		// parametres d'appel metier 

		//String cmroc = "?cmroc=9997";
		//String codeProduit = "&codeProduit=VRSP";
		//String identifiantPack = "&identifiantPack=04";
		//String environnement = "&env=DEV";
		try {
			//ClientRequest request = new ClientRequest(url+cmroc+codeProduit+identifiantPack);
			ClientRequest request = new ClientRequest(url);
			request.header("Authorization", authentification);

			try {
				ClientResponse<String> req = request.get(String.class);
				if (req.getStatus() != 200) {
					throw new CimutMetierException(
							"Appel à la recherche en echec; http status " + req.getStatus() + " => " + req.getEntity(String.class));
				} else {
					String eddoc_id = req.getEntity();
					System.out.println(eddoc_id);
				}
			} catch (Exception e1) {
				throw new CimutMetierException("Appel au RechercheEddocEndpoint", e1);
			} finally {
				if (request != null) {
					request.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getDoc() {

		// parametres d'appel serveur [DEV]
		String url = "http://lcid-gedoc-cons.cimut.fr:8080/gede/rest/recherche";
		//String url = "http://localhost:8080/gede/rest/recherche";
		String authentification = "jkdsozfh-4qsdf7-qsdf";

		// parametres d'appel metier 

		String cmroc = "9997";
		String codeProduit = "D";
		String identifiantPack = "1";
		HttpClient httpclient = null;
		try {
			httpclient = new HttpClient();
			ClientRequest request = new ClientRequest(url + "/" + cmroc + "/" + codeProduit + "/" + identifiantPack);
			request.header("Authorization", authentification);

			try {
				ClientResponse<String> req = request.get(String.class);

				if (req.getStatus() != 200) {
					throw new CimutMetierException(
							"Appel à la recherche en echec; http status " + req.getStatus() + " => " + req.getEntity(String.class));
				} else {
					String eddoc_id = req.getEntity();
					System.out.println("eddoc_id => " + eddoc_id);
				}

			} catch (Exception e1) {
				throw new CimutMetierException("Appel au RechercheEddocEndpoint", e1);
			} finally {
				if (request != null) {
					request.clear();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (httpclient != null) {

			}
		}
	}
}