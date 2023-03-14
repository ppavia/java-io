package fr.cimut.ged.entrant.beans.p360;

/**
 * Couple contenant un CMROC et la valeur d'un champ dont le type est defini par la classe
 * 
 * @author pgarel
 */
public class CoupleCmrocValeur<T> {

	/** Valeur du CMROC */
	private String cmroc;

	/** regime associé au CMROC */
	private Regime regime;

	/** Valeur du champ */
	private T valeur;

	public CoupleCmrocValeur() {
		super();
	}

	public CoupleCmrocValeur(String cmroc, T valeur) {
		this.cmroc = cmroc;
		this.regime = Regime.getRegime(cmroc);
		this.valeur = valeur;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public T getValeur() {
		return valeur;
	}

	public void setValeur(T valeur) {
		this.valeur = valeur;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CoupleCmrocValeur)) {
			return false;
		}
		return getCmroc().equals(((CoupleCmrocValeur<?>) obj).getCmroc());
	}

	@Override
	public int hashCode() {
		return getCmroc().hashCode();
	}
}
