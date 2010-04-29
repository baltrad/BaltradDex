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

import java.util.List;
import java.util.ArrayList;

/**
 * Data channel manager class implementing data channel handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ChannelManager {

//------------------------------------------------------------------------------------------ Methods    
    /**
     * Method creates list of available data channels.
     *
     * @return List of all available data channels.
     */
    public List getChannels( List userSubscriptions ) {
        List channelsList = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            channelsList = session.createQuery(
                    "FROM Channel channel ORDER BY channel.wmoNumber" ).list();
            session.getTransaction().commit();
        }
        catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        // Check for data channels selected by the currently signed user
        for( int i = 0; i < channelsList.size(); i++ ) {
            Channel channel = ( Channel )channelsList.get( i );
            for( int j = 0; j < userSubscriptions.size(); j++ ) {
                Subscription subscription = ( Subscription )userSubscriptions.get( j );
                if( subscription.getChannelId() == channel.getId() ) {
                    channel.setSelected( true );
                } 
            }
        }
        return channelsList;
    }
    /**
     * Method returns data channel identified by a given name.
     *
     * @param channelName Name of the data channel
     * @return Data channel identified by a given name
     */
    public Channel getChannel( String channelName ) {
        Channel channel = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            channel = ( Channel )session.createQuery( "FROM Channel WHERE name = ?" ).setString(
                    0, channelName ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return channel;
    }
    /**
     * Method returns data channel identified by a channel ID.
     *
     * @param id Data channel ID
     * @return Data channel identified by channel ID
     */
    public Channel getChannel( int id ) {
        Channel channel = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            channel = ( Channel )session.createQuery( "FROM Channel WHERE id = ?").setInteger(
                    0, id ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return channel;
    }
    /**
     * Method fetches IDs of the listed channels.
     *
     * @param channelNames List of channels' names
     * @return List of channel IDs
     */
    public List getChannelIds( String[ ] channelNames ) {
        List channelIds = new ArrayList();
        if( channelNames != null ) {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            for( int i = 0; i < channelNames.length; i++ ) {
                session.beginTransaction();
                try {
                    Channel channel = ( Channel )session.createQuery( "FROM Channel WHERE name = ?"
                            ).setString( 0, channelNames[ i ] ).uniqueResult();
                    session.getTransaction().commit();
                    channelIds.add( channel.getId() );
                } catch( HibernateException e ) {
                    session.getTransaction().rollback();
                    throw e;
                }
            }
        }
        return channelIds;
    }
}
//--------------------------------------------------------------------------------------------------