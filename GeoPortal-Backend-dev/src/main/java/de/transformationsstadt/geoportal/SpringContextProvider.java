package de.transformationsstadt.geoportal;


import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
 
@Component
public class SpringContextProvider{

    private static WebApplicationContext applicationContext;
    public static void setApplicationContext(WebApplicationContext context) {
    	SpringContextProvider.applicationContext = context;
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    public static <T> T getBean(String beanId, Class<T> beanClass) {
        return applicationContext.getBean(beanId, beanClass);
    }
    
    
}