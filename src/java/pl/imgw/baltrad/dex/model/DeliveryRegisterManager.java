/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import pl.imgw.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
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

    /**
     * Method retrieves single entry from data delivery register.
     * 
     * @param userID User ID
     * @param dataID Data ID
     * @return Single entry data delivery register
     */
    public DeliveryRegisterEntry getEntry( int userID, int dataID ) {

        DeliveryRegisterEntry deliveryRegisterEntry = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            deliveryRegisterEntry = ( DeliveryRegisterEntry )session.createQuery(
                    "from DeliveryRegisterEntry" + " where userID = ?" +
                    " and dataID = ?" ).setInteger( 0, userID ).setInteger(
                    1, dataID ).uniqueResult();
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
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
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
     * Method deletes given entry from data delivery register
     *
     * @param registerEntryID Register entry ID
     */
    public void deleteEntry( int registerEntryID ) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( Subscription.class, new Integer( registerEntryID ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }

}
