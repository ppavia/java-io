package fr.cimut.ged.entrant.mail;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.cimut.ged.entrant.mail.oauth.MailOAuthConfiguration;
import org.apache.log4j.Logger;

import com.sun.mail.util.MailSSLSocketFactory;

import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CompteMail {

	private static final Logger LOGGER = Logger.getLogger(CompteMail.class);

	private static final SimpleDateFormat UPDATE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

	@XmlTransient
	private String email = "";

	private String cmroc = "";
	private String environnement = "";
	private String host = "";
	private int port = 0;
	private String protocole = "";
	private String login = "";
	private String password = "";

	@XmlElementWrapper
	@XmlElement(name = "inBox")
	private List<String> inBoxes = new ArrayList<String>();

	@XmlElementWrapper
	@XmlElement(name = "rapportEmail")
	private List<String> rapportEmails = new ArrayList<String>();

	private String accusReceptionTemplateId = "";
	private String accusReceptionExpediteur = "";

	private String lastUpdateAuthor = "";
	private String lastUpdateDate = "";

	@XmlElement(name = "authProvider")
	private MailOAuthConfiguration authProvider = new MailOAuthConfiguration();

	public Properties getProperties() throws CimutMailException {
		Properties properties = new Properties();
		properties.put(String.format("mail.%s.host", this.protocole), this.host);
		properties.put(String.format("mail.%s.port", this.protocole), this.port);
		properties.put(String.format("mail.%s.timeout", this.protocole), String.valueOf(60 * 1000));
		properties.put(String.format("mail.%s.connectiontimeout", this.protocole), String.valueOf(60 * 1000));
		Boolean partialFetch = GlobalVariable.getPartialFecthForAttachement();
		if (null != partialFetch) {
			properties.put(String.format("mail.%s.partialfetch", this.protocole), partialFetch.booleanValue());
		}

		MailSSLSocketFactory sf;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			throw new CimutMailException(e);
		}
		sf.setTrustAllHosts(true);
		properties.put(String.format("mail.%s.ssl.trust", this.protocole), "*");
		properties.put(String.format("mail.%s.ssl.socketFactory", this.protocole), sf);

		//properties.setProperty( String.format("mail.%s.socketFactory.fallback", this.protocole),"false");
		properties.setProperty(String.format("mail.%s.socketFactory.port", this.protocole), String.valueOf(this.port));
		properties.put(String.format("mail.%s.ssl.trust", this.protocole), "*");
		//System.setProperty("javax.net.ssl.trustStore","/src/test/resources/myTrustStore");
		//System.setProperty("javax.net.ssl.trustStorePassword","gyclon");
		//properties.setProperty( String.format("mail.%s.ssl.checkserveridentity", this.protocole), "false");

		Logger.getLogger(CompteMail.class).debug(properties.toString());

		return properties;
	}

	public Properties getPropertiesForOAuth() throws CimutMailException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("XOAUTH2 login: " + host +" "+ port +" "+ login);
		}

		// Utiliser le prototype de configuration
		Properties properties = this.getProperties();

		// Appliquer le spécifique pour XOAUTH2

		properties.setProperty(
				String.format("mail.%s.socketFactory.fallback", protocole),
				"false");

		properties.put(String.format("mail.%s.ssl.enable", protocole), "true");
		properties.put(String.format("mail.%s.auth.mechanisms", protocole), "XOAUTH2");
		properties.put(String.format("mail.%s.user", protocole), login);
		properties.put(String.format("mail.%s.auth.login.disable", protocole), "true");
		properties.put(String.format("mail.%s.auth.plain.disable", protocole), "true");

		return properties;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocole() {
		return protocole;
	}

	public void setProtocole(String protocole) {
		this.protocole = protocole;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public String getEnvironnement() {
		return environnement;
	}

	public void setEnvironnement(String environnement) {
		this.environnement = environnement;
	}

	public List<String> getInBoxes() {
		return inBoxes;
	}

	public void setInBoxes(List<String> inBoxes) {
		this.inBoxes = inBoxes;
	}

	public void setRapportEmails(List<String> list_email) {
		this.rapportEmails = list_email;
	}

	public List<String> getRapportEmails() {
		return this.rapportEmails;
	}

	public String getAccusReceptionExpediteur() {
		return accusReceptionExpediteur;
	}

	public void setAccusReceptionExpediteur(String accusReceptionExpediteur) {
		this.accusReceptionExpediteur = accusReceptionExpediteur;
	}

	public String getAccusReceptionTemplateId() {
		return accusReceptionTemplateId;
	}

	public void setAccusReceptionTemplateId(String accusReceptionTemplateId) {
		this.accusReceptionTemplateId = accusReceptionTemplateId;
	}

	public String getLastUpdateAuthor() {
		return lastUpdateAuthor;
	}

	public void setLastUpdateAuthor(String lastUpdateAuthor) {
		this.lastUpdateAuthor = lastUpdateAuthor;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDateToFormat) {
		this.lastUpdateDate = UPDATE_DATE_FORMAT.format(lastUpdateDateToFormat);
	}

	public MailOAuthConfiguration getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(MailOAuthConfiguration authProvider) {
		this.authProvider = authProvider;
	}
}