package fr.cimut.ged.entrant.utils;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.ids.EditicId;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentDto;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentInfoComp;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class GedeIdHelper {
    private static final String SEP_ID = "_";
    private static Random rand = new Random();

    private GedeIdHelper() {}

    /**
     * Transforme une string eddocId en un tableau d'entier [idStar,tsStar]
     * @param idDoc
     * @return
     * @throws CimutDocumentException
     */
    public static long[] splitEddocId(String idDoc) throws CimutDocumentException {
        String[] splitId = idDoc.split("_");
        long[] idTs = new long[2];
        if (splitId.length != 2) {
            throw new CimutDocumentException("L'id du document doit être de la forme 123456_987654");
        }
        try {
            idTs[0] = Long.parseLong(splitId[0]);
            idTs[1] = Long.parseLong(splitId[1]);
        } catch (NumberFormatException e) {
            throw new CimutDocumentException(String.format("L'id %s n'est pas composé de deux nombres séparés du caractère '_'", Arrays.toString(splitId)), e);
        }
        return idTs;
    }

    /**
     *
     * @param listDocMongo
     * @return
     */
    public static List<String> extractEddocIdFromDocMongo(Collection<DocumentMongo> listDocMongo)
            throws CimutDocumentException {
        List<String> eddocList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(listDocMongo)) {
            for (DocumentMongo doc : listDocMongo) {
                String eddoc = normalizeEddocId(doc.getEddocId());
                if (StringUtils.isNotBlank(eddoc)) {
                    eddocList.add(eddoc);
                }
            }
        }
        return eddocList;
    }

    /**
     * recuperation du rang de raatchement par rapport a l'eddocId
     *
     * @param eddocId
     * @param docStarwebList
     * @return
     */
    public static DocumentInfoComp getInfoComplementaireForEddocId(String eddocId, List<DocumentDto> docStarwebList) {
        if (StringUtils.isNotBlank(eddocId) && CollectionUtils.isNotEmpty(docStarwebList)) {
            for (DocumentDto doc : docStarwebList) {
                if (eddocId.equals(doc.getEddocIdWithoutZero())) {
                    return doc.getInfoComp();
                }
            }
        }
        return null;
    }

    /**
     * recupere un doc de erdrefDoc qui match avec l'id
     *
     * @param eddocIdWithoutZero
     * @param erdRefDocList
     * @return
     */
    public static Document getMatchingEddocInrErdRefDoc(String eddocIdWithoutZero, Collection<Document> erdRefDocList) {
        if (StringUtils.isNotBlank(eddocIdWithoutZero) && CollectionUtils.isNotEmpty(erdRefDocList)) {
            for (Document erdRefDoc : erdRefDocList) {
                if (eddocIdWithoutZero.equals(erdRefDoc.getEddocId())) {
                    return erdRefDoc;
                }
            }
        }
        // on a pas trouvé le doc
        return null;
    }

    /**
     * Convertit tous les id format starwebWS des documents en id format editique
     * @param docStarwebList
     * @return Une map avec key = idstar, value = object EditicId contenant les données d'id du document
     */
    public static Map<Long, EditicId> getEditicIds(List<DocumentDto> docStarwebList) {
        Map<Long, EditicId> editicIds = new HashMap<Long, EditicId>();
        for (DocumentDto docDto : docStarwebList) {
            EditicId editicId = GedeIdHelper.convertIdStarwebToEditic(docDto.getId());
            editicIds.put(editicId.getIdStar(), editicId);
        }
        return editicIds;
    }

    public static List<String> getIdsStarwebWithoutZero(List<DocumentDto> docStarwebList) {
        List<String> eddocList = new ArrayList<String>();
        for (DocumentDto docDto : docStarwebList) {
            eddocList.add(docDto.getEddocIdWithoutZero());
        }
        return eddocList;
    }

    /**
     * Transforme une string eddocId en une string, résultat d'une concaténation de deux long séparés par le caractère '_' :
     * in -> 0000123456_987654
     * step -> [0000123456,987654]
     * out -> 123456_987654
     * @param eddocToNormalize
     * @return
     */
    public static String normalizeEddocId(String eddocToNormalize) throws CimutDocumentException {
        long[] idTsStar = splitEddocId(eddocToNormalize);
        return idTsStar[0] + "_" +idTsStar[1];
    }

    /**
     * Convertit un id starweb en id editic
     * format id starweb : 0000123456_987654
     * format id editic :<br/>
     * idstar = 123456
     * tsstar = 987654
     * @param starwebId
     * @return
     */
    public static EditicId convertIdStarwebToEditic (String starwebId) {
        EditicId editicId = new EditicId();
        // split de l'id starweb
        String[] ids = starwebId.split(SEP_ID);
        // suppression des 0000 et conversion
        if (ids.length == 2) {
            editicId.setIdStar(Long.parseLong(ids[0]));
            editicId.setTsStar(Long.parseLong(ids[1]));
        }
        return editicId;
    }

    /**
     * Convertit un id mongo en id editic
     * @param mongoDbId
     * @return
     */
    public static EditicId convertIdMongoToEditic (String mongoDbId) {
        EditicId editicId = new EditicId();
        editicId.setLbnmfd(mongoDbId);
        return editicId;
    }

    /**
     * Génère un identifiant eddm sous la forma attendue pour le lien entre le référencement technique et le
     * référencement fonctionnel
     *
     * revert currentTimeMillis to date : System.out.println(new SimpleDateFormat("MMM dd,yyyy HH:mm").format(new
     * Date(System.currentTimeMillis()))); sous linux : date -d @currentTimeMillis # moins les 3 derniers digits
     *
     * @return
     */
    public static String getNewEddmId() {
        return generateIdStar() + "_0" + generateTsstar();
    }

    public static long generateIdStar() {
        long idStar = rand.nextInt(999999999);
        return Long.valueOf(String.format("1%011d", idStar));
    }

    /**
     * revert currentTimeMillis to date : System.out.println(new SimpleDateFormat("MMM dd,yyyy HH:mm").format(new
     * Date(System.currentTimeMillis()))); sous linux : date -d @currentTimeMillis # moins les 3 derniers digits
     */
    public static long generateTsstar() {
        return System.currentTimeMillis();
    }

    public static void setIdstarTstar(Document doc, String idStar_tsStar) throws CimutDocumentException {
        List<Long> ids = getIdsFromEddoc(idStar_tsStar);
        doc.setTsstar(ids.get(1));
        doc.setIdstar(ids.get(0));
    }

    public static void setNewIdstarTstar(Document doc) throws CimutDocumentException {
        doc.setIdstar(generateIdStar());
        doc.setTsstar(generateTsstar());
    }

    /**
     * Convertie IdStar/Tsstar en EDDOC
     *
     * @param idstar
     * @param tsstar
     * @return List<Long>
     * @throws CimutDocumentException
     */
    public static String getEddocIdFromIds(Long idstar, Long tsstar) throws CimutDocumentException {

        StringBuilder idstarStr = new StringBuilder(String.valueOf(idstar));
        StringBuilder tsstarStr = new StringBuilder(String.valueOf(tsstar));

        if (!idstarStr.toString().matches("^\\d{1,12}$")) {
            throw new CimutDocumentException("idstar invalid : " + idstarStr.toString());
        }
        if (!tsstarStr.toString().matches("^\\d{14}$")) {
            throw new CimutDocumentException("tsstar invalid : " + tsstarStr.toString());
        }
        while (idstarStr.length() < 12) {
            idstarStr.insert(0, "0");
        }
        return idstarStr.append("_").append(tsstarStr).toString();
    }

    public static String getExactEddocIdFromIds(Long idstar, Long tsstar) throws CimutDocumentException {
        String idStarStr = Long.toString(idstar, 10);
        String tsstarStr = Long.toString(tsstar, 10);

        String left = StringUtils.leftPad(idStarStr, 12, '0');
        String right = tsstarStr;

        String both = left + '_' + right;
        return StringUtils.rightPad(both, 30, ' ');
    }

    public static String getEddocId(Document document) {
        String idstarStr = String.valueOf(document.getIdstar());
        String tsstarStr = String.valueOf(document.getTsstar());

        while (idstarStr.length() < 12) {
            idstarStr = "0" + idstarStr;
        }
        while (tsstarStr.length() < 14) {
            tsstarStr = "0" + tsstarStr;
        }
        return idstarStr + "_" + tsstarStr;
    }

    /**
     * Convertie un EDDOC en IdStar/Tsstar (dans l'ordre)
     *
     * @param eddocId
     * @return List<Long>
     * @throws CimutDocumentException
     */
    public static List<Long> getIdsFromEddoc(String eddocId) throws CimutDocumentException {

        if (eddocId == null || !eddocId.matches("^\\d{12}_\\d{14}$")) {
            throw new CimutDocumentException("Eddoc invalid: " + eddocId);
        }

        java.util.StringTokenizer st = new StringTokenizer(eddocId, "_");
        String idstar = st.nextToken();
        String tsstar = st.nextToken();
        List<Long> ids = new ArrayList<Long>();
        ids.add(Long.valueOf(idstar));
        ids.add(Long.valueOf(tsstar));
        return ids;
    }

}
