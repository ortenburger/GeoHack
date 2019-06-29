package de.transformationsstadt.geoportal.api;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.glassfish.jersey.servlet.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.transformationsstadt.geoportal.DAO.MessageDAO;
import de.transformationsstadt.geoportal.apiparameters.MessageParameters;
import de.transformationsstadt.geoportal.apiparameters.PaginationInfo;
import de.transformationsstadt.geoportal.entities.Message;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.services.MessageService;
import de.transformationsstadt.geoportal.services.UserService;

/**
 * 
 * API-Teil, der den Unterpfad "/Messages/" (/geoportal/Messages/) abbildet.
 * 
 * Die Operanden sind Objekte des Typs {@link Message}.
 * 
 * Diese sollen den Benutzern des Geoportals die Möglichkeit geben Nachrichten untereinander auszutauschen bzw. den Betreibern die Möglichkeiten geben, Nachrichten
 * an die Benutzer zu versenden.
 * 
 * TODO: Noch nicht fertig.
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */

@Path("/Messages/")
public class Messages {

	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	Subject subject;
	Logger logger;

	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	public Messages() {
		this.logger = LoggerFactory.getLogger("geoportal.messages");
		this.subject = SecurityUtils.getSubject();
	}


	/**
	 * Setzt eine Nachricht ab.
	 * 
	 * Die Nachricht wird dabei als ungelesen markiert und doppelt in der Datenbank abgelegt (einmal mit dem Empfänger und einmal mit dem Sender als Besitzer).
	 * 
	 * 
	 * @param msg {@link MessageParameters}
	 * @return 
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMessage(MessageParameters msg) throws Exception {
		HashMap<String,String> map = new HashMap<String,String>();

		try {
			msg.getReceiverId();
		}catch(Exception e) {
			map.put("error", "message parameters incomplete or invalid.");
			return Response.status(Status.BAD_REQUEST).entity(map).build();
		}
		
		if(!msg.isValid()) {
			map.put("error", "message parameters incomplete or invalid.");
			return Response.status(Status.BAD_REQUEST).entity(map).build();
		}
		
		if(!subject.isAuthenticated()) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		
		String currentUserName=(String)subject.getPrincipal();
		User sender = userService.getUser(currentUserName);
		User receiver = userService.get(msg.getReceiverId());
		/**
		 * check if receiver exists.
		 */
		if(receiver == null) {
			map.clear();
			map.put("error","unknown receiver");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		/**
		 *	check if conversation-id includes current user 
		 */
		if(msg.getConversationId() != null) {
			Message conversationOrigin = messageService.get(msg.getConversationId());
			
			if(conversationOrigin != null && conversationOrigin.getSenderId() != sender.getId() && conversationOrigin.getReceiverId() != sender.getId()) {
				map.clear();
				map.put("error","the conversation-id you are referring to points to a conversation not including you. go away.");;
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}
		}
		
		/**
		 * create message.
		 */
		Message senderMessage = new Message(msg);
		senderMessage.setOwnerId(sender.getId());
		senderMessage.setSenderId(sender.getId());
		Message receiverMessage = new Message(msg);
		
		Long senderMessageId = messageService.create(senderMessage);
		Long conversationId = msg.getConversationId();
		if(conversationId == 0) {
			conversationId = senderMessageId;
			senderMessage.setConversationId(conversationId);
			messageService.update(senderMessage);
		}
		senderMessage.setId(senderMessageId);
		
		map.put("msgId",Long.toString(senderMessageId));
		receiverMessage.setOwnerId(receiver.getId());
		receiverMessage.setSenderId(sender.getId());
		receiverMessage.setConversationId(conversationId);
		messageService.create(receiverMessage);
		
		map.put("success","message delivered.");
		
		return Response.status(Status.OK).entity(map).build();
	}

	
	/**
	 * Ruft die Nachricht mit der entsprechenden ID ab.
	 * 
	 * Der Zugriff wird verweigert, wenn der Benutzerkontext nicht dem Besitzer der Nachricht entspricht. 
	 * 
	 * @param messageId {@link Long} id der Nachricht
	 * @return {@link Message}
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id:[0-9]+}/")
	public Response getMessage(@PathParam("id") Long messageId) {
		HashMap<String,String> map = new HashMap<String,String>();
		if(!subject.isAuthenticated()) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		User currentUser = userService.getCurrentUser();
		if(currentUser == null) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		
		Message msg = messageService.get(messageId);
		
		if(msg == null) {
			map.put("error", "no such message");			
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		if(msg.getOwnerId() != currentUser.getId()) {
			// in diesem kontext nicht falsch.
			map.put("error", "no such message");			
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		return Response.status(Status.OK).entity(msg).build();
	}

	/**
	 * Eine Nachricht verändern.
	 *
	 * @deprecated
	 * 
	 * @param messageId
	 * @param params
	 * @return
	 */
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id:[0-9]+}/")
	public Response updateMessage(@PathParam("id") Long messageId,MessageParameters params) {
		HashMap<String,String> map = new HashMap<String,String>();
		if(!subject.isAuthenticated()) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		User currentUser = userService.getCurrentUser();
		if(currentUser == null) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		
		Message msg = messageService.get(messageId);
		
		messageService.update(msg);
		if(msg == null) {
			map.put("error", "no such message");			
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		if(msg.getOwnerId() != currentUser.getId()) {
			// in diesem kontext nicht falsch.
			map.put("error", "no such message");			
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		return Response.status(Status.OK).entity(msg).build();
	}
	
	/**
	 * Ruft alle Nachrichten (+- Pagination) ab, die dem Benutzerkontext zugeordnet werden.
	 * 
	 * TODO: Pagination-Parameter auch benutzen. 
	 * @param page {@link Long} Seite (im Sinne der Pagination)
	 * @param itemsPerPage {@link Long} (Nachrichten pro Seite)
	 * @param after {@link Date} frühester Zeitstempel der Nachrichten
	 * @param before {@link Date} spätester Zeitstempel der Nachrichten
	 * @param filter {@link String} Filterung (z.B. für eine Suche) 
	 * @return 
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/")
	public Response getMessages(@PathParam("page") Long page, @PathParam("itemsPerPage") Long itemsPerPage, @PathParam("after") Date after, @PathParam("before") Date before, @PathParam("filter") String filter) {
		PaginationInfo info = new PaginationInfo();
		info.setPage(page);
		info.setItemsPerPage(itemsPerPage);
		info.setBefore(before);
		info.setAfter(after);
		info.setFilter(filter);
		HashMap<String,String> map = new HashMap<String,String>();
		if(!subject.isAuthenticated()) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		User currentUser = userService.getCurrentUser();
		if(currentUser == null) {
			map.put("error", "not authenticated");
			return Response.status(Status.FORBIDDEN).entity(map).build();
		}
		
		ArrayList<Message> messages = messageService.getMessages(currentUser,info);
		return Response.status(Status.OK).entity(messages).build();
	}


}