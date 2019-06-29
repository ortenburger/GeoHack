package de.transformationsstadt.geoportal.auth;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import de.transformationsstadt.geoportal.SpringContextProvider;
import de.transformationsstadt.geoportal.entities.Permission;
import de.transformationsstadt.geoportal.entities.Role;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.services.UserService;

/**
 * Hibernate-Realm für Shiro.
 * 
 * Ermöglicht das Authentifizieren von Benutzern aus der Geoportal Datenbank (salted hashes, siehe {@link User}, {@link Role} und {@link Permission})
 * 
 * Dieser Realm wird benötigt um eine Authentifizierung mit Benutzername/Passwort zu ermöglichen um anschließen JWT auszugeben, welche dann über den {@link BearerRealm}
 * verarbeitet werden.
 * 
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */

@Repository
public class ShiroHibernateRealm extends AuthorizingRealm{
	@Autowired
	UserService _userService;
	
	private UserService userService() {
		if(this._userService == null) {
			this._userService = SpringContextProvider.getBean(UserService.class);
		}
		return this._userService;		
	}
	public ShiroHibernateRealm() {
		
		setName("de.transformationsstadt.geoportal.auth.ShiroHibernateRealm");
	}



	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.ShiroHibernateRealm").debug("doGetAuthenticationInfo.");
		UsernamePasswordToken upt = (UsernamePasswordToken) token;
		String username = upt.getUsername();
		if(userService() == null) {
			System.out.println("Userservice null");
		}
		User user = userService().getUser(username);
		if(user == null) {
			throw new UnknownAccountException("unknown account.");
		}
		
		if(user.isLocked()) {
			throw new LockedAccountException("locked.");
		}
		if(!user.isValidated()) {
			throw new DisabledAccountException("not yet validated.");
		}
		//String userString = user.getUsername() + " / " + user.getPasswordHash() + " / "+ user.getSalt();
		//if(user != null) {throw new AuthenticationException(userString);}
		return new AuthInfo(user.getUsername(),user.getPasswordHash(),user.getSalt());
	}
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.ShiroHibernateRealm").debug("doGetAuthorizationInfo.");
		User user = userService().getUser((String)principals.getPrimaryPrincipal());
		if(user == null) {
			return null;
		}
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.ShiroHibernateRealm").debug("user "+user.getUsername());
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		for(Role role: user.getRoles()) {
			info.addRole(role.getName());
			LoggerFactory.getLogger("de.transformationsstadt.geoportal.auth.ShiroHibernateRealm").debug("   role: "+role.getName());
			for(Permission permission: role.getPermissions()) {
				String perm = permission.getPermission();
				if(perm != null && !perm.isEmpty()) {
				info.addStringPermission(permission.getPermission());
				}else {
					LoggerFactory.getLogger("ShiroHibernateRealm.").debug("empty permission in role " + role.getName());
				}
			}
		}
		return info;
	}
}