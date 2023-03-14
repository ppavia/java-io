package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rule implements Serializable, GenericMgDbBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cmroc;
	private List<RuleCriteres> criteres = new ArrayList<RuleCriteres>();
	private boolean actif;
	private RuleDa daParameters;
	private List<String> mails = new ArrayList<String>();
	private String priority;
	private String user;
	private Date dateModif;
	private String name;

	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public List<RuleCriteres> getCriteres() {
		return criteres;
	}

	public void setCriteres(List<RuleCriteres> criteres) {
		this.criteres = criteres;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public RuleDa getService() {
		return daParameters;
	}

	public void setService(RuleDa daParameters) {
		this.daParameters = daParameters;
	}

	public List<String> getMails() {
		return mails;
	}

	public void setMails(List<String> mails) {
		this.mails = mails;
	}

	public void addMail(String mail) {
		this.mails.add(mail);
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Date getDateModif() {
		return dateModif;
	}

	public void setDateModif(Date dateModif) {
		this.dateModif = dateModif;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addCritere(RuleCriteres ruleCritere) {
		criteres.add(ruleCritere);
	}

}
