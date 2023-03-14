
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

@Entity
@Table(name = "ERD_HIST_GED")
@NamedQueries({ @NamedQuery(name = "Historique.getAllById", query = "select h from Historique h where h.docId = :id"),
		@NamedQuery(name = "Historique.updateAllById", query = "update Historique h set h.docId = :newId where h.docId = :oldId") })
public class Historique implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "historique_sequence", sequenceName = "S_ERD_HIST_GED", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historique_sequence")
	@Column(name = "ERD_ID", unique = true, nullable = false, precision = 10, scale = 0)
	private long id;

	@Column(name = "ERD_LBNMFD")
	private String docId;

	@Column(name = "ERD_MODIF")
	private String modification;

	@Column(name = "ERD_USER_MAJ")
	private String userMaj;

	@Column(name = "ERD_DT_MAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;

	public Historique() {

	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModification() {
		return modification;
	}

	public void setModification(String modification) {
		this.modification = modification;
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

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

}