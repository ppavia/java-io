package fr.cimut.ged.entrant.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchFilteredDocsDto {
    private PaginationDto paginationResponse;

    private List<EddocDto> eddocs = new ArrayList<EddocDto>();

    public PaginationDto getPaginationResponse() {
        return paginationResponse;
    }

    public void setPaginationResponse(PaginationDto paginationResponse) {
        this.paginationResponse = paginationResponse;
    }

    public List<EddocDto> getEddocs() {
        return eddocs;
    }

    public void setEddocs(List<EddocDto> eddocs) {
        this.eddocs = eddocs;
    }
}