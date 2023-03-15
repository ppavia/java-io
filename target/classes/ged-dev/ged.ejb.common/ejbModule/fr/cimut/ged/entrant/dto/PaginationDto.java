package fr.cimut.ged.entrant.dto;

import java.util.List;

import fr.cimut.ged.entrant.beans.db.Type;

public class PaginationDto {
    private int size;
    private long totalElements;
    private long totalPages;
    private int numPage;
    private List<Type> results;

    public PaginationDto(){}

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumPage() {
        return numPage;
    }

    public void setNumPage(int numPage) {
        this.numPage = numPage;
    }
    
    public List<Type> getResults() {
		return results;
	}

	public void setResults(List<Type> results) {
		this.results = results;
	}

    public PaginationDto(int size, long totalElements, long totalPages, int numPage) {
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.numPage = numPage;
    }

    public PaginationDto(long totalElements) {
        this.size = (int)totalElements;
        this.totalElements = totalElements;
        this.totalPages = 1;
        this.numPage = 1;
    }

    @Override public String toString() {
        return "PaginationDto" + "{" + "\n" + "size=" + size + "\n" + "totalPages=" + totalPages + "\n" + "totalElements="
                + totalElements + "\n" + "numPage=" + numPage + "\n" + "}";
    }
}
