package de.transformationsstadt.geoportal.auth;



import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.SpringContextProvider;
import de.transformationsstadt.geoportal.DAO.UserDAO;
import de.transformationsstadt.geoportal.entities.Permission;
import de.transformationsstadt.geoportal.entities.Role;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.services.UserService;

/**
 * Realm zur Authentifizierung per JWToken / Authorization-Bearer
 * 
 * 
 * 
 * @author Sebastian Bruch
 *
 */
@Repository
public class BearerRealm extends AuthorizingRealm{
	@Autowired
	UserService _userService;
	UserService userService(){
		if(this._userService == null) {
			System.out.println("Instantiating userService");
			this._userService = SpringContextProvider.getBean(UserService.class);
		}else {
			System.out.println("Already instantiated.");
		}
		return this._userService;
	}
	public BearerRealm() {
		setAuthenticationTokenClass(BearerAuthenticationToken.class);
		LoggerFactory.getLogger("BearerRealm.").debug("constructing");
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token != null && token instanceof BearerAuthenticationToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerRealm.").debug("doGetAuthenticationInfo");
		BearerAuthenticationToken bat = (BearerAuthenticationToken) token;
		if(bat.isExpired()) {
			LoggerFactory.getLogger("BearerRealm").debug("bearer expired");
			throw new AuthenticationException("token expired");
		}
		if(!bat.isValid()) {
			LoggerFactory.getLogger("BearerRealm").debug("bearer invalid");
			throw new AuthenticationException("Token not valid");
		}
		String username = (String) bat.getPrincipal();
		User user = userService().getUser(username);
		if(user == null) {
			throw new AuthenticationException("user not found.");
		}
		if(user.isLocked()) {
			throw new LockedAccountException("account locked.");
		}
		if(!user.isValidated()) {
			throw new DisabledAccountException("account not yet validated");
		}
		if(!user.isActive()) {
			throw new DisabledAccountException("account is disabled");
		}
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerRealm.").debug("got bearer for "+bat.getPrincipal()+" with " + bat.getCredentials());
		return new SimpleAuthenticationInfo((String)bat.getPrincipal(),(String) bat.getCredentials(),"bearerRealm");
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerRealm.").debug("doGetAuthorizationInfo");

		User user = userService().getUser((String)principals.getPrimaryPrincipal());

		LoggerFactory.getLogger("BearerRealm.").debug("authorizationinfo for "+principals.getPrimaryPrincipal());
		if(user == null) {
			return null;
		}

		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerRealm.").debug("User "+user.getUsername());

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		for(Role role: user.getRoles()) {

			LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.BearerRealm.").debug("   role: "+role.getName());

			info.addRole(role.getName());
			for(Permission permission: role.getPermissions()) {
				String perm = permission.getPermission();
				if(perm != null && !perm.isEmpty()) {
					info.addStringPermission(permission.getPermission());
				}else {
					LoggerFactory.getLogger("BearerRealm.").debug("empty permission in role " + role.getName());
				}
			}
		}
		return info;

	}
}
