package fr.cimut.ged.entrant.appelmetier.utils;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import fr.cimut.habilitation.core.ApplicationCimut;
import fr.cimut.mos.MapDataMesl;
import fr.cimut.mos.ProfilCallServiceProprietaire;
import fr.cimut.mos.ProfilCallServiceXmlFirst;
import fr.cimut.mos.TechnicalField;
import fr.cimut.txot.mos_wsdl.ServiceMosStub;

/**
 * <p>
 * Les instances de la classe ClientMos Permettent d'interroger le serveur mos.<br />
 * Un howto est disponnible dans le repertoire doc de la librairie en cours.
 * </p>
 * 
 * @author ggermain
 */
public class ClientMosGed extends ProfilCallServiceXmlFirst {

	protected ClientMosGed(String url, ServiceMosStub stub, ProfilCallServiceProprietaire proprietaire) {
		super(url, stub, proprietaire);
	}

	@Override
	public String callService(final MapDataMesl mapData, final ApplicationCimut application, final TechnicalField technicalField)
			throws RemoteException, TransformerConfigurationException, ParserConfigurationException, SAXException, IOException, TransformerException,
			TransformerFactoryConfigurationError {
		return (String) super.callService(mapData, application, technicalField);
	}

}
