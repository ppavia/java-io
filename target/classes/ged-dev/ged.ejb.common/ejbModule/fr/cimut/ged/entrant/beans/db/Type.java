
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * <p>
 * les types de documents
 * </p>
 * 
 * @author jlebourgocq
 * 
 */

@Entity
@Table(name = "ERD_REFTYPE")

//@formatter:off 
@NamedQueries({ 
	@NamedQuery(name = "Type.findByCriteria", query =
	" select t from Type t " +
	" where (t.categorie.id = :idCategorie or  :idCategorie is null) " +
	" and (t.code = :code or :code is null) "),
	@NamedQuery(name = "Type.findByCodeList", query =
	" select t from Type t " +
	" where t.code in :codeList "),
	@NamedQuery(name = "Type.findByCodeDocument", query =
	" select t.libelle from Type t " +
	" where t.code = :code "),
	@NamedQuery(name = "Type.countTypeWithCode", query =
	" select count(t.id) from Type t " +
	" where (t.code = :code ) "),
	@NamedQuery(name = "Type.countTypeByCodeAndLibelle", query =
	" select count(t.id) from Type t " +
	" where (t.code like :code ) and (t.libelle like :libelle )")
})
//@formatter:on 
public class Type implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194914970737821175L;

	public static final String CODE_INCONNU = "INCONNU";

	// ici la declaration des types directement manipulés par l'application
	public static final String CODE_MODELE_COURRIER = "MODELE_COURRIER";
	public static final String CODE_TABLEAU_GARANTIE = "TABLEAU_GARANTIE";
	public static final String CODE_RAPPORT_INTEGRATION_IDOC = "RAPPORT_INTEGRATION_IDOC";
	public static final String CODE_PIECE_JOINTE_SUDE = "PIECE_JOINTE_SUDE";
	public static final String CODE_MAIL_DEMATERIALISE = "MAIL_DEMATERIALISE";
	public static final String CODE_COURRIER_DEMATERIALISE = "COURRIER_DEMATERIALISE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ERD_REFCAT_FK", nullable = true)
	private Categorie categorie;

	@Column(name = "ERD_CDTYPE")
	private String code;

	@Column(name = "ERD_LBTYPE")
	private String libelle;

	@Column(name = "ERD_TYPE_E_S")
	@Enumerated(EnumType.STRING)
	private TypeEntrantSortant typeEntrantSortant;

	@Column(name = "ERD_VISU_EXT")
	protected boolean visibleExtranet;

	@Column(name = "ERD_DUREE_VISU_EXT")
	protected Integer dureeVisibleExtranet;

	@Column(name = "ERD_NOTIF_EXT")
	protected boolean notifExtranet;

	@Column(name = "ERD_DUREE_ARCH")
	protected Integer dureeArchivage;

	@Column(name = "ERD_DUREE_PURGE")
	protected Integer dureePurge;

	@Column(name = "ERD_INTEG_IDOC")
	protected boolean integrableIdoc;

	@Column(name = "ERD_USER_MAJ")
	private String userMaj;

	@Column(name = "ERD_DT_MAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;

	/** les documents erd ref doc. */
	@OneToMany(cascade = CascadeType.DETACH, mappedBy = "type", fetch = FetchType.LAZY)
	private List<Document> documents = new ArrayList<Document>();

	/** les surcharges. */
	@OneToMany(cascade = CascadeType.DETACH, mappedBy = "type", fetch = FetchType.LAZY)
	private List<Document> typesSurcharge = new ArrayList<Document>();

	public Type() {
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
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

	public TypeEntrantSortant getTypeEntrantSortant() {
		return typeEntrantSortant;
	}

	public void setTypeEntrantSortant(TypeEntrantSortant typeEntrantSortant) {
		this.typeEntrantSortant = typeEntrantSortant;
	}

	public boolean isVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
	}

	public Integer getDureeVisibleExtranet() {
		return dureeVisibleExtranet;
	}

	public void setDureeVisibleExtranet(Integer dureeVisibleExtranet) {
		this.dureeVisibleExtranet = dureeVisibleExtranet;
	}

	public boolean isNotifExtranet() {
		return notifExtranet;
	}

	public void setNotifExtranet(boolean notifExtranet) {
		this.notifExtranet = notifExtranet;
	}

	public Integer getDureeArchivage() {
		return dureeArchivage;
	}

	public void setDureeArchivage(Integer dureeArchivage) {
		this.dureeArchivage = dureeArchivage;
	}

	public Integer getDureePurge() {
		return dureePurge;
	}

	public void setDureePurge(Integer dureePurge) {
		this.dureePurge = dureePurge;
	}

	public boolean isIntegrableIdoc() {
		return integrableIdoc;
	}

	public void setIntegrableIdoc(boolean integrableIdoc) {
		this.integrableIdoc = integrableIdoc;
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
	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	@JsonIgnore
	public List<Document> getTypesSurcharge() {
		return typesSurcharge;
	}

	public void setTypesSurcharge(List<Document> typesSurcharge) {
		this.typesSurcharge = typesSurcharge;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}