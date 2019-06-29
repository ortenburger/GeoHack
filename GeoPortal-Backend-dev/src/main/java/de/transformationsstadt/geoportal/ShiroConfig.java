package de.transformationsstadt.geoportal;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({ShiroBeanConfiguration.class,
        ShiroAnnotationProcessorConfiguration.class,
        ShiroWebConfiguration.class,
        ShiroWebFilterConfiguration.class})
public class ShiroConfig {

	@Bean
	public ShiroFilterChainDefinition shiroFilterChainDefinition() {
	    DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
	    chainDefinition.addPathDefinition("/**", "anon"); // all paths are managed via annotations
	    
	    // or allow basic authentication, but NOT require it.
	    // chainDefinition.addPathDefinition("/**", "authcBasic[permissive]"); 
	    return chainDefinition;
	}
	@Bean
	protected CacheManager cacheManager() {
	    return new MemoryConstrainedCacheManager();
	}
}
