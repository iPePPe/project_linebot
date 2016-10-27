package com.zygen.hcp.model.data;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zygen.hcp.jpa.Command;
/*import com.sap.espm.model.Customer;
import com.sap.espm.model.CustomerReview;
import com.sap.espm.model.Product;
import com.sap.espm.model.ProductCategory;
import com.sap.espm.model.Stock;
import com.sap.espm.model.Supplier;*/

/**
 * Data Loader tool for loading business partners and products into the db.
 * 
 */
public class DataLoader {

	private static Logger logger = LoggerFactory.getLogger(DataLoader.class);

	private EntityManagerFactory emf;

	public DataLoader(EntityManagerFactory emf) {
		this.emf = emf;
	}


	
	/**
	 * Load Customers to db from Business_Partners.xml.
	 */
	public void loadCommands() {
		EntityManager em = emf.createEntityManager();
		TypedQuery<Command> queryBP;
		List<Command> resBP;
		try {
			em.getTransaction().begin();
			queryBP = em
					.createQuery("SELECT c FROM Command c", Command.class);
			resBP = queryBP.getResultList();
			if (resBP.size() > 5) {
				logger.info(resBP.size()
						+ " Command already available in the db");
			} else {
				new XMLParser().readCommand(em,
						"com/zygen/hcp/model/data/Command.xml");
				em.getTransaction().commit();
				queryBP = em.createQuery("SELECT c FROM Command c",
						Command.class);
				resBP = queryBP.getResultList();
				logger.info(resBP.size() + " command loaded into the db");
			}
		} catch (Exception e) {
			logger.error("Exception occured", e);
		} finally {
			em.close();
		}
	}

	
/**
	 * Load Command to db from
	 * respective xml's and Generate Stock.
	 */
	public void loadData() {
		loadCommands();

	}

}
