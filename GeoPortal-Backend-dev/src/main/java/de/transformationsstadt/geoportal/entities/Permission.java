package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Speichert einen String, der eine Zugriffsberechtigung im Kontext eines Benutzers definiert.
 * 
 * Siehe https://shiro.apache.org/permissions.html
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="permissions")
public class Permission implements Serializable{



	/**
	 * 
	 */
	private static final long serialVersionUID = -5406041469909419080L;


	@Id
	@GeneratedValue
	Long id;
	

	@Column(unique=true)
	private String name;
	
	@Column(unique=true)
	private String permission;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setPermission(String permission){
		this.permission = permission;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return this.id;
	}
	public String getPermission() {
		return this.permission;
	}
	public Permission() {};


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Permission) {
			return this.getId().equals( ( (Permission) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
