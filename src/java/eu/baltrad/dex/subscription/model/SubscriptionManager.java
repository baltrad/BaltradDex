/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.subscription.model;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;

import java.util.List;

/**
 * Subscription manager inplementing subscription handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method gets all available subscriptions.
     *
     * @return List containing all available subscriptions
     */
    public List getSubscriptions() {
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
        } finally {
            session.close();
        }
        return subscriptionList;
    }
    /**
     * Method saves subscription in the database.
     *
     * @param subscription Subscription object
     */
    public void addSubscription( Subscription subscription ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( subscription );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    /**
     * Selects subscription by channel name and type, updates subscription object
     * and saves it int the database.
     *
     * @param channelName Channel name
     * @param type Subscription type
     * @param selected Channel selection status - updated parameter
     */
    public void updateSubscription( String channelName, String type, boolean selected ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Subscription subs = ( Subscription )session.createQuery(
                    "FROM Subscription WHERE channelName = ? AND type = ?" ).setString( 0,
                    channelName ).setString( 1, type ).uniqueResult();
            subs.setSelected( selected );
            session.update( subs );
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    /**
     * Method removes subscription from the database.
     *
     * @param id Subscription ID
     */
    public void removeSubscription( int id ) throws HibernateException {
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
        } finally {
            session.close();
        }
    }
    /**
     * Method removes subscription identified by a given user name, channel name and type.
     *
     * @param channelName Channel name
     * @param type Subscription type
     */
    public void removeSubscription( String channelName, String type ) throws HibernateException {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            String hql = "DELETE FROM Subscription WHERE channel_name = :channelName "
                    + "AND type = :type";
            Query query = session.createQuery( hql );
            query.setString( "channelName", channelName );
            query.setString( "type", type );
            query.executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    /**
     * Method removes subscription identified by a given user name, channel name and type.
     *
     * @param userName User name
     * @param channelName Channel name
     * @param type Subscription type
     */
    public void removeSubscription( String userName, String channelName, String type )
            throws HibernateException {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            String hql = "DELETE FROM Subscription WHERE user_name = :userName AND " +
                    "channel_name = :channelName AND type = :type";
            Query query = session.createQuery( hql );
            query.setString( "userName", userName );
            query.setString( "channelName", channelName );
            query.setString( "type", type );
            query.executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    /**
     * Gets subsciptions by type.
     *
     * @param type Subscription type
     * @return List of subscriptions of a given type
     */
    public List getSubscriptionsByType( String type ) {
        List subscriptionList = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subscriptionList = session.createQuery(
                    "FROM Subscription WHERE type = ?" ).setString( 0, type ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return subscriptionList;
    }
    /**
     * Gets unique subsciption identified by data channel name and subscription type.
     *
     * @param channelName Channel name
     * @param type Subscription type
     * @return Subscription object
     */
    public Subscription getSubscription( String channelName, String type ) {
        Subscription subs = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subs = ( Subscription )session.createQuery(
                    "FROM Subscription WHERE channelName = ? AND type = ?" ).setString( 0, 
                    channelName ).setString( 1, type ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return subs;
    }
    /**
     * Gets unique subsciption identified by user name, data channel name and subscription type.
     *
     * @param userName User name
     * @param channelName Channel name
     * @param type Subscription type
     * @return Subscription object
     */
    public Subscription getSubscription( String userName, String channelName, String type ) {
        Subscription subs = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            subs = ( Subscription )session.createQuery(
                    "FROM Subscription WHERE userName = ? AND channelName = ? " + 
                    "AND type = ?" ).setString( 0, userName ).setString( 1,
                    channelName ).setString( 2, type ).uniqueResult();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
        return subs;
    }
    /**
     * Compares two subscription lists based on chosen subscription field values.
     *
     * @param s1 First subscription list
     * @param s2 Second subscription list
     * @return True if field values are equal
     */
    public boolean compareSubscriptionLists( List< Subscription > s1, List< Subscription > s2 ) {
        boolean res = true;
        if( s1.size() != s2.size() ) {
            res = false;
        } else {
            for( int i = 0; i < s1.size(); i++ ) {
                if( !s1.get( i ).getChannelName().equals( s2.get( i ).getChannelName() ) ||
                        !s1.get( i ).getNodeAddress().equals( s2.get( i ).getNodeAddress() ) ||
                        !s1.get( i ).getOperatorName().equals( s2.get( i ).getOperatorName() ) ||
                        !s1.get( i ).getType().equals( s2.get( i ).getType() ) ||
                        s1.get( i ).getSelected() != s2.get( i ).getSelected() ) {
                    res = false;
                }
            }
        }
        return res;
    }
    /**
     * Compares two subscription objects based on chosen field values.
     *
     * @param s1 First subscription object
     * @param s2 Second subscription object
     * @return True if field values are equal
     */
    public boolean compareSubscriptions( Subscription s1, Subscription s2 ) {
        boolean res = true;
        if( !s1.getChannelName().equals( s2.getChannelName() ) ||
                !s1.getNodeAddress().equals( s2.getNodeAddress() ) ||
                !s1.getOperatorName().equals( s2.getOperatorName() ) ||
                !s1.getType().equals( s2.getType() ) || s1.getSelected() != s2.getSelected() ) {
            res = false;
        }
        return res;
    }
}
//--------------------------------------------------------------------------------------------------
