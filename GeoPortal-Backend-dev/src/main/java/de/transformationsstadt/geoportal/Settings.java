package de.transformationsstadt.geoportal;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import de.transformationsstadt.geoportal.auth.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;

public class Settings {


	public final static String propertiesFile = "geoportal.properties";
	private Boolean userRegistrationEnabled = null;
	private static Settings instance = null;

	private Settings() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFile);
		Properties properties = new Properties();
		try{
			properties.load(is);
			String ureString = (String)properties.getProperty("userRegistration.enabled");
			this.userRegistrationEnabled = (ureString.toLowerCase().equals("true"));
			if(this.userRegistrationEnabled) {
				System.out.println("User registration enabled.");
			}else {
				System.out.println("User registration disabled. config-item: ["+ureString+"]");
			}
		}catch(IOException ioe) {
			
		}
		
		
	}
	public static Settings getInstance() {
		if(instance == null) {
			instance = new Settings();
		}
		return instance;
	}
	public Boolean userRegistrationEnabled() {
		return this.userRegistrationEnabled;
	}
}
