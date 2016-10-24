package com.zygen.hcp.jpa.odata;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.factory.ODataJPAAccessFactory;
import org.apache.olingo.odata2.jpa.processor.api.factory.ODataJPAFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.account.TenantContext;
import com.zygen.hcp.jpa.JPAEntityFactoryManager;
import com.zygen.linebot.callback.CallbackHCPServlet;

public class NiceODataJPAServiceFactory extends ODataJPAServiceFactory {
	//private static final String PUNIT_NAME = "persistence-linebot";
	private static final Logger LOGGER = LoggerFactory.getLogger(NiceODataJPAServiceFactory.class);
	@SuppressWarnings("null")
	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		// TODO Auto-generated method stub
		ODataJPAContext oDataJPAContext = getODataJPAContext();
		EntityManagerFactory emf = null;
		EntityManager em = null ;
		try {
			
			InitialContext ctx = new InitialContext();
			Context envCtx = (Context) ctx.lookup(JPAEntityFactoryManager.COMP_ENV);
			TenantContext tenantContext = (TenantContext) envCtx.lookup("TenantContext");
			String currentTenantId = tenantContext.getTenant().getId();
			//DataSource ds = (DataSource)ctx.lookup(JPAEntityFactoryManager.DATA_SOURCE_NAME);
			//Map<String, Object> properties = new HashMap<String, Object>();
			//properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			//properties.put(JPAEntityFactoryManager.TENANT_ID, currentTenantId);
			//emf = Persistence.createEntityManagerFactory(JPAEntityFactoryManager.PERSISTENCE_UNIT_NAME, properties);
			emf = JPAEntityFactoryManager.getEntityManagerFactory();
			em = emf.createEntityManager();
			em.setProperty(JPAEntityFactoryManager.TENANT_ID, currentTenantId);
			oDataJPAContext.setEntityManagerFactory(emf);
			oDataJPAContext.setPersistenceUnitName(JPAEntityFactoryManager.PERSISTENCE_UNIT_NAME);
			oDataJPAContext.setEntityManager(em);
			

		    
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return oDataJPAContext;
	}

}
