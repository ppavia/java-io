package fr.cimut.ged.entrant.integration.ejb;

import javax.ejb.Remote;

@Remote
public interface RebuildRemote {
	/**
	 * Cree des fichiers dans lequel on serialize le json pour le rebuild
	 * 
	 * @param organisme
	 *            (organisme dont on veut le dump)
	 * @param environnement
	 *            (environnement cible, obligatoire lorsque le serveur de GEDe est en mode multi-environnement)
	 * 
	 * @return String message de sortie (erreur ou OK)
	 */
	public String rebuild(String organisme, String environnement);
}
