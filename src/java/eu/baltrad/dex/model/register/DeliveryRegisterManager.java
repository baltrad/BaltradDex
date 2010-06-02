/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.register;

import eu.baltrad.dex.model.subscription.Subscription;
import eu.baltrad.dex.model.register.DeliveryRegisterEntry;
import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;

/**
 * Class implementing data delivery handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DeliveryRegisterManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method retrieves single entry from data delivery register.
     * 
     * @param userId User id
     * @param dataId Data id
     * @return Single entry from data delivery register
     */
    public DeliveryRegisterEntry getEntry( int userId, long dataId ) {
        DeliveryRegisterEntry deliveryRegisterEntry = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            deliveryRegisterEntry = ( DeliveryRegisterEntry )session.createQuery(
                    "FROM DeliveryRegisterEntry" + " WHERE userId = ?" +
                    " AND dataId = ?" ).setInteger( 0, userId ).setLong(
                    1, dataId ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deliveryRegisterEntry;
    }
    /**
     * Method gets all available elivery register entries.
     *
     * @return List object containing all available delivery register entries
     */
    public List getAllEntries() {
        List regEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            regEntries = session.createQuery( "FROM DeliveryRegisterEntry" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return regEntries;
    }
    /**
     * Method adds an entry to data delivery register.
     *
     * @param dataDeliveryRegister Data delivery register entry
     */
    public void addEntry( DeliveryRegisterEntry deliveryRegisterEntry ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( deliveryRegisterEntry );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Method deletes given entry from data delivery register.
     *
     * @param id Register entry ID
     */
    public void deleteEntry( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( Subscription.class, new Integer( id ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Deletes all rows from data delivery register table.
     *
     * @return Number of deleted records
     */
    public int deleteAllEntries() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        int deletedEntries = 0;
	    session.beginTransaction();
        try {
            deletedEntries = session.createQuery(
                    "DELETE FROM DeliveryRegisterEntry" ).executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deletedEntries;
    }
}
//--------------------------------------------------------------------------------------------------
