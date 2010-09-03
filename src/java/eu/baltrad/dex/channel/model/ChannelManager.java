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

package eu.baltrad.dex.channel.model;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;
import java.util.ArrayList;

/**
 * Data channel manager class implementing data channel handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class ChannelManager {

//------------------------------------------------------------------------------------------ Methods    
    /**
     * Gets list of all registered channels.
     *
     * @return List containing all registered channels
     */
    public List getChannels() {
        List channels = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            channels = session.createQuery( "FROM Channel" ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return channels;
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
            channel = ( Channel )session.createQuery( "FROM Channel WHERE channelName = "+
                    "?" ).setString( 0, channelName ).uniqueResult();
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
                    Channel channel = ( Channel )session.createQuery( "FROM Channel WHERE " +
                            " channelName = ?" ).setString( 0, channelNames[ i ] ).uniqueResult();
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
    /**
     * Adds data channel to the database.
     *
     * @param channel Channel class object
     */
    public void addChannel( Channel channel ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( channel );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Deletes channel with a given ID.
     *
     * @param id Channel ID
     */
    public void removeChannel( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( Channel.class, new Integer( id ) ) );
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
//--------------------------------------------------------------------------------------------------