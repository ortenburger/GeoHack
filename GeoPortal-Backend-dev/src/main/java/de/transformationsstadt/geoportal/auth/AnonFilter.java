package de.transformationsstadt.geoportal.auth;

/**
 * Filter, der einen nicht-authentifizierten (bzw. anonymen) Zugriff ermöglicht. 
 * Wenn dieser Filter auf einer Resource liegt, wird der Zugriff unter Nichtbeachtung aller Rahmenbedingungen gestattet (siehe shiro / shiro.ini)
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 */

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

// extracts bearer-token from authorization-header and returns a BearerAuthenticationToken
public class AnonFilter extends AuthenticatingFilter{

	@Override
	protected boolean onAccessDenied(ServletRequest request_arg, ServletResponse response_arg) throws Exception {
		return true;
	}

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
