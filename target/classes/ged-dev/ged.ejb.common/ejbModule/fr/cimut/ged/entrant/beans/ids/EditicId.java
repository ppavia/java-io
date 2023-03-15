package fr.cimut.ged.entrant.beans.ids;

public class EditicId {

    private String lbnmfd;
    private long idStar;
    private long tsStar;

    public String getLbnmfd() {
        return lbnmfd;
    }

    public void setLbnmfd(String lbnmfd) {
        this.lbnmfd = lbnmfd;
    }

    public long getIdStar() {
        return idStar;
    }

    public void setIdStar(long idStar) {
        this.idStar = idStar;
    }

    public long getTsStar() {
        return tsStar;
    }

    public void setTsStar(long tsStar) {
        this.tsStar = tsStar;
    }

    @Override public String toString() {
        return "EditicId{" + "lbnmfd='" + lbnmfd + '\'' + ", idStar=" + idStar + ", tsStar=" + tsStar + '}';
    }
}
