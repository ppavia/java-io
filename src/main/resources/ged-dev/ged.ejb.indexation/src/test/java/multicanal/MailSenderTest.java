package multicanal;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cimut.multicanal.web.client.beans.Destinataire;
import fr.cimut.multicanal.web.client.beans.Emetteur;
import fr.cimut.multicanal.web.client.beans.Job;
import fr.cimut.multicanal.web.client.beans.Mail;
import fr.cimut.multicanal.web.client.beans.PieceJointe;

public class MailSenderTest {

	private Date before;

	//@Test
	public void sendMailAccuseReceptionTest() {

		List<Destinataire> destList = new ArrayList<Destinataire>();

		Emetteur emit = new Emetteur("9970", "9970", "9970", "noreply@cimut.fr", "ORGANISME");
		emit.setTypeEmetteur("INTERNE");

		Map<String, String> map = new HashMap<String, String>();
		Destinataire dest = new Destinataire("guillaume.yclon@cimut.fr", map, "", "9970", "ORGANISME");
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("sude_id", "15845");
		dest.setVariables(variables);
		destList.add(dest);

		//Destinataire dest1 = new Destinataire("guillaume.yclon@gmail.fr", map, "", "9916", "ORGANISME");
		//destList.add(dest1);

		Job job = new Job();
		job.setDestinataires(destList);
		job.setEmetteur(emit);
		Set<PieceJointe> piecesJointes = new HashSet<PieceJointe>();
		Mail mail = new Mail("9970MP0001", piecesJointes);
		//Mail mail = new Mail("Accusé de récéption", "test", "test", piecesJointes);
		job.setMail(mail);

		try {
			//MailSender.send(job, "http://lcid-multicanal:28080/server/multicanal");

			ObjectMapper mapper = new ObjectMapper();
			String stringigyJob = "";
			try {
				stringigyJob = mapper.writeValueAsString(job);
				byte ptext[] = stringigyJob.getBytes();
				stringigyJob = new String(ptext, "UTF-8");

			} catch (JsonProcessingException e1) {
				// 
				e1.printStackTrace();
			}
			ClientRequest request = new ClientRequest("http://lcid-multicanal:28080/server/multicanal/mail");
			request.accept("application/json");
			request.body("application/json", stringigyJob);
			if (request.post().getStatus() != 201) {
				System.out.println("Failed");
			} else {
				System.out.println("message Sent !");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR " + e.getMessage());
			fail();
		}

	}

	//@Test
	@SuppressWarnings("unused")
	public void sendMailTest() {

		List<Destinataire> destList = new ArrayList<Destinataire>();

		Emetteur emit = new Emetteur("9970", "9970", "9970", "nepasrepondre@cimut.fr", "ORGANISME");
		emit.setTypeEmetteur("INTERNE");

		Map<String, String> map = new HashMap<String, String>();
		Destinataire dest = new Destinataire("guillaume.yclon@cimut.fr", map, "", "9970", "ORGANISME");
		destList.add(dest);

		//Destinataire dest1 = new Destinataire("guillaume.yclon@gmail.fr", map, "", "9916", "ORGANISME");
		//destList.add(dest1);

		Job job = new Job();
		job.setDestinataires(destList);
		job.setEmetteur(emit);
		Set<PieceJointe> piecesJointes = new HashSet<PieceJointe>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Traitement de la regle : aaaaaa\n\n");
		stringBuilder.append("Critères de séléction : \n");
		stringBuilder.append("machin truc !");

		StringBuilder htmlString = new StringBuilder();
		htmlString.append("TEST IMAGE EMBEDDED<br><br>");

		File file = new File("c:/tmp/cimut_logo.jpg");
		byte[] a = null;
		try {
			a = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
		} catch (IOException e2) {
			// 
			e2.printStackTrace();
			fail();
			return;
		}

		htmlString.append("<img alt=\"Embedded Image\" src=\"data:image/jpg;base64," + new String(a) + "\" />");

		htmlString.append("<br><br><br>");
		htmlString.append("Guillaume Yclon");

		//Mail mail = new Mail("Object du mail","AAAAAAA",stringBuilder.toString().replace("\n","<br>"), piecesJointes );

		String styr = "		<TABLE style=\"border-collapse:collapse\">" + "<tr><td rowspan=\"3\" ><div style=\"" + "height:90px;" + "width:90px;"
				+ "background-color:grey;" + "border-top-left-radius:45px;" + "border-bottom-right-radius:45px;" + "border-bottom-left-radius:45px;"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td>" + "<td><div style=\"" + "height:30px;" + "width:30px;"
				+ "background-color:red;" + "border-top-left-radius:45px;" + "border-bottom-right-radius:45px;" + "border-top-right-radius:45px;"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td></tr>" + "<tr><td><div style=\"" + "height:30px;" + "width:30px;"
				+ "background-color:green;" + "border-top-left-radius:45px;" + "border-bottom-right-radius:45px;" + "border-top-right-radius:45px;"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td></tr>" + "<tr><td><div style=\"" + "height:30px;" + "width:30px;"
				+ "background-color:blue;" + "border-top-left-radius:45px;" + "border-bottom-right-radius:45px;" + "border-top-right-radius:45px;"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td></tr>" + "</TABLE>";

		String str = "<table><tr><td>" + "</td>" + "<td style=\"font-size:200%;\">" + "  &nbsp;&nbsp;&nbsp;&nbsp;Rapport d'intégration" + "</td>"
				+ "<tr>" + "</table>" + "<br>" + "<br>"
				+ "3 emails n'ont pas été intégrés et ont été déplacés dans le repertoire ERREUR de la boite email suivante : guillaume.yclon@cimut.fr.<br>Liste des messages n'ayant pu être traités :"
				+ "<br>" + "<br>" + "" + "<table style=\"border-collapse:collapse\">" + "    <tr>"
				+ "        <th style=\" border: 1px solid black;\">Sujet</th>" + "        <th style=\" border: 1px solid black;\">Expediteur</th>"
				+ "        <th style=\" border: 1px solid black;\">Date</th>" + "    </tr>" + "    <tr>"
				+ "        <td>EDITIQUE - /appli/cimut/inst_wildfly_dev - fatal error</td>" + "        <td>LCIR-EDITIQUE@CIMUT.FR</td>"
				+ "        <td>13/01/2016 16:02</td>" + "    </tr>" + "    <tr>" + "        <td>Alert EFIDEM</td>" + "        <td>OSB@CIMUT.FR</td>"
				+ "        <td>18/01/2016 11:13</td>" + "    </tr>" + "    <tr>"
				+ "        <td>EDITIQUE - /appli/cimut/inst_jboss_prod - fatal error</td>" + "        <td>PXES@CIMUT.FR</td>"
				+ "        <td>18/01/2016 11:22</td>" + "    </tr>" + "</table>" + "<br>"
				+ "Lien : <a href=\"https://mail.cimut.net/owa/\">https://mail.cimut.net/owa/</a><br><br><br><br>";

		Mail mail = new Mail("Object du mail", "AAAAAAA", styr, piecesJointes);
		job.setMail(mail);

		try {
			//MailSender.send(job, "http://lcid-multicanal:28080/server/multicanal");

			ObjectMapper mapper = new ObjectMapper();
			String stringigyJob = "";
			try {
				stringigyJob = mapper.writeValueAsString(job);
				byte ptext[] = stringigyJob.getBytes();
				stringigyJob = new String(ptext, "UTF-8");

			} catch (JsonProcessingException e1) {
				// 
				e1.printStackTrace();
			}
			ClientRequest request = new ClientRequest("http://lcid-multicanal:8080/server/multicanal/mail");
			request.accept("application/json");
			request.body("application/json", stringigyJob);
			if (request.post().getStatus() != 201) {
				System.out.println("Failed");
			} else {
				System.out.println("message Sent !");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR " + e.getMessage());
			fail();
		}

	}

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		System.out.println("");
		this.before = new Date();
	}

	@After
	public void terminate() {
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

}
