package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.cimut.ged.entrant.utils.NoObjectIdSerializer;

public class BlackList implements Serializable, GenericMgDbBean {

	private static final long serialVersionUID = 1L;

	@org.jongo.marshall.jackson.oid.ObjectId
	@JsonSerialize(using = NoObjectIdSerializer.class)
	@JsonProperty("_id")
	private String id;

	@NotNull
	private String email;

	@NotNull
	private String cmroc;

	@NotNull
	private long dtModification;

	@NotNull
	private String userModification;

	private boolean created;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the userModification
	 */
	public String getUserModification() {
		return userModification;
	}

	/**
	 * @param userModification
	 *            the userModification to set
	 */
	public void setUserModification(String userModification) {
		this.userModification = userModification;
	}

	/**
	 * @return the dtModification
	 */
	public long getDtModification() {
		return dtModification;
	}

	/**
	 * @param dtModification
	 *            the dtModification to set
	 */
	public void setDtModification(long dtModification) {
		this.dtModification = dtModification;
	}

	/**
	 * @return the cmroc
	 */
	public String getCmroc() {
		return cmroc;
	}

	/**
	 * @param cmroc
	 *            the cmroc to set
	 */
	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	/**
	 * @return the created
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(boolean created) {
		this.created = created;
	}

}
