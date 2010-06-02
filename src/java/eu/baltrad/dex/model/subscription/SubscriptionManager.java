/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.subscription;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;

import java.util.List;
import java.util.ArrayList;

/**
 * Subscription manager class implementing subscription handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method saves subscription in the database.
     *
     * @param subscription Subscription object
     */
    public void registerSubscription( Subscription subscription ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( subscription );
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
     * Method gets subsciptions made by a given user.
     *
     * @param userID User ID
     * @return List of subscriptions
     */
    public List getUserSubscriptions( int userId ) {
        List subscriptionList = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subscriptionList = session.createQuery(
                    "FROM Subscription WHERE user_id = ?" ).setInteger( 0, userId ).list();
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
    public void cancelUserSubscriptions( int userId ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            String hql = "DELETE FROM Subscription WHERE user_id = " + userId;
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
            subscriptionList = session.createQuery( "FROM Subscription" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return subscriptionList;
    }
    /**
     * Method creates list of channel IDs subscribed by a given user.
     *
     * @param userId User ID
     * @return List of channel IDs
     */
    public List getChannelIds( int userId ) {
        List subs = null;
        List channelIds = new ArrayList();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subs = session.createQuery( "FROM Subscription WHERE user_id = ?" ).setInteger(
                    0, userId ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        for( int i = 0; i < subs.size(); i++ ) {
            Subscription s = ( Subscription )subs.get( i );
            channelIds.add( s.getChannelId() );
        }
        return channelIds;
    }

}
//--------------------------------------------------------------------------------------------------
