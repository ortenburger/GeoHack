package de.transformationsstadt.geoportal.auth;



import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Enthält Bentuzername, Salt und Passwort um eine Authentifizierung per Shiro zu ermöglichen (siehe {@link ShiroHibernateRealm} )
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class AuthInfo implements org.apache.shiro.authc.SaltedAuthenticationInfo {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String username;
	  private final String password;
	  private final String salt;

	  public AuthInfo(String username, String password, String salt) {
	    this.username = username;
	    this.password = password;
	    this.salt = salt;
	  }

	  @Override
	  public PrincipalCollection getPrincipals() {
	    PrincipalCollection coll = new SimplePrincipalCollection(username, username);
	    return coll;
	  }

	  @Override
	  public Object getCredentials() {
	    return password;
	  }

	  @Override
	  public ByteSource getCredentialsSalt() {
	    return  new SimpleByteSource(Base64.decode(salt)); 
	  }

	}