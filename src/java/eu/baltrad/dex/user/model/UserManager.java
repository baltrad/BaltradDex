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

package eu.baltrad.dex.user.model;

import eu.baltrad.dex.util.HibernateUtil;
import eu.baltrad.dex.util.MessageDigestUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;

/**
 * User manager class implementing user object handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class UserManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method retrieves user with a given userID from the database.
     *
     * @param id User id
     * @return User object
     */
    public User getUserByID( int id ) {
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            user = ( User )session.createQuery(
                    "FROM User WHERE id = ?" ).setInteger( 0, id ).uniqueResult();
            session.getTransaction().commit();
        }
        catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }
    /**
     * Method retrieves user with a given name from the database.
     *
     * @param name User name
     * @return User object
     */
    public User getUserByName( String name ) {
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            user = ( User )session.createQuery(
                    "FROM User WHERE name = ?" ).setString( 0, name ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }
    /**
     * Method retrieves user with a given name hash.
     *
     * @param nameHash User name hash
     * @return User object
     */
    public User getUserByNameHash( String nameHash ) throws HibernateException {
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            user = ( User )session.createQuery(
                    "FROM User WHERE nameHash = ?" ).setString( 0, nameHash ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }
    /**
     * Method retrieves user based on associated node address.
     *
     * @param nodeAddress
     * @return User object
     */
    public User getUserByNodeAddress( String nodeAddress ) throws HibernateException {
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            user = ( User )session.createQuery(
                    "FROM User WHERE nodeAddress = ?" ).setString( 0, nodeAddress ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }
    /**
     * Gets all roles defined in the system.
     *
     * @return List of available roles
     */
    public List getAllRoles() {
        List roles = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            roles = session.createQuery( "FROM Role" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return roles;
    }
    /**
     * Checks if user with a given name exists in the database.
     *
     * @param name User name
     * @return True if user exists, false otherwise
     */
    public boolean userExists( String name ) {
        boolean res = false;
        List users = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            users = session.createQuery( "FROM User" ).list();
            session.getTransaction().commit();
            for( int i = 0; i < users.size(); i++ ) {
                User user = ( User )users.get( i );
                if( user.getName().equals( name ) ) {
                    res = true;
                }
            }
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return res;
    }
    /**
     * Gets list of all registered users.
     *  
     * @return List containing all registered users
     */
    public List getAllUsers() {
        List users = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            users = session.createQuery( "FROM User" ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return users;
    }
    /**
     * Adds user to the database.
     *
     * @param user User class object
     */
    public void addUser( User user ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            // encrypt passwords
            user.setNameHash( MessageDigestUtil.createHash( user.getName() ) );
            user.setPassword( MessageDigestUtil.createHash( user.getPassword() ) );
            user.setRetPassword( MessageDigestUtil.createHash( user.getRetPassword() ) );
            session.saveOrUpdate( user );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    /**
     * Deletes user with a given ID.
     *
     * @param id User ID
     */
    public void removeUser( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( User.class, new Integer( id ) ) );
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
//--------------------------------------------------------------------------------------------------
