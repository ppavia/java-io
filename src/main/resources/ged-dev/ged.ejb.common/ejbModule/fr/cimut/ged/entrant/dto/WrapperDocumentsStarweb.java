package fr.cimut.ged.entrant.dto;

import fr.cimut.ged.entrant.beans.starwebdao.DocumentDto;

import java.util.ArrayList;
import java.util.List;

public class WrapperDocumentsStarweb {
    private Page page;
    private List<DocumentDto> documents = new ArrayList<DocumentDto>();
    
    public WrapperDocumentsStarweb() {}

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDto> documents) {
        this.documents = documents;
    }

    public WrapperDocumentsStarweb(Page page, List<DocumentDto> documents) {
        this.page = page;
        if (documents != null) {
            this.documents = documents;
        }
    }

    @Override public String toString() {
        return "WrapperDocumentsStarweb" + "{" + "\n" + "page=" + page + "\n" + "documents=" + documents.toString() + "\n"
                        + "}";
    }
}
