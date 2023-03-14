package fr.cimut.ged.entrant.beans.starwebdao;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.utils.GedeIdHelper;

/**
 * la classe d'échange sur les infos de documents
 * 
 * @author jlebourgocq
 *
 */
public class DocumentDto implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDto.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6952442266897423338L;

	String id;

	/**
	 * Id de la forme 132456_987654
	 */
	String eddocIdWithoutZero;

	/**
	 * info complémentaire sur le document
	 */
	@JsonUnwrapped
	DocumentInfoComp infoComp;

	public DocumentDto() {
		super();
	}

	public DocumentDto(String id, String rangRattachement) {
		super();
		this.id = id;
	}

	public String getEddocIdWithoutZero() {
		if (null == eddocIdWithoutZero) {
			try {
				eddocIdWithoutZero = GedeIdHelper.normalizeEddocId(id);
			} catch (CimutDocumentException e) {
				LOGGER.error(String.format("Erreur dans le formattage de l'id %s", id), e);
			}
		}
		return eddocIdWithoutZero;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DocumentInfoComp getInfoComp() {
		return infoComp;
	}

	public void setInfoComp(DocumentInfoComp infoComp) {
		this.infoComp = infoComp;
	}

}
