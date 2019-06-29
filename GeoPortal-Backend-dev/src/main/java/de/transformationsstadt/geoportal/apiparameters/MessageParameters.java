package de.transformationsstadt.geoportal.apiparameters;

import de.transformationsstadt.geoportal.entities.Message;

/**
 * Objekt, in das {@link Message}-Objekte deserialisiert werden.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class MessageParameters {
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	private long receiverId;
	private long conversationId;
	private String body;
	private String subject;
	public boolean isValid() {
		boolean valid = true;
		valid = valid && (this.receiverId > 0);
		valid = valid && !body.isEmpty();
		valid = valid && !subject.isEmpty();
		return valid;
	}
	
	public Long getConversationId() {
		return conversationId;
	}
	public void setConversationId(Long conversationId) {
		
		this.conversationId = conversationId;
	}
}
