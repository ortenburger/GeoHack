package de.transformationsstadt.geoportal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 *
 * @author Paul Samsotha
 */
@Order(1)
public class SpringInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.setInitParameter("contextConfigLocation", "noop");
        
        AnnotationConfigWebApplicationContext context
                = new AnnotationConfigWebApplicationContext();
        context.register(HibernateConfig.class);
        SpringContextProvider.setApplicationContext(context);
        sc.addListener(new ContextLoaderListener(context));
        sc.addListener(new RequestContextListener());
    } 
}
