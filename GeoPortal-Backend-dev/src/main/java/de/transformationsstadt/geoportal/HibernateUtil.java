package de.transformationsstadt.geoportal;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.service.ServiceRegistry;
/**
 * Hibernate-Helper
 * @deprecated
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class HibernateUtil {
	private static final SessionFactory sessionFactory;

	//private static ServiceRegistry serviceRegistry;

	static {
		
		try {
		StandardServiceRegistry standardRegistry = 
		new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		Metadata metaData = 
		new MetadataSources(standardRegistry).getMetadataBuilder().build();
			sessionFactory = metaData.getSessionFactoryBuilder().build();
		} catch (Throwable th) {

			System.err.println("Enitial SessionFactory creation failed" + th);
			throw new ExceptionInInitializerError(th);

		}
		
	}
	
	public static Session getCurrentSession() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if(session.isOpen()) {
			return session;
		}else {
			return sessionFactory.openSession();
		}
	}
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}