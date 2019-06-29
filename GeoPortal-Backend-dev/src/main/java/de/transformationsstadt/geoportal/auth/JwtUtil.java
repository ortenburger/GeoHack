package de.transformationsstadt.geoportal.auth;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Helfer-Klasse für JSON Web Token (JWT)
 * 
 * Diese (Singleton-)Klasse bietet einen zentralen Zugriff auf die für die Benutzung von JWT benötigten Parameter.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class JwtUtil{
	public final static String propertiesFile = "geoportal.properties";

	private SignatureAlgorithm sigAlg = SignatureAlgorithm.HS512;
	private String keyString;
	private String issuerPrefix;
	private byte[] keyBytes;
	private Key key;
	private static JwtUtil instance = null;

	private JwtUtil() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFile);
		Properties properties = new Properties();
		try{
			properties.load(is);
			keyString = (String)properties.getProperty("jwt.key");
			issuerPrefix = (String)properties.getProperty("jwt.issuerPrefix");
			keyBytes = DatatypeConverter.parseBase64Binary(keyString);
			key = new SecretKeySpec(keyBytes, sigAlg.getJcaName());
		}catch(IOException ioe) {
			
		}
		
		
	}
	public static JwtUtil getInstance() {
		if(instance == null) {
			instance = new JwtUtil();
		}
		return instance;
	}
	public SignatureAlgorithm getSignatureAlgorithm() {
		return sigAlg;
	}
	public String getIssuerPrefix() {
		return issuerPrefix;
	}

	public String getKeyString() {
		return keyString;
	}
	public byte[] getKeyBytes() {
		return keyBytes;
	}
	public Key getKey() {
		return key;
	}
}