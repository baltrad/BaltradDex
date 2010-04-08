/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

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
                    "from DeliveryRegisterEntry" + " where userId = ?" +
                    " and dataId = ?" ).setInteger( 0, userId ).setLong(
                    1, dataId ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deliveryRegisterEntry;
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
}
//--------------------------------------------------------------------------------------------------
