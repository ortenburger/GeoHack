package de.transformationsstadt.geoportal.auth;
import javax.servlet.ServletRequest;

import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;


/**
 * Rekonstruiert und validiert ein JWToken aus einem String und hält die darin befindlichen, für das GeoPortal relevanten Daten vor.
 * 
 * @author Sebastian Bruch < sebastian.bruch@utopiastadt.eu >
 *
 */
public class BearerAuthenticationToken implements AuthenticationToken{

	private static final long serialVersionUID = 1L;
	private String username = ""; 
	private String token = "";
	private String issuer=""; 
	private boolean valid = false; 
	private boolean expired = false;
	private String remoteaddr= ""; 
	private String useragent = "";

	public BearerAuthenticationToken(String token, ServletRequest request) {
		this.setup(token);
	}

	/**
	 * Rekonstruiert das Token aus dem String, speichert die Daten in den entsprechenden Properties ab.
	 * @param token {@link String)
	 */
	private void setup(String token) {

		LoggerFactory.getLogger("BearerAuthenticationToken").debug("Constructor");
		this.token = token;
		if(token == null|| token.isEmpty()) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("bearer empty");
			valid = false;
			return;
		}
		JwtUtil jwtUtil = JwtUtil.getInstance();
		JwtParser parser =  Jwts.parser();
		try {
			Jws<Claims> jws = parser.setSigningKey(jwtUtil.getKey()).parseClaimsJws(this.token);
			this.username = jws.getBody().getSubject();
			this.issuer = jws.getBody().getIssuer();
			this.remoteaddr = (String) jws.getBody().get("remote-adress");
			this.useragent = (String) jws.getBody().get("user-agent");
			this.valid = true;
		}catch(ExpiredJwtException eje) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("Expired token");
			expired = true;
			valid = false;
		}catch(SignatureException se) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("signature invalid");
			expired = false;
			valid = false;
		}catch(MalformedJwtException mje) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("malformed token");
			valid = false;
		}catch(io.jsonwebtoken.CompressionException ce) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("compression invalid");
			valid = false;
		}catch(JwtException e) {
			LoggerFactory.getLogger("BearerAuthenticationToken").debug("invalidated token");
			valid = false;
		}

	}
	public BearerAuthenticationToken(String token) {
		this.setup(token);
	}

	public boolean isExpired() {
		return expired;
	}
	public boolean isValid() {
		return valid;
	}
	@Override
	public Object getPrincipal() {
		return this.username;
	}


	@Override
	public Object getCredentials() {
		return this.token;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getRemoteaddr() {
		return remoteaddr;
	}
	public void setRemoteaddr(String remoteaddr) {
		this.remoteaddr = remoteaddr;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

}
