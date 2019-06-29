package de.transformationsstadt.geoportal.apiparameters;

/**
 * Objekt, in das Nonces deserialisiert werden.
 * 
 * @author Sebastian Bruch
 * 
 * @deprecated Wird nicht mehr benutzt.
 *
 */
public class Nonce {
	private String nonce;

	public Nonce() {}
	public Nonce(String nonce) {
		this.nonce = nonce;
	}
	public void setNonce(String nonce) {this.nonce = nonce;}
	public String getNonce() { return this.nonce; }
}
