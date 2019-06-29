package de.transformationsstadt.geoportal.entities;


/**
 * Speichert einen String, der eine Rolle im Kontext eines Benutzers definiert.
 * 
 * Dient im wesentlichen dem aggregieren verschiedener Zugriffsrechte ( Permission )
 * 
 * Siehe https://shiro.apache.org/permissions.html
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name="roles")
public class Role implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@ManyToMany()
	@JoinTable(
			name="user_roles",
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn( name =" user_id") }
			)
	@JsonIgnore
	private Set<User> users = new HashSet<User>();
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="role_permissions",
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn( name =" permission_id") }
			)
	@JsonManagedReference(value="permissions")
	private Set<Permission> permissions = new HashSet<Permission>();
	
	@JsonIgnore
	public Set<Permission> getPermissions() {
		return this.permissions;
	}
	
	public void addPermission(Permission permission) {
		if(!this.permissions.contains(permission)) {
			this.permissions.add(permission);
		}
	}
	
	public void removePermission(Permission permission) {
		this.permissions.remove(permission);

	}
	public boolean hasPermission(Permission permission) {
		return this.permissions.contains(permission);
	}
	
	public Long getId() {return id;}
	public void setId(Long id) { this.id=id;}
	
	@JsonIgnore
	public Set<User> getUser() { return this.users; }
	public void removeUser(User user) {
		if(users.contains(user)) {
			users.remove(user);
		}
	}
	public void addUser(User user) {
		if(!users.contains(user)) {
			users.add(user);
			}
	}
	
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public Role() {}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Role) {
			return this.getId().equals( ( (Role) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
