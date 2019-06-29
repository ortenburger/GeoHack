package de.transformationsstadt.geoportal.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.LoggerFactory;

/**
 * Authentifizierender Filter für Shiro. 
 * 
 * Ermöglicht die Authentifizierung per JWT ( HTTP-Header "Authorization: Bearer <token>")
 * 
 * Liegt dieser Filter auf einer Resource, wird der Zugriff verweigert, wenn kein gültiges JWToken im Authorization-Header liegt.
 * 
 * Von dieser Regelung lassen sich die Resourcen ausnehmen, über die diese Token ausgestellt werden (definiert mittels HTTP-Methode und Pfad)
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class BearerAuthenticatingFilter extends AuthenticatingFilter{
	private String authPath = "/login";
	private String authMethod = "POST";
	
	/**
	 * Setzt den API-Pfad, über den die Token ausgegeben werden und der durch diesen Filter *nicht* gesichert werden soll.
	 * default: '/login'
	 * 
	 * @param path {@link String}  
	 */
	public void setAuthPath(String path) {
			this.authPath = path;

			if(!this.authPath.endsWith("/")) {
				this.authPath+="/";
			}
	}
	
	/**
	 * Setzt die Methode für den API-Pfad, über den die Token ausgegeben werden und der durch diesen Filter *nicht* gesichert werden soll.
	 * 
	 * default: 'POST'
	 * 
	 * @param path {@link String}  
	 */
	public void setAuthMethod(String str) {
		this.authMethod = str.toUpperCase();
	}
	
	/**
	 * Reproduziert das {@link BearerAuthenticationToken} aus dem Authorization-Header und gibt es zurück.
	 * 
	 * @return {@link BearerAuthenticationToken}
	 */
	@Override
	protected BearerAuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerAuthenticatingFilter").debug("CreateToken");
		String authHeader = WebUtils.toHttp(request).getHeader("Authorization");
		String bearer = "";
		if(authHeader == null || authHeader.isEmpty()) {
			authHeader = WebUtils.toHttp(request).getHeader("authorization");
		}
		if(authHeader == null || authHeader.isEmpty()) {
			return new BearerAuthenticationToken("");
		}
		
		if(authHeader.contains("Bearer ")) {
			bearer = authHeader.replaceFirst("Bearer ","");
			LoggerFactory.getLogger("bearerToken").debug("bearer " + bearer);
		}
		
		// bearer is empty or whatever comes after "Bearer " in the authorization-header now. validation is left to BearerAuthenticationTokens constructor.
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerAuthenticatingFilter").debug("returning token.");
		return new BearerAuthenticationToken(bearer,request);
	}
	
	
	/**
	 * Verweigert den Zugriff, falls er nicht auf den definierten Login-Pfad erfolgt oder eine 'OPTIONS'-Anfrage ist (für CORS).
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request_arg, ServletResponse response_arg) throws Exception {
		LoggerFactory.getLogger("BearerAuthenticatingFilter").debug("onAccessDenied");
		boolean loggedIn = false;
		LoggerFactory.getLogger("BearerAuthenticatingFilter").debug("onAccessDenied Method: "+WebUtils.toHttp(request_arg).getMethod());
		String currentPath = WebUtils.toHttp(request_arg).getPathInfo();
		String currentMethod = WebUtils.toHttp(request_arg).getMethod();
		if(currentMethod.equals("OPTIONS")) {
			return true;
		}
		if(currentMethod.equals("GET") && !currentPath.toLowerCase().startsWith("/accounts/")) {
			return true;
		}
			
		/**
		 * Configured login-path should be accessible
		 */

		if(currentPath.equals(authPath)&& (currentMethod.equals(authMethod))) {
			LoggerFactory.getLogger("BearerAuthenticatingFilter").debug("login attempt detected. Granting access.");
			return true;
		}
		
		if(!SecurityUtils.getSubject().isAuthenticated()) {
			loggedIn = executeLogin(request_arg, response_arg);
        }

		if(!loggedIn) {
    		HttpServletResponse response = (HttpServletResponse) response_arg;
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    	};
    	
		LoggerFactory.getLogger("bearerToken").debug("onAccessDenied");
		return loggedIn;
	}

}
