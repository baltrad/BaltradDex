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

package eu.baltrad.dex.core.model;

import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;

import java.util.List;

/**
 * Node manager class implementing node connection object handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class NodeConnectionManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets list of all registered connections.
     *
     * @return List containing all registered node connections
     */
    public List getConnections() {
        List connections = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            connections = session.createQuery( "FROM NodeConnection" ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return connections;
    }
    /**
     * Gets node connection identified by connection id.
     *
     * @param id Node connection id
     * @return NodeConnection object
     */
    public NodeConnection getConnection( int id ) {
        NodeConnection nodeConnection = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            nodeConnection = ( NodeConnection )session.createQuery(
                    "FROM NodeConnection WHERE id = ?" ).setInteger( 0, id ).uniqueResult();
            session.getTransaction().commit();
        }
        catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return nodeConnection;
    }
    /**
     * Gets node connection identified by connection name.
     *
     * @param connectionName Node connection name
     * @return NodeConnection object
     */
    public NodeConnection getConnection( String connectionName ) {
        NodeConnection nodeConnection = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            nodeConnection = ( NodeConnection )session.createQuery(
                    "FROM NodeConnection WHERE connectionName = ?" ).setString(
                    0, connectionName ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return nodeConnection;
    }
    /**
     * Adds new connection to the database.
     *
     * @param nodeConnection NodeConnection class object
     */
    public void addConnection( NodeConnection nodeConnection ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( nodeConnection );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Removes node connection identified by ID.
     *
     * @param id Node connection ID
     */
    public void removeConnection( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	session.beginTransaction();
        try {
            session.delete( session.load( NodeConnection.class, new Integer( id ) ) );
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Removes all node connections.
     *
     * @return Number of removed records
     */
    public int removeConnections() {
        int res = 0;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	session.beginTransaction();
        try {
            Query query = session.createQuery( "DELETE FROM NodeConnection" );
            res = query.executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return res;
    }
}
//--------------------------------------------------------------------------------------------------