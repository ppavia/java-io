package fr.cimut.ged.entrant.service;

import com.mongodb.BasicDBObject;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.exceptions.*;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless(mappedName = "DocumentMongo")
public class DocumentMongoService {
    private static final Logger LOGGER = Logger.getLogger(DocumentMongoService.class);

    @EJB
    MongoConnection mongoConnection;

    /**
     * Insère une nouvelle règle
     *
     * @param environnement
     * @param cmroc
     * @param document
     * @return
     * @throws CimutDocumentException
     */
    public DocumentMongo insertMongo(String environnement, String cmroc, DocumentMongo document) throws CimutDocumentException {
        DocumentHelper.checkValid(document);
        try {
            fr.cimut.ged.entrant.mongo.Manager<DocumentMongo> manager = MongoManagerFactory
                    .getDocumentManager(environnement, cmroc, getMongoClient());
            return manager.insert(document);
        } catch (Exception e) {
            LOGGER.error("Problème d'insertion de document : ", e);
            throw new CimutDocumentException("Problème d'insertion de document", e);
        }
    }

    public DocumentMongo insertMongo(String environnement, String cmroc, Document document, String demandeur)
            throws CimutDocumentException, GedeCommonException, CimutConfException {
        DocumentMongo docMongo = DocumentHelper.completeDocMongo(document);
        if (document.getJson() == null || document.getJson().getData() == null) {
            DocumentHelper.fillJsonFromDocMongo(document);
        }
        if (docMongo.getUser() == null) {
            docMongo.setUser(demandeur);
        }
        return insertMongo(environnement, cmroc, docMongo);
    }

    /**
     * Efface une règle
     *
     * @param environnement
     * @param cmroc
     * @param id
     * @throws CimutDocumentException
     */
    public void removeMongo(String environnement, String cmroc, String id) throws CimutDocumentException {
        try {
            Manager<DocumentMongo> manager = MongoManagerFactory.getDocumentManager(environnement, cmroc, getMongoClient());
            manager.remove(id);
        } catch (Exception e) {
            LOGGER.error("Problème de suppression de document : ", e);
            throw new CimutDocumentException(e.getMessage());
        }
    }

    /**
     * retourne la liste des documents mongos
     *
     * @param cmroc
     * @param codeProduit
     * @param codePack
     * @param envir
     * @return
     * @throws CimutMongoDBException
     * @throws CimutConfException
     */
    public List<DocumentMongo> listDocumentMongoPackGarantie(String cmroc, String codeGarantie, String codeProduit, String codePack, String envir)
            throws GedeException {

        String environnement = EnvironnementHelper.determinerEnvironnement(envir);

        Manager<DocumentMongo> managerDoc = MongoManagerFactory.getDocumentManager(environnement, cmroc, getMongoClient());
        BasicDBObject query = new BasicDBObject();
        BasicDBObject sort = new BasicDBObject();

        // on ne considère les champs de recherche que s'ils ne sont pas nuls
        if (StringUtils.isNotBlank(codeGarantie)) {
            query.append(GlobalVariable.ATTR_CODE_GARANTIE, codeGarantie);
        }
        if (StringUtils.isNotBlank(codePack)) {
            query.append(GlobalVariable.ATTR_IDENTIFIANT_PACK, codePack);
        }
        if (StringUtils.isNotBlank(codeProduit)) {
            query.append(GlobalVariable.ATTR_CODE_PRODUIT, codeProduit);
        }
        query.append(GlobalVariable.ATTR_SHOW_RULE, GlobalVariable.SHOW_RULE_TABLEAU_RMBT);

        // On tri sur la date d'intégration, Mongo le fait déjà probablement, mais on le force au cas ou
        sort.append(GlobalVariable.ATTR_DTINTEGRATION, -1);
        // 10/02/2020 : On affiche tous les documents, sans filtres : DIGIT1901-169
        // pour chacun des documents on ne remonte que ceux chargé en dernier
        // on monte une map sont la clé est l'assemblage des éléments de recherche pour ne remonter que le dernier élément concerné (en effet
        // un tableau de remboursement peut avoir été rechargé plusieurs fois et dans ce cas seul le dernier document nous interesse)
        //		Map<String, DocumentMongo> lastDocByKeyMap = new HashMap<String, DocumentMongo>();
        //		if (CollectionUtils.isNotEmpty(listAllDoc)) {
        //			for (DocumentMongo docMongo : listAllDoc) {
        //				if (null != docMongo) {
        //					String key = docMongo.getAttribute(GlobalVariable.ATTR_CODE_PRODUIT) + "##"
        //							+ docMongo.getAttribute(GlobalVariable.ATTR_IDENTIFIANT_PACK);
        //					DocumentMongo docAlreadyInMap = lastDocByKeyMap.get(key);
        //					if (null == docAlreadyInMap) {
        //						lastDocByKeyMap.put(key, docMongo);
        //					} else if (docMongo.getDtIntegration().isAfter(docAlreadyInMap.getDtIntegration())) {
        //						lastDocByKeyMap.put(key, docMongo);
        //					}
        //				}
        //			}
        //		}

        return managerDoc.list(query, sort);
    }

    /**
     * recuperation de l'eddocId
     *
     * @param cmroc
     * @param codeProduit
     * @param identifiantPack
     * @param environnement
     * @return
     * @throws CimutMongoDBException
     * @throws NotFoundException
     */
    public String getEddocId(String cmroc, String codeProduit, String identifiantPack, String environnement) throws GedeException {
        List<DocumentMongo> list = listDocumentMongoPackGarantie(cmroc, null, codeProduit, identifiantPack, environnement);
        if (list == null || list.isEmpty()) {
            throw new NotFoundException("Aucune occurence ne correspond aux parametres fournis");
        }
        // La liste est déjà triée du plus récent au plus ancien voir listDocumentMongoPackGarantie, on retourne donc le premier élément.
        return list.get(0).getEddocId();
    }

    public InteractionMongo getMongoClient() {
        return this.mongoConnection.getMongoClient();
    }

    public MongoConnection getMongoConnection() {
        return mongoConnection;
    }

    public void setMongoConnection(MongoConnection mongoConnection) {
        this.mongoConnection = mongoConnection;
    }
}
