package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import de.transformationsstadt.geoportal.apiparameters.MessageParameters;

/**
 * Speichert eine Nachricht zwischen Nutzern für das Nachrichtenmodul des GeoPortals.
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */

@Entity
@Table(name="messages")
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	private String subject;
	@Column(columnDefinition = "text")
	private String body;
	
	
	private Long conversationId;
	/*
	@NotNull
	private User owner;
	
	@NotNull
	private User sender;
	
	@NotNull
	private User receiver;
	*/
	
	@NotNull
	private Long senderId;
	@NotNull 
	private Long receiverId;
	@NotNull
	private Long ownerId;
	
	@NotNull
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date sent;

	@Temporal(TemporalType.TIMESTAMP)
	private Date read;
	
	public Long getId() {
		if(this.id == null) {
			return new Long(-1);
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long l) {
		this.conversationId = l;
	}
/*
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
*/
	public Message() {
		
	}
	
	/**
	 * Erzeugt eine Nachricht aus den übergebenen Parametern.
	 * {@link MessageParameters}-Objekte werden aus der API von Clients angelegt.
	 * @param params {@link MessageParameters} 
	 */
	public Message(MessageParameters params) {
		
		this.setSubject(params.getSubject());
		this.setBody(params.getBody());
		this.setReceiverId(params.getReceiverId());
		this.setConversationId(params.getConversationId());
		this.setSent(new Date());
		if(this.conversationId == null) {
			this.setConversationId(this.getId());
		}
	}
	
	/**
	 * Gibt das Datum zurück, an dem die Nachricht angelegt wurde.
	 * @return
	 */
	public Date getSent() {
		return sent;
	}

	/**
	 * Setzt das Datum, an dem die Nachricht angelegt wurde.
	 * @return
	 */
	
	public void setSent(Date sent) {
		this.sent = sent;
	}

	
	/**
	 * Gibt zurück, ob die Nachricht schon gelesen wurde
	 * @return
	 */
	public Date getRead() {
		return read;
	}

	/**
	 * Setzt, ob die Nachricht schon gelesen wurde
	 * @return
	 */
	public void setRead(Date read) {
		this.read = read;
	}

	/**
	 * Gibt den Absender zurück (per ID des {@link User})
	 * @return
	 */
	public long getSenderId() {
		return senderId;
	}

	/**
	 * Setzt den Absender (per ID des {@link User})
	 * @return
	 */
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	/**
	 * Gibt den Empfänger zurück (per ID des {@link User})
	 * @return
	 */
	public long getReceiverId() {
		return receiverId;
	}
	
	/**
	 * Setzt den Empfänger (per ID des {@link User})
	 * @return
	 */
	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}

	/**
	 * Gibt den Besitzer der Nachricht zurück (per ID des {@link User})
	 * 
	 * Nachrichten werden jeweils für den Empfänger und Sender separat gespeichert.
	 * Eine Nachricht sollte dann im Posteingang angezeigt werden, wenn owner == receiver
	 * und im Postausgang, wenn owner == sender
	 * @return
	 */
	public long getOwnerId() {
		return ownerId;
	}

	/**
	 * Setzt den Besitzer der Nachricht (per ID des {@link User})
	 * 
	 * @return
	 */
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Message) {
			return this.getId().equals( ( (Message) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
