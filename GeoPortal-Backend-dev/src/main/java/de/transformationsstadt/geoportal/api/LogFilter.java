package de.transformationsstadt.geoportal.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.validation.constraints.NotNull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import de.transformationsstadt.geoportal.DAO.LogEntryDAO;
import de.transformationsstadt.geoportal.entities.LogEntry;
import de.transformationsstadt.geoportal.services.LogService;
/**
 * TODO
 * @author Sebastian Bruch
 *
 */
@Provider
public class LogFilter implements ContainerResponseFilter {

    @Context
    private Providers providers;
    
    @Autowired
    LogService logService;
    
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
		throws IOException {
	
		Subject subject = SecurityUtils.getSubject();
		String user = (String)subject.getPrincipal();
		String path = requestContext.getUriInfo().getPath();
		String method = requestContext.getMethod();
		String data = "";
		/*
		if(requestContext.hasEntity()) {
			ByteArrayInputStream resettableIS = toResettableStream(requestContext.getEntityStream());
		}*/
		
		int status = responseContext.getStatus();
		LogEntry log = new LogEntry(user,method,path,data,status);
		logService.create(log);
		System.out.println(method+"-request to "+ path);
	}
	/*
	@NotNull
	private ByteArrayInputStream toResettableStream(InputStream entityStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = entityStream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }
    */
}