package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.transformationsstadt.geoportal.DAO.RoleDAO;
import de.transformationsstadt.geoportal.apiparameters.UserParameters;
import de.transformationsstadt.geoportal.services.RoleService;

/**
 * Benutzer des Geoportals.
 * 
 * Achtung: 
 * 
 * setPassword() erwartet ein Klartext-Passwort und erzeugt den salt und hash dann.
 * setPassword_hash() erwartet den fertigen hash. Der salt-Wert muss dann noch gesetzt werden.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="users")
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	@Transient
	RoleService roleService;
	
	@Id
	@GeneratedValue
	private Long id;

	public String getPassword_hash() {
		return password_hash;
	}


	public void setPassword_hash(String password_hash) {
		this.password_hash = password_hash;
	}


	public Date getAccount_created() {
		return account_created;
	}


	public void setAccount_created(Date account_created) {
		this.account_created = account_created;
	}


	public Date getLast_login() {
		return last_login;
	}


	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}


	public Long getLogin_count() {
		return login_count;
	}

	/**
	 * Login-Count. Uninteressant und verwässert seit JWT.
	 * @param login_count
	 */
	public void setLogin_count(Long login_count) {
		this.login_count = login_count;
	}

	/**
	 * Gibt die Version des durch den Benutzer akzeptierten Nutzungsbedingungen zurück.
	 * 
	 * @return String
	 */
	public String getEula_version() {
		return eula_version;
	}


	/**
	 * Setzt die Version des durch den Benutzer akzeptierten Nutzungsbedingungen.
	 * 
	 * @return String
	 */
	public void setEula_version(String eula_version) {
		this.eula_version = eula_version;
	}

	/**
	 * Gibt zurück, ob der Benutzer gerade geupdated wird.
	 * Das ist für die deserialisierung interessant, weil wir während eines Updates
	 * ein leeres Passwort akzeptieren können (dieses wird dann nicht gesetzt). 
	 * 
	 * @return
	 */
	public boolean isUpdating() {
		return updating;
	}


	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	/**
	 * Gibt zurück, ob das Benutzerkonto aktiv ist oder nicht.
	 * @return
	 */
	public Boolean getActive() {
		return active;
	}


	/**
	 * Gibt zurück, ob das Benutzerkonto validiert wurde oder nicht.
	 * @return
	 */
	public Boolean getValidated() {
		return validated;
	}

	
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="user_tags",
			joinColumns = { @JoinColumn(name = "user_id") }, 
			inverseJoinColumns = { @JoinColumn( name =" tag_id") }
			)
	@JsonManagedReference
	private Set<Tag> tags = new HashSet<Tag>();

	@Column(unique = true)
	private String username;

	private String firstname;
	private String lastname;

	@Column(unique = true)
	private String email;

	@Column(columnDefinition = "text")
	private String description;
	@ColumnDefault("true")

	private Boolean active=true;
	@ColumnDefault("false")

	private Boolean validated=false;

	@JsonIgnore
	private String password_hash;

	@JsonIgnore
	private String salt;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date account_created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date last_login;

	private Long login_count;

	private String eula_version;

	@ColumnDefault("false")
	@JsonIgnore
	private boolean locked=false;

	@Transient
	@JsonIgnore
	private boolean updating=false;

	@ManyToMany(fetch=FetchType.EAGER,cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE } )
	@JoinTable(
			name="user_roles",
			joinColumns = { @JoinColumn(name = "user_id") }, 
			inverseJoinColumns = { @JoinColumn( name =" role_id") }
			)
	@JsonManagedReference
	private Set<Role> roles = new HashSet<Role>();

	/**
	 * Setzt die Benutzerrollen, die dieser Benutzer hat.
	 * @return
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Gibt die Benutzerrollen zurück, die dieser Benutzer hat.
	 * @return
	 */
	@JsonManagedReference
	public Set<Role> getRoles(){
		return this.roles;
	}

	/**
	 * Gibt zurück, ob der Benutzer einen gegebene Benutzerrolle hat.
	 * @param role {@link Role}
	 * @return
	 */
	public boolean hasRole(Role role) {
		return this.roles.contains(role);
	}


	/**
	 * Fügt eine einzelne Rolle hinzu
	 * @param role {@link Role}
	 */
	public void addRole(Role role) {
		if(!this.roles.contains(role)) {
			this.roles.add(role);
		}
	}


	public User() {
	}

	public User(String email, String username, String password_hash) {
		this.setEmail(email);
		this.setUsername(username);
		this.setPasswordHash(password_hash);
	}

	public void updateUser(UserParameters user) {
		updateUser(user,false);
	}
	/**
	 * Aktualisiert den Benutzer. 
	 * 
	 * Das "creating"-Flag entscheidet, ob hier ein leeres Passwort akzeptiert wird.
	 * 
	 * 
	 * @param user {@link UserParameters}
	 * @param creating 
	 */
	public void updateUser(UserParameters user,boolean creating) {
		if(!creating) {
			updating = true;
		}
		setEmail(user.email);
		setDescription(user.description);
		setFirstname(user.firstname);
		setLastname(user.lastname);
		setEulaVersion(user.eulaVersion);
		setPassword(user.password);
		setUsername(user.username);
		updating = false;
	}

	/**
	 * Siehe {@link UserParameters} 
	 * @param user
	 */
	public User(UserParameters user) {
		updateUser(user,true);
	}

	/**
	 * Sperrt/Entsperrt das Benutzerkonto
	 * @param locked
	 */
	public void setLocked(boolean locked) { this.locked = locked; }
	
	/**
	 * Gibt zurück, ob das Bentzerkonto gesperrt ist.
	 * @return
	 */
	public boolean isLocked() { return this.locked; }

	/**
	 * Gibt zurück, ob das Benutzerkonto validiert wurde (z.B. per Mail)
	 * @return
	 */
	public Boolean isValidated() {
		if(validated != null) {
			return validated;
		}else {
			return false;
		}
	}
	
	/**
	 * Validiert/Invalidiert das Benutzerkonto.
	 */
	public void setValidated(Boolean validated) {
		this.validated = validated;
	}
	public Long getId() {return this.id;}

	/**
	 * Gibt den hinterlegten Vornamen zurück
	 * @return
	 */
	public String getFirstname() {return this.firstname;}
	/**
	 * Setzt den Vornamen
	 * @param firstname
	 */
	public void setFirstname(String firstname) {
		if(updating) {
			if(firstname == null|| firstname.isEmpty()) {
				return;
			}
		}
		this.firstname = firstname;
	}
	/**
	 * Gibt den hinterlegten Nachnamen zurück
	 * @return
	 */
	public String getLastname() {return this.lastname;}
	/**
	 * Setzt den Nachnamen
	 * @param lastname
	 */
	public void setLastname(String lastname) {this.lastname = lastname;}

	
	/**
	 * Setzt das Passwort. Erwartet das Passwort im Klartext.
	 * 
	 * Ist das übergebene Passwort leer und der Benutzer befindet sich nicht in einer aktualisierung (z.B. Änderung des Benutzerprofils),
	 * wird kein Passwort gesetzt.
	 * Ansonsten wird der angegebene String gehasht und der Hash-Wert sowie der Salt-Wert gespeichert.
	 * 
	 * 
	 * @param plaintextPassword
	 */
	public void setPassword(String plaintextPassword) {
		if(updating) {
			if(plaintextPassword == null|| plaintextPassword.isEmpty()) {
				return;
			}
		}
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		Object salt = rng.nextBytes();
		// Now hash the plain-text password with the random salt and multiple
		// iterations and then Base64-encode the value (requires less space than Hex):
		String hashedPasswordBase64 = new Sha512Hash(plaintextPassword, salt,1024).toBase64();
		setPasswordHash(hashedPasswordBase64);
		setSalt(salt.toString());
	}

	/**
	 * Gibt den Benutzernamen zurück.
	 * @return
	 */
	public String getUsername() { return this.username; }
	/**
	 * Setzt den Benutzernamen
	 * @param username
	 */
	public void setUsername(String username) {
		if(updating) {
			if(username == null|| username.isEmpty()) {
				return;
			}
		}
		this.username = username; 
	}
	
	/*
	 * Gibt die hinterlegte Email-Adresse zurück
	 */
	public String getEmail() {return this.email;}
	
	/**
	 * Setzt die Email-Adresse.
	 * TODO: Sanitizing.
	 * @param email
	 */
	public void setEmail(String email) {
		if(updating) {
			if(email == null|| email.isEmpty()) {
				return;
			}
		}
		this.email = email;
	}

	/**
	 * Ruft die Beschreibung des Benutzers ab.
	 * @return
	 */
	public String getDescription() {return this.description;}
	
	/**
	 * Setzt die Beschreibung des Benutzers.
	 * @param description
	 */
	public void setDescription(String description) {
		if(updating) {
			if(description == null|| description.isEmpty()) {
				return;
			}
		}
		this.description = description;

	}

	/**
	 * Gibt zurück, ob das Benutzerkonto aktiv ist.
	 * @return
	 */
	public Boolean isActive(){ if(this.active != null) {return this.active;}else{return true;}}
	
	/**
	 * Setzt das Benutzerkonto (in)aktiv
	 * @return
	 */
	public void setActive(Boolean active) { this.active = active; }

	/**
	 * Gibt den Passwort-Hash zurück.
	 * @return
	 */
	@JsonIgnore
	public String getPasswordHash() { return this.password_hash;}
	/**
	 * Setzt den Passwort-Hash
	 * @param password_hash
	 */
	public void setPasswordHash(String password_hash) { this.password_hash = password_hash; }

	/**
	 * Gibt den Salt-Wert für den Passwort-Hash zurück.
	 * @return
	 */
	@JsonIgnore
	public String getSalt() { return this.salt; }
	
	/**
	 * Setzt den Salt-Wert für den Passwort-Hash
	 * @param salt
	 */
	public void setSalt(String salt) { this.salt = salt; }

	/**
	 * Gibt das Datum zurück, an dem das Benutzerkonto erstellt wurde.
	 * @return
	 */
	@JsonFormat
	(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy hh:mm:ss")
	public Date getAccountCreated() { return this.account_created; }
	/**
	 * Setzt das Datum, an dem das Benutzerkonto erstellt wurde.
	 * @param account_created
	 */
	public void setAccountCreated(Date account_created) { this.account_created = account_created; }

	@JsonFormat
	(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy hh:mm:ss")	public Date getLastLogin() { return this.last_login; }
	public void setLastLogin(Date last_login) { this.last_login = last_login; }

	public Long getLoginCount() { return this.login_count; }
	public void setLoginCount(Long login_count) { this.login_count = login_count; }
	public String getEulaVersion() { return this.eula_version; }
	public void setEulaVersion(String eula_version) { this.eula_version = eula_version; }

	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}



	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User) {
			return this.getId().equals( ( (User) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
