package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Table permettant de connaitre la version du schéma de base de données, celle-ci doit être en phase avec la version
 * applicative
 * 
 * @author jlebourgocq
 *
 */
@Entity
@Table(name = "Editique_Version")
public class EditiqueVersion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -510723487018714504L;

	/**
	 * Automatic primary key.
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Field version : numero de la version mise a jour
	 */
	@Column(name = "VERSION")
	private String version;

	/**
	 * Field description : description de la version
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	/**
	 * Field install_Date : date d'installation de la version
	 */
	@Column(name = "INSTALL_DATE")
	private String installDate;

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the installDate
	 */
	public String getInstallDate() {
		return installDate;
	}

	/**
	 * @param installDate
	 *            the installDate to set
	 */
	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}
