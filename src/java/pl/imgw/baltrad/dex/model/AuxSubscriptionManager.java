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
import org.hibernate.Query;

import java.util.List;

/**
 * Auxiliary subscription manager class implementing subscription handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class AuxSubscriptionManager {

//------------------------------------------------------------------------------------------ Methods
    /**
     * Method saves subscription in the database.
     *
     * @param subscription Subscription object
     */
    public void registerSubscription( AuxSubscription auxSubscription ) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( auxSubscription );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Method removes subscription from the database.
     *
     * @param id Subscription ID
     */
    public void cancelSubscription( int id ) {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( AuxSubscription.class, new Integer( id ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Method gets subsciptions made by a given user.
     *
     * @param userID User ID
     * @return List of subscriptions
     */
    public List getUserSubscriptions( int userID ) {
        List subscriptionList = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subscriptionList = session.createQuery(
                    "from AuxSubscription" + " where user_id = ?" ).setInteger( 0, userID ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return subscriptionList;
    }

    /**
     * Method removes all subscriptions assigned to a given user
     *
     * @param userID User ID
     */
    public void cancelUserSubscriptions( int userID ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            String hql = "delete from AuxSubscription where user_id = " + userID;
            Query query = session.createQuery( hql );
            query.executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Method creates subscription list.
     *
     * @return Subscription list
     */
    public List getSubscriptionList() {
        List subscriptionList = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subscriptionList = session.createQuery( "from AuxSubscription" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return subscriptionList;
    }

}
//--------------------------------------------------------------------------------------------------
