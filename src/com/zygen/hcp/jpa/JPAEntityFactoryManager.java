package com.zygen.hcp.jpa;

import java.util.HashMap;
import java.util.Map;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;



public class JPAEntityFactoryManager {
	public static final String DATA_SOURCE_NAME = "java:comp/env/jdbc/DefaultDB";
	public static final String COMP_ENV = "java:comp/env";
	public static final String PERSISTENCE_UNIT_NAME = "persistence-linebot";
	public static final String TENANT_ID = "me-tenant.id";
	private static EntityManagerFactory emf;
	
	public static synchronized EntityManagerFactory getEntityManagerFactory() throws NamingException{
		if (emf == null){
			InitialContext ctx = new InitialContext();

			DataSource ds = (DataSource)ctx.lookup(DATA_SOURCE_NAME);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			properties.put(PersistenceUnitProperties.CACHE_SHARED_, true);
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		}
		
		return emf;
		
	}
}
