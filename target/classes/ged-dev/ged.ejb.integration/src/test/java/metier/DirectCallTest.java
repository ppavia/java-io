package metier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.junit.After;
import org.junit.Before;

public class DirectCallTest {

	private Date before;

	//@Test
	public void injectTest() throws IOException {

		String xml = "<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns2:meslRequest xmlns:ns2=\"http://txot.cimut.fr/mos.wsdl\"><routage>9916</routage><prog>M-EDDM-EDDM</prog><servicename>SVCMESL</servicename><ctxtid>0</ctxtid><cmrocid>9916</cmrocid><ecran></ecran><nexttpr>M-STARMESL</nexttpr><options>&lt;userLogin>gyclon9916&lt;/userLogin></options><ip>0:0:0:0:0:0:0:1</ip><message>&lt;ROOT>&lt;PARAM_CNX>&lt;CNX_CMROC>9916&lt;/CNX_CMROC>&lt;CNX_NEXTTPR>M-EDDM-EDDM&lt;/CNX_NEXTTPR>&lt;CNX_UTIL_NOMTRACE>gyclon9916&lt;/CNX_UTIL_NOMTRACE>&lt;CNX_MOTPAS/>&lt;CNX_VISUDATASENSIBLE>N&lt;/CNX_VISUDATASENSIBLE>&lt;CNX_NOMTX>SVCMESL&lt;/CNX_NOMTX>&lt;CNX_DATASENSIBLE>O&lt;/CNX_DATASENSIBLE>&lt;CNX_DRCMSI>ICDMS&lt;/CNX_DRCMSI>&lt;CNX_PARAM/>&lt;CNX_PRESSCAN/>&lt;CNX_NOMAGENDA/>&lt;CNX_SIGNATURE/>&lt;CNX_UTIL-ECRAN>GY00&lt;/CNX_UTIL-ECRAN>&lt;CNX_IDENTIFIANT>gyclon9916&lt;/CNX_IDENTIFIANT>&lt;CNX_NOMSERV/>&lt;CNX_REFLT/>&lt;CNX_TRIGRAMME>gyc&lt;/CNX_TRIGRAMME>&lt;CNX_MNT-REG>2&lt;/CNX_MNT-REG>&lt;CNX_IDENTCOURT>gyclon&lt;/CNX_IDENTCOURT>&lt;CNX_INTERLOC>03&lt;/CNX_INTERLOC>&lt;CNX_PROFILMET>ALL;OUT-CON;CIMUT-REF;CIMUT-PAR;GEST-TAR;CIMUT-PATCH&lt;/CNX_PROFILMET>&lt;CNX_INTGRTW/>&lt;CNX_INOC>BT&lt;/CNX_INOC>&lt;CNX_NODEPT>14&lt;/CNX_NODEPT>&lt;CNX_NOAIR>55&lt;/CNX_NOAIR>&lt;/PARAM_CNX>&lt;PARAM_ZT>&lt;ZT_CMSI>GC&lt;/ZT_CMSI>&lt;ZT_OBJET/>&lt;ZT_ACTION/>&lt;/PARAM_ZT>&lt;PARAM_MESL>&lt;EDDOC_INTIT>infermi - Libell..&lt;/EDDOC_INTIT>&lt;EDDOC_UTI_DEM>_import_courrier&lt;/EDDOC_UTI_DEM>&lt;CTSAN_S_SECTION/>&lt;COURRIER_ID>infermi&lt;/COURRIER_ID>&lt;PART_NIV/>&lt;ENTRPRIS_ID/>&lt;EDDOC_TYPE_FICH>.pdf&lt;/EDDOC_TYPE_FICH>&lt;EDDOC_CMROC>9916&lt;/EDDOC_CMROC>&lt;EDDOC_CD_CANAL/>&lt;PART_ID/>&lt;EDDOC_CD_SENS>R&lt;/EDDOC_CD_SENS>&lt;EDDOC_LIB_CANAL>Courrier&lt;/EDDOC_LIB_CANAL>&lt;ETATDOC_ID>0200&lt;/ETATDOC_ID>&lt;ASSU_S_ASSURE>421971&lt;/ASSU_S_ASSURE>&lt;EDDOC_DT_PRVARCH/>&lt;TYPPART_ID/>&lt;EDDOC_DT_PRVPURGE/>&lt;/PARAM_MESL>&lt;/ROOT></message></ns2:meslRequest></soapenv:Body></soapenv:Envelope>";
		String url = "http://lisoadev.cimut.fr:8011/ProxyHttpMosIntv3";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Axis2");
		con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		con.setRequestProperty("SOAPAction", "");
		con.setDoOutput(true);

		con.connect();

		// Send post request
		OutputStream reqStream = con.getOutputStream();
		reqStream.write(xml.getBytes());
		reqStream.close();

		// reading response
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + xml);
		System.out.println("Response Code : " + responseCode);
		System.out.println("Response Msg : " + con.getResponseMessage());

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
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
