package fr.cimut.ged.entrant.dto;

public class Page {
    private int size;
    private long totalElements;
    private long totalPages;
    private int numPage;

    public Page(){}

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

    public Page(int size, long totalElements, long totalPages, int numPage) {
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.numPage = numPage;
    }

    public Page(long totalElements) {
        this.size = (int)totalElements;
        this.totalElements = totalElements;
        this.totalPages = 1;
        this.numPage = 1;
    }

    @Override public String toString() {
        return "Page" + "{" + "\n" + "size=" + size + "\n" + "totalPages=" + totalPages + "\n" + "totalElements="
                        + totalElements + "\n" + "numPage=" + numPage + "\n" + "}";
    }
}
