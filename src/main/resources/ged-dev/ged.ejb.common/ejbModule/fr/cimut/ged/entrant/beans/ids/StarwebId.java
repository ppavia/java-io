package fr.cimut.ged.entrant.beans.ids;

public class StarwebId {
    private String p_dieddm_id;
    private long dieddm_ts_dem;
    private long dieddm_seq;

    public String getP_dieddm_id() {
        return p_dieddm_id;
    }

    public void setP_dieddm_id(String p_dieddm_id) {
        this.p_dieddm_id = p_dieddm_id;
    }

    public long getDieddm_ts_dem() {
        return dieddm_ts_dem;
    }

    public void setDieddm_ts_dem(long dieddm_ts_dem) {
        this.dieddm_ts_dem = dieddm_ts_dem;
    }

    public long getDieddm_seq() {
        return dieddm_seq;
    }

    public void setDieddm_seq(long dieddm_seq) {
        this.dieddm_seq = dieddm_seq;
    }

    @Override public String toString() {
        return "StarwebId{" + "p_dieddm_id='" + p_dieddm_id + '\'' + ", dieddm_ts_dem=" + dieddm_ts_dem
                + ", dieddm_seq=" + dieddm_seq + '}';
    }
}
