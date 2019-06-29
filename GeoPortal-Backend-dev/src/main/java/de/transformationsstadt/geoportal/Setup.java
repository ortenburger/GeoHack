package de.transformationsstadt.geoportal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Setup beim Start der Server-Anwendung. 
 * 
 * @deprecated
 * @author Sebastian Bruch
 *
 */

@Component
public class Setup {
  
  @EventListener
  public void handleEvent(ContextRefreshedEvent  event) {
  }
}
