package ejb;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.integration.ejb.RebuildRemote;

public class rebuildTest {

	private Date before;

	private RebuildRemote process;

	private final static String ejbJndi = "ged.production.ear/ged.entrant.ejb.integration/Rebuild!fr.cimut.ged.entrant.integration.ejb.RebuildRemote";

	@Before
	public void setUp() throws Exception {

		Properties clientProps = new Properties();

		clientProps.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		clientProps.put("remote.connections", "default");
		clientProps.put("endpoint.name", "client-endpoint");
		clientProps.put("remote.connection.default.port", "4447");
		clientProps.put("remote.connection.default.host", "localhost");
		clientProps.put("remote.connection.default.username", "cimut");
		clientProps.put("remote.connection.default.password", "Quimper29");
		clientProps.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

		EJBClientConfiguration ejbConfig = new PropertiesBasedEJBClientConfiguration(clientProps);
		ContextSelector<EJBClientContext> cntxSelector = new ConfigBasedEJBClientContextSelector(ejbConfig);
		EJBClientContext.setSelector(cntxSelector);

		final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		process = (RebuildRemote) context.lookup("ejb:" + ejbJndi);

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

	// cree un fichier d'echange dans le repertoire add du module d'indexation. 
	// C'est dans la minute qui suit que le module d'indexation va consommer ce fichier pour repeupler cf. Sebastien Quelen 
	// /!\ IL FAUT BIEN VIDER LE CONTENU MONGO DB AVANT DE LE REPEUPLER DEPUIS CE TEST U /!\
	//@Test
	public void rebuild() {

		try {
			String message = process.rebuild("8080", "RECV");
			System.out.println(message);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
