package de.transformationsstadt.geoportal;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("de.transformationsstadt.geoportal")
@EnableJpaRepositories("de.transformationsstadt.DAO")
public class HibernateConfig {
	/**
	 * 
	 * Hibernate
	 * 
	 * 
	 * 
	 */
	
	
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] {"de.transformationsstadt.geoportal.entities","de.transformationsstadt.geoportal.api","de.transformationsstadt.geoportal.overpass" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		
		return sessionFactory;
	}
	
	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/dev");
		dataSource.setUsername("dev");
		dataSource.setPassword("dev");
		
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager hibernateTransactionManager() {
		HibernateTransactionManager htm = new HibernateTransactionManager();
		htm.setSessionFactory(sessionFactory().getObject());
		return htm;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect","org.hibernate.dialect.PostgreSQL94Dialect");
		hibernateProperties.setProperty("hibernate.connection.driver_class","org.postgresql.Driver");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto","create");
		hibernateProperties.setProperty("hibernate.show_sql","false");
		hibernateProperties.setProperty("hibernate.connection.characterEncoding","utf8");
		hibernateProperties.setProperty("hibernate.connection.useUnicode","true");
		//hibernateProperties.setProperty("hibernate.archive.autodetection","class,hbm");
		return hibernateProperties;
	}
	
	
}