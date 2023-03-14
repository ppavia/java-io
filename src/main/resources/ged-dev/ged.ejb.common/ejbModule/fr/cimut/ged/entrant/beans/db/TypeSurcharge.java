
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * <p>
 * les types de documents, avec les données éventuellement surchargées pour les mutuelles
 * </p>
 * 
 * @author jlebourgocq
 * 
 */

@Entity
@Table(name = "ERD_REFTYPE_SURCHARGE")
public class TypeSurcharge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194914970737821175L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	@ManyToOne
	@JoinColumn(name = "ERD_REFTYPE_FK", nullable = true)
	private Type type;

	@Column(name = "ERD_CDCORG")
	private String cmroc;

	@Column(name = "ERD_VISU_EXT")
	protected boolean visibleExtranet;

	@Column(name = "ERD_DUREE_VISU_EXT")
	protected Integer dureeVisibleExtranet;

	@Column(name = "ERD_NOTIF_EXT")
	protected boolean notifExtranet;

	@Column(name = "ERD_DUREE_PURGE")
	protected Integer dureePurge;

	@Column(name = "ERD_USER_MAJ")
	private String userMaj;

	@Column(name = "ERD_DT_MAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;

	public TypeSurcharge() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JsonIgnore
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
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

	public Integer getDureePurge() {
		return dureePurge;
	}

	public void setDureePurge(Integer dureePurge) {
		this.dureePurge = dureePurge;
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

}