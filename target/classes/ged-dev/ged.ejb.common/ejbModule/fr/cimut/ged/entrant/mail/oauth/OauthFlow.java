package fr.cimut.ged.entrant.mail.oauth;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum OauthFlow {

	/**
	 * Transmettre le user/Mdp du compte mail en plus de l'authentification du client (clientID/clientSecret)
	 */
	ROPC
}
