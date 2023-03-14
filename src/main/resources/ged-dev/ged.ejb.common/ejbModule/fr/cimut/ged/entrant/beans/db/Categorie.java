
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * <p>
 * les categories de documents - pouvant contenir des regroupements de type
 * </p>
 * 
 * @author jlebourgocq
 * 
 */

@Entity
@Table(name = "ERD_REFCAT")
public class Categorie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194914970737821175L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	@Column(name = "ERD_CDCAT")
	private String code;

	@Column(name = "ERD_LBCAT")
	private String libelle;

	@Column(name = "ERD_USER_MAJ")
	private String userMaj;

	@Column(name = "ERD_DT_MAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;

	//	/** les types rattachés a la categorie. */
	@OneToMany(cascade = CascadeType.DETACH, mappedBy = "categorie", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Type> types = new ArrayList<Type>();

	public Categorie() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getUserMaj() {
		return userMaj;
	}

	public void setUserMaj(String userMaj) {
		this.userMaj = userMaj;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	@JsonIgnore
	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}