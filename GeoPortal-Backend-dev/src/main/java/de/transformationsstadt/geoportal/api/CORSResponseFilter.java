package de.transformationsstadt.geoportal.api;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Setzt die CORS-Header (Cross-Origins-Resource-Sharing) für die API.
 * 
 * @author Sebastian Bruch
 *
 */
@Provider
public class CORSResponseFilter
implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
		throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, content-type, Content-Type, Accept, authorization, Authorization");
		headers.add("Content-Encoding", "UTF-8");
	}
}