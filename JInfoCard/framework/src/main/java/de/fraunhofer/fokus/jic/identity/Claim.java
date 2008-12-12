package de.fraunhofer.fokus.jic.identity;

public interface Claim {
	public String getType();
	public String getValue();
	public ClaimIdentity getSubject();
	public ClaimIdentity getIsuuer();
}
