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
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;

/**
 * Product manager class implementing data channel handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataChannelManager {

    /**
     * Method creates list of available data channels.
     *
     * @return List of all available data channels.
     */
    public List getDataChannels( List userSubscriptions ) {
        List dataChannelsList = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            dataChannelsList = session.createQuery(
                    "from DataChannel dataChannel order by dataChannel.wmoNumber" ).list();
            session.getTransaction().commit();
        }
        catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        // Change the case of the first character
        for( int i = 0; i < dataChannelsList.size(); i++ ) {
            DataChannel dataChannel = ( DataChannel )dataChannelsList.get( i );
            String in = dataChannel.getName();
            if( !in.isEmpty() && in != null ) {
                String out = in.substring( 0, 1 ).toUpperCase() + in.substring( 1, in.length() );
                dataChannel.setName( out );
                dataChannelsList.set( i, dataChannel );
            }
        }
        // Check for data channels selected by the currently signed user
        for( int i = 0; i < dataChannelsList.size(); i++ ) {
            DataChannel dataChannel = ( DataChannel )dataChannelsList.get( i );
            for( int j = 0; j < userSubscriptions.size(); j++ ) {
                Subscription subscription = ( Subscription )userSubscriptions.get( j );

                if( subscription.getDataChannelID() == dataChannel.getDataChannelID() ) {
                    dataChannel.setChecked( "checked" );
                } 
            }
        }
        return dataChannelsList;
    }

    /**
     * Method returns data channel identified by a given name.
     *
     * @param dataChannelName Name of the data channel
     * @return Data channel identified by a given name
     */
    public DataChannel getDataChannel( String dataChannelName ) {

        DataChannel dataChannel = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            dataChannel = ( DataChannel )session.createQuery( "from DataChannel" +
                    " where name= ?" ).setString( 0, dataChannelName ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        // Change the case of the first character
        String in = dataChannel.getName();
        if( !in.isEmpty() && in != null ) {
            String out = in.substring( 0, 1 ).toUpperCase() + in.substring( 1, in.length() );
            dataChannel.setName( out );
        }
        return dataChannel;
    }

    /**
     * Method returns data channel identified by a channel ID.
     *
     * @param dataChannelID Data channel ID
     * @return Data channel identified by channel ID
     */
    public DataChannel getDataChannel( int dataChannelID ) {

        DataChannel dataChannel = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            dataChannel = ( DataChannel )session.createQuery( "from DataChannel" +
                    " where dataChannelID = ?").setInteger( 0, dataChannelID ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        // Change the case of the first character
        String in = dataChannel.getName();
        if( !in.isEmpty() && in != null ) {
            String out = in.substring( 0, 1 ).toUpperCase() + in.substring( 1, in.length() );
            dataChannel.setName( out );
        }
        return dataChannel;
    }

}
