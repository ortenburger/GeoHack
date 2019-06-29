package de.transformationsstadt.geoportal.api;


//import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import javax.crypto.spec.SecretKeySpec;
import javax.persistence.PersistenceException;
//import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.DefaultValue;
//import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
//import javax.xml.bind.DatatypeConverter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import org.apache.shiro.authz.annotation.RequiresRoles;
//import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.glassfish.jersey.servlet.WebConfig;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.transformationsstadt.geoportal.Settings;
import de.transformationsstadt.geoportal.DAO.RoleDAO;

import de.transformationsstadt.geoportal.apiparameters.Credentials;
import de.transformationsstadt.geoportal.apiparameters.UserParameters;
import de.transformationsstadt.geoportal.auth.JwtUtil;
import de.transformationsstadt.geoportal.entities.Role;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.services.RoleService;
import de.transformationsstadt.geoportal.services.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

/**
 * 
 * API-Teil, der den Unterpfad "/Accounts/" (/geoportal/Accounts/) abbildet.
 * 
 * Hierüber sollen alle die Benutzer- Rechte- und Gruppenverwaltung betreffenden operationen abgewickelt werden.
 * 
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */
@Path("/accounts/")
public class Accounts {
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
	
	Subject subject;
	Logger logger;

	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	public Accounts() {
		this.logger = LoggerFactory.getLogger("geoportal.accounts");
		this.subject = SecurityUtils.getSubject();
	}

	/**
	 * Debug-Funktion. Gibt eine leere Antwort auf eine Anfrage.
	 * 
	 * @return null
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ping/")
	public Response ping() {
		return Response.status(Status.OK).entity(null).build();
	}
	
	/**
	 * Gibt den aktuellen Benuter (Typ {@link User}) zurück 
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return this.getCurrentUser();
	}

	
	/**
	 * Updates am Benutzerprofil.
	 * 
	 * 
	 * @param userId ID des zu verändernden Benutzer
	 * @param userArg Änderungen am Benutzer (siehe {@link UserParameters})
	 * 
	 * 
	 * @return {@link User}
	 */
	@PATCH
	@Path("/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserById(@PathParam("id") Long userId,UserParameters userArg) {
		Status status = Status.BAD_REQUEST;
		HashMap<String,String> map = new HashMap<String,String>();

		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}

		LoggerFactory.getLogger("de.transformationsstadt.geoportal.api.accounts").warn(userArg.getEmail());
		String currentUserIdentifier = (String)SecurityUtils.getSubject().getPrincipal();
		map.put("user",currentUserIdentifier);
		User currentUser;
		currentUser = userService.getUser(currentUserIdentifier);
		// user may see his own details.
		if(currentUser.getId() == userId) {
			try {
				userService.updateUser(currentUser,userArg);
			}catch(PersistenceException pe) {
				return Response.status(Status.CONFLICT).entity(currentUser).build();
			}

			status = Status.OK;
			return Response.status(status).entity(currentUser).build();
		}else {
			// admin may any users details.
			if(subject.hasRole("admin")) {
				User requestedUser = userService.get(userId);
				try {
					userService.updateUser(requestedUser,userArg);
				}catch(PersistenceException pe) {
					return Response.status(Status.CONFLICT).entity(requestedUser).build();
				}
				return Response.status(status).entity(requestedUser).build();
			}else {
				status = Status.UNAUTHORIZED;
				map.put("description","you are not authorized to see this users details.");
				return Response.status(status).entity(map).build();
			}
		}

	}

	/**
	 * Gibt den Benutzer zurück, der mittels 'id' adressiert wurde.
	 * @param userIdString
	 * @return {@link User}
	 */
	@GET
	@Path("/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") Long userId) {
		Status status = Status.BAD_REQUEST;
		HashMap<String,String> map = new HashMap<String,String>();

		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}


		String currentUserIdentifier = (String)subject.getPrincipal();
		map.put("user",currentUserIdentifier);
		User currentUser = userService.getUser(currentUserIdentifier);
		// user may see his own details.
		if(currentUser.getId() == userId) {
			status = Status.OK;
			return Response.status(status).entity(currentUser).build();
		}else {
			// admin may any users details.
			if(subject.hasRole("admin")) {
				User requestedUser = userService.get(userId);
				return Response.status(status).entity(requestedUser).build();
			}else {
				status = Status.UNAUTHORIZED;
				map.put("description","you are not authorized to see this users details.");
				return Response.status(status).entity(map).build();
			}
		}

	}

	
	/**
	 * Legt einen neuen Benutzer an.
	 * 
	 * Der neue Benutzer wird der Rolle "user" zugeordnet. 
	 * 
	 * TODO: EMail-Verifikation
	 * @param user {@link UserParameters}
	 * @return {@link User}
	 */
	@POST
	@Path("/register/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(UserParameters user) {
		
		Status status = Status.OK;

		Map<String,String> map = new HashMap<String,String>();
		
		/**
		 * Registrierung abgeschaltet.
		 */
		
		if(!Settings.getInstance().userRegistrationEnabled()){
			map.put("error","user registration has been disabled");
			return Response.status(Status.SERVICE_UNAVAILABLE).entity(map).build();
		}
		if(user == null) {
			status = Status.BAD_REQUEST;
			map.put("error", "no data, userdata expected");
		}
		else if(user != null && user.isValid()) {
			User newUser = new User(user);
			newUser.setValidated(true);
			// todo: validation-mail
			try {
				Role userRole = roleService.getRole("user");
				newUser.setAccountCreated(new Date());
				newUser.addRole(userRole);
				userService.create(newUser);
				status= Status.OK;

				return Response.status(status).entity(newUser).build();
			}catch (PersistenceException pe) {
				Throwable t = pe.getCause();
				while ((t != null) && !(t instanceof ConstraintViolationException)) {
					t = t.getCause();
				}
				if (t instanceof ConstraintViolationException) {
					map.put("error","username or email already in use.");
					status = Status.CONFLICT;
				}
			}
		}else{
			status = Status.NOT_ACCEPTABLE;
			int i = 0;
			for(String error: user.getErrors()){
				map.put("error"+i,error);
				i++;
			}
		}
		LoggerFactory.getLogger("de.transformationsstadt.geoportal.api.accounts").debug("Register called.");
		return Response.status(status).entity(map).build();
	}

	/**
	 * Logout.
	 * 
	 * Noch ein bisschen sinnlos, solange wir JWT verwenden.
	 * 
	 * TODO: JWT invalidieren?
	 * 
	 * @return null
	 */
	@PUT
	@Path("/logout/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
		HashMap<String,String> map = new HashMap<String,String>();
		if(subject.isAuthenticated()) {
			subject.logout();
			map.put("ok", "logged out");
			return Response.status(Status.OK).entity(map).build();
		}else {
			map.put("error", "not logged in");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}

	}

	/**
	 * Gibt den aktuellen Benuter (Typ {@link User}) zurück 
	 * @return
	 */
	@GET
	@Path("/currentUser/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentUser() {
		Map<String,String> map = new HashMap<String,String>();
		System.out.println("Getting current user.");
		if(!subject.isAuthenticated()) {
			map.put("error", "not logged in");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}
		
		String userIdentifier = (String)subject.getPrincipal();
		System.out.println("UserId: "+userIdentifier);
		User user = userService.getUser(userIdentifier);
		System.out.println("User: "+user.getUsername());
		return Response.status(Status.OK).entity(user).build();

	}

	/**
	 * Authentifiziert den mittels der übergeben {@link Credentials} definierten Benutzer
	 * 
	 * Gibt ein JWT-Token zurück, welches von dann an für 1440 Minuten (24 Stunden) gültig ist und auf den entsprechenden Pfaden mittels 
	 * Header "Authorization: Bearer <token>" die Zugriffe autorisieren soll.
	 * 
	 * TODO: Die entsprechenden Abfragen fehlen noch an einigen Pfaden.
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param credentials {@link Credentials}
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/authenticate/")
	public Response login(@Context HttpServletRequest request,Credentials credentials) {
		System.out.println("Loggin-Attempt.");
		Status status = Status.NOT_ACCEPTABLE;

		HashMap<String, String> map = new HashMap<String,String>();

		LoggerFactory.getLogger("de.transformationsstadt.geoportal.Accounts").debug("login attempt. Credentials: "+credentials.getUsername() + "/"+credentials.getPassword());
		try {
			LoggerFactory.getLogger("de.transformationsstadt.geoportal.Accounts").debug("login attempt. Credentials: "+credentials.getUsername() + "/"+credentials.getPassword());
			//UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(),credentials.getPassword());
			this.subject.login(new UsernamePasswordToken(
					credentials.getUsername(),
					credentials.getPassword())
					);

			/**
			 * Generate JSON Web Token
			 */


			LoggerFactory.getLogger("de.transformationsstadt.geoportal.Accounts").debug("Remote-Host: "+request.getRemoteAddr());
			JwtUtil jwtUtil = JwtUtil.getInstance();
			JwtBuilder builder = Jwts.builder()
					.setSubject(credentials.getUsername())
					.setIssuer(jwtUtil.getIssuerPrefix()+"::"+uriInfo.getAbsolutePath().toString())
					.setIssuedAt(new Date())
					.setExpiration(Date.from((LocalDateTime.now().plusMinutes(1440L)).atZone(ZoneId.systemDefault()).toInstant()))
					.claim("remote-address", request.getRemoteAddr())
					.claim("user-agent", request.getHeader("user-agent"))
					.signWith(jwtUtil.getSignatureAlgorithm(),jwtUtil.getKey());
			String bearer = builder.compact();

			//BearerAuthenticationToken bat = new BearerAuthenticationToken(bearer);
			map.put("Bearer", bearer);


			/**
			 * Session 
			 */
			//Session session = this.subject.getSession(false);


			/**
			 * Return
			 */
			this.logger.debug(subject.isAuthenticated()?"authenticated.":"not authenticated.");
			status = Status.OK;
			map.put("success", "authenticated.");
			map.put("username",credentials.getUsername());
			User user = userService.getCurrentUser();
			map.put("id",user.getId().toString());
			
			
		}catch(LockedAccountException lae) {
			status = Status.FORBIDDEN;
			map.put("error", "account locked");
		}catch(DisabledAccountException dae) {
			status = Status.FORBIDDEN;
			map.put("error", "account not validated yet");
		}catch(AuthenticationException ae) {
			status = Status.UNAUTHORIZED;
			map.put("error", "authentication failed.");
		}
		return Response.status(status).entity(map).build();
	}

}