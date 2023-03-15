package fr.cimut.ged.entrant.beans.appelmetier;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Sude {

	private String id;
	private String organisme;
	private String dateReception;
	private String dateDemande;
	private String dateReponse;
	private String typeDmaId;
	private String dTypeSupId;
	private String objDemande;
	private String objReponse;
	private String status;
	private String reponseTypeSupport;
	private String referenceCourrierRecommandeDemande;
	private String referenceCourrierRecommandeReponse;
	private String mail;

	private String assure;
	private String insee;
	private String partenaire;
	private String entreprise;
	private String entiteId;
	private String entiteNom;
	private String etatDadhId;
	private String reclamType = "2"; // Acte de gestion
	private String address1 = "";
	private String address2 = "";
	private String address3 = "";
	private String address4 = "";
	private String address5 = "";
	private String address6 = "";
	private String address7 = "";
	private String justifie = "O";
	private String referentielCourrier;
	private String priorite;
	private String typeDme;
	private String compteUid;

	private SudeNote note;
	private String accuseReceptionDone = "0";
	private String accuseReceptionDate;
	private String accuseReceptionCanal;

	private String RECLAM_RECLAMATION;
	private String RECLAM_JUSTIFIE;
	private String RECLAM_RESP_OC;
	private String R_SMOTRE_PRINC_ID;
	private String R_SCAURE_PRINC_ID;
	private String RECLAM_DT_RA;
	private String RECLAM_REP_ATT;
	private String CRATYPSUP_ID;
	private String RECLAM_RECOUR;
	private String RECLAM_DT_ENV_REC;
	private String RECLAM_DT_REP_REC;
	private String RECLAM_DEC_REC;
	private String RECLAM_DEROG;
	private String RECLAM_DT_ENV_DER;
	private String RECLAM_DT_REP_DER;
	private String RECLAM_DEC_DER;
	private String RECLAM_MEDIA;
	private String RECLAM_DT_ENV_MED;
	private String RECLAM_DT_REP_MED;
	private String RECLAM_DEC_MED;
	private String RECLAM_ACTION;
	private String R_SMOTRE_SEC_ID;
	private String R_SCAURE_SEC_ID;
	private String RECLAM_RESP;
	private String RECLAM_REITER;
	private String RECLAM_TRANS_EXP;
	private String RECLAM_DT_ENV_EXP;
	private String RECLAM_DT_REP_EXP;
	private String RECLAM_TEXTE_1;
	private String RECLAM_TEXTE_2;
	private String RECLAM_DT_RELAN;
	private String RECLAM_DECISION;
	private String RECLAM_ORIGINE;
	private String RECLAM_REF_LER_DEM;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssure() {
		return assure;
	}

	public void setAssure(String assure) {
		this.assure = assure;
	}

	public String getOrganisme() {
		return organisme;
	}

	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}

	public String getDateReception() {
		return dateReception;
	}

	public void setDateReception(String dateReception) {
		this.dateReception = dateReception;
	}

	public String getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(String dateDemande) {
		this.dateDemande = dateDemande;
	}

	public String getTypeDmaId() {
		return typeDmaId;
	}

	public void setTypeDmaId(String typeDmaId) {
		this.typeDmaId = typeDmaId;
	}

	public String getdTypeSupId() {
		return dTypeSupId;
	}

	public void setdTypeSupId(String dTypeSupId) {
		this.dTypeSupId = dTypeSupId;
	}

	public String getObjDemande() {
		return objDemande;
	}

	public void setObjDemande(String objDemande) {
		this.objDemande = objDemande;
	}

	public String getEntiteId() {
		return entiteId;
	}

	public void setEntiteId(String entiteId) {
		this.entiteId = entiteId;
	}

	public String getEntiteNom() {
		return entiteNom;
	}

	public void setEntiteNom(String entiteNom) {
		this.entiteNom = entiteNom;
	}

	public String getReclamType() {
		return reclamType;
	}

	public void setReclamType(String reclamType) {
		this.reclamType = reclamType;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getJustifie() {
		return justifie;
	}

	public void setJustifie(String justifie) {
		this.justifie = justifie;
	}	

	public String getReferentielCourrier() {
		return referentielCourrier;
	}

	public void setReferentielCourrier(String referentielCourrier) {
		this.referentielCourrier = referentielCourrier;
	}

	public String getPriorite() {
		return priorite;
	}

	public void setPriorite(String priorite) {
		this.priorite = priorite;
	}

	public String getCompteUid() {
		return compteUid;
	}

	public void setCompteUid(String compteUid) {
		this.compteUid = compteUid;
	}

	public String getPartenaire() {
		return partenaire;
	}

	public void setPartenaire(String partenaire) {
		this.partenaire = partenaire;
	}

	public String getEntreprise() {
		return entreprise;
	}

	public void setEntreprise(String entreprise) {
		this.entreprise = entreprise;
	}

	public String getInsee() {
		return insee;
	}

	public void setInsee(String insee) {
		this.insee = insee;
	}

	public String getAddress5() {
		return address5;
	}

	public void setAddress5(String address5) {
		this.address5 = address5;
	}

	public String getAddress6() {
		return address6;
	}

	public void setAddress6(String address6) {
		this.address6 = address6;
	}

	public String getAddress7() {
		return address7;
	}

	public void setAddress7(String address7) {
		this.address7 = address7;
	}

	public SudeNote getNote() {
		return note;
	}

	public void setNote(SudeNote note) {
		this.note = note;
	}

	public void setTypeDemandeur(String typeDme) {
		this.typeDme = typeDme;
	}

	public String getTypeDemandeur() {
		return this.typeDme;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccuseReceptionDone() {
		return this.accuseReceptionDone;
	}

	public String getAccuseReceptionDate() {
		return this.accuseReceptionDate;
	}

	public String getAccuseReceptionCanal() {
		return this.accuseReceptionCanal;
	}

	public void setAccuseReceptionDone(String accuseReceptionDone) {
		this.accuseReceptionDone = accuseReceptionDone;
	}

	public void setAccuseReceptionDate(String accuseReceptionDate) {
		this.accuseReceptionDate = accuseReceptionDate;
	}

	public void setAccuseReceptionCanal(String accuseReceptionCanal) {
		this.accuseReceptionCanal = accuseReceptionCanal;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getEtatDadhId() {
		return etatDadhId;
	}

	public void setEtatDadhId(String etatDadhId) {
		this.etatDadhId = etatDadhId;
	}

	public String getDateReponse() {
		return dateReponse;
	}

	public void setDateReponse(String dateReponse) {
		this.dateReponse = dateReponse;
	}

	public String getReponseTypeSupport() {
		return reponseTypeSupport;
	}

	public void setReponseTypeSupport(String reponseTypeSupport) {
		this.reponseTypeSupport = reponseTypeSupport;
	}

	public String getObtReponse() {
		return objReponse;
	}

	public void setObjReponse(String objReponse) {
		this.objReponse = objReponse;
	}

	public String getReferenceCourrierRecommandeDemande() {
		return referenceCourrierRecommandeDemande;
	}

	public void setReferenceCourrierRecommandeDemande(String referenceCourrierRecommandeDemande) {
		this.referenceCourrierRecommandeDemande = referenceCourrierRecommandeDemande;
	}

	public String getReferenceCourrierRecommandeReponse() {
		return referenceCourrierRecommandeReponse;
	}

	public void setReferenceCourrierRecommandeReponse(String referenceCourrierRecommandeReponse) {
		this.referenceCourrierRecommandeReponse = referenceCourrierRecommandeReponse;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getTypeDme() {
		return typeDme;
	}

	public void setTypeDme(String typeDme) {
		this.typeDme = typeDme;
	}

	public String getRECLAM_RECLAMATION() {
		return RECLAM_RECLAMATION;
	}

	public void setRECLAM_RECLAMATION(String rECLAM_RECLAMATION) {
		RECLAM_RECLAMATION = rECLAM_RECLAMATION;
	}

	public String getRECLAM_JUSTIFIE() {
		return RECLAM_JUSTIFIE;
	}

	public void setRECLAM_JUSTIFIE(String rECLAM_JUSTIFIE) {
		RECLAM_JUSTIFIE = rECLAM_JUSTIFIE;
	}

	public String getRECLAM_RESP_OC() {
		return RECLAM_RESP_OC;
	}

	public void setRECLAM_RESP_OC(String rECLAM_RESP_OC) {
		RECLAM_RESP_OC = rECLAM_RESP_OC;
	}

	public String getR_SMOTRE_PRINC_ID() {
		return R_SMOTRE_PRINC_ID;
	}

	public void setR_SMOTRE_PRINC_ID(String r_SMOTRE_PRINC_ID) {
		R_SMOTRE_PRINC_ID = r_SMOTRE_PRINC_ID;
	}

	public String getR_SCAURE_PRINC_ID() {
		return R_SCAURE_PRINC_ID;
	}

	public void setR_SCAURE_PRINC_ID(String r_SCAURE_PRINC_ID) {
		R_SCAURE_PRINC_ID = r_SCAURE_PRINC_ID;
	}

	public String getRECLAM_DT_RA() {
		return RECLAM_DT_RA;
	}

	public void setRECLAM_DT_RA(String rECLAM_DT_RA) {
		RECLAM_DT_RA = rECLAM_DT_RA;
	}

	public String getRECLAM_REP_ATT() {
		return RECLAM_REP_ATT;
	}

	public void setRECLAM_REP_ATT(String rECLAM_REP_ATT) {
		RECLAM_REP_ATT = rECLAM_REP_ATT;
	}

	public String getCRATYPSUP_ID() {
		return CRATYPSUP_ID;
	}

	public void setCRATYPSUP_ID(String cRATYPSUP_ID) {
		CRATYPSUP_ID = cRATYPSUP_ID;
	}

	public String getRECLAM_RECOUR() {
		return RECLAM_RECOUR;
	}

	public void setRECLAM_RECOUR(String rECLAM_RECOUR) {
		RECLAM_RECOUR = rECLAM_RECOUR;
	}

	public String getRECLAM_DT_ENV_REC() {
		return RECLAM_DT_ENV_REC;
	}

	public void setRECLAM_DT_ENV_REC(String rECLAM_DT_ENV_REC) {
		RECLAM_DT_ENV_REC = rECLAM_DT_ENV_REC;
	}

	public String getRECLAM_DT_REP_REC() {
		return RECLAM_DT_REP_REC;
	}

	public void setRECLAM_DT_REP_REC(String rECLAM_DT_REP_REC) {
		RECLAM_DT_REP_REC = rECLAM_DT_REP_REC;
	}

	public String getRECLAM_DEC_REC() {
		return RECLAM_DEC_REC;
	}

	public void setRECLAM_DEC_REC(String rECLAM_DEC_REC) {
		RECLAM_DEC_REC = rECLAM_DEC_REC;
	}

	public String getRECLAM_DEROG() {
		return RECLAM_DEROG;
	}

	public void setRECLAM_DEROG(String rECLAM_DEROG) {
		RECLAM_DEROG = rECLAM_DEROG;
	}

	public String getRECLAM_DT_ENV_DER() {
		return RECLAM_DT_ENV_DER;
	}

	public void setRECLAM_DT_ENV_DER(String rECLAM_DT_ENV_DER) {
		RECLAM_DT_ENV_DER = rECLAM_DT_ENV_DER;
	}

	public String getRECLAM_DT_REP_DER() {
		return RECLAM_DT_REP_DER;
	}

	public void setRECLAM_DT_REP_DER(String rECLAM_DT_REP_DER) {
		RECLAM_DT_REP_DER = rECLAM_DT_REP_DER;
	}

	public String getRECLAM_DEC_DER() {
		return RECLAM_DEC_DER;
	}

	public void setRECLAM_DEC_DER(String rECLAM_DEC_DER) {
		RECLAM_DEC_DER = rECLAM_DEC_DER;
	}

	public String getRECLAM_MEDIA() {
		return RECLAM_MEDIA;
	}

	public void setRECLAM_MEDIA(String rECLAM_MEDIA) {
		RECLAM_MEDIA = rECLAM_MEDIA;
	}

	public String getRECLAM_DT_ENV_MED() {
		return RECLAM_DT_ENV_MED;
	}

	public void setRECLAM_DT_ENV_MED(String rECLAM_DT_ENV_MED) {
		RECLAM_DT_ENV_MED = rECLAM_DT_ENV_MED;
	}

	public String getRECLAM_DT_REP_MED() {
		return RECLAM_DT_REP_MED;
	}

	public void setRECLAM_DT_REP_MED(String rECLAM_DT_REP_MED) {
		RECLAM_DT_REP_MED = rECLAM_DT_REP_MED;
	}

	public String getRECLAM_DEC_MED() {
		return RECLAM_DEC_MED;
	}

	public void setRECLAM_DEC_MED(String rECLAM_DEC_MED) {
		RECLAM_DEC_MED = rECLAM_DEC_MED;
	}

	public String getRECLAM_ACTION() {
		return RECLAM_ACTION;
	}

	public void setRECLAM_ACTION(String rECLAM_ACTION) {
		RECLAM_ACTION = rECLAM_ACTION;
	}

	public String getObjReponse() {
		return objReponse;
	}

	public String getR_SMOTRE_SEC_ID() {
		return R_SMOTRE_SEC_ID;
	}

	public void setR_SMOTRE_SEC_ID(String r_SMOTRE_SEC_ID) {
		R_SMOTRE_SEC_ID = r_SMOTRE_SEC_ID;
	}

	public String getR_SCAURE_SEC_ID() {
		return R_SCAURE_SEC_ID;
	}

	public void setR_SCAURE_SEC_ID(String r_SCAURE_SEC_ID) {
		R_SCAURE_SEC_ID = r_SCAURE_SEC_ID;
	}

	public String getRECLAM_RESP() {
		return RECLAM_RESP;
	}

	public void setRECLAM_RESP(String rECLAM_RESP) {
		RECLAM_RESP = rECLAM_RESP;
	}

	public String getRECLAM_REITER() {
		return RECLAM_REITER;
	}

	public void setRECLAM_REITER(String rECLAM_REITER) {
		RECLAM_REITER = rECLAM_REITER;
	}

	public String getRECLAM_TRANS_EXP() {
		return RECLAM_TRANS_EXP;
	}

	public void setRECLAM_TRANS_EXP(String rECLAM_TRANS_EXP) {
		RECLAM_TRANS_EXP = rECLAM_TRANS_EXP;
	}

	public String getRECLAM_DT_ENV_EXP() {
		return RECLAM_DT_ENV_EXP;
	}

	public void setRECLAM_DT_ENV_EXP(String rECLAM_DT_ENV_EXP) {
		RECLAM_DT_ENV_EXP = rECLAM_DT_ENV_EXP;
	}

	public String getRECLAM_DT_REP_EXP() {
		return RECLAM_DT_REP_EXP;
	}

	public void setRECLAM_DT_REP_EXP(String rECLAM_DT_REP_EXP) {
		RECLAM_DT_REP_EXP = rECLAM_DT_REP_EXP;
	}

	public String getRECLAM_TEXTE_1() {
		return RECLAM_TEXTE_1;
	}

	public void setRECLAM_TEXTE_1(String rECLAM_TEXTE_1) {
		RECLAM_TEXTE_1 = rECLAM_TEXTE_1;
	}

	public String getRECLAM_TEXTE_2() {
		return RECLAM_TEXTE_2;
	}

	public void setRECLAM_TEXTE_2(String rECLAM_TEXTE_2) {
		RECLAM_TEXTE_2 = rECLAM_TEXTE_2;
	}

	public String getRECLAM_DT_RELAN() {
		return RECLAM_DT_RELAN;
	}

	public void setRECLAM_DT_RELAN(String rECLAM_DT_RELAN) {
		RECLAM_DT_RELAN = rECLAM_DT_RELAN;
	}

	public String getRECLAM_DECISION() {
		return RECLAM_DECISION;
	}

	public void setRECLAM_DECISION(String rECLAM_DECISION) {
		RECLAM_DECISION = rECLAM_DECISION;
	}

	public String getRECLAM_ORIGINE() {
		return RECLAM_ORIGINE;
	}

	public void setRECLAM_ORIGINE(String rECLAM_ORIGINE) {
		RECLAM_ORIGINE = rECLAM_ORIGINE;
	}

	public String getRECLAM_REF_LER_DEM() {
		return RECLAM_REF_LER_DEM;
	}

	public void setRECLAM_REF_LER_DEM(String rECLAM_REF_LER_DEM) {
		RECLAM_REF_LER_DEM = rECLAM_REF_LER_DEM;
	}
	
	

}
