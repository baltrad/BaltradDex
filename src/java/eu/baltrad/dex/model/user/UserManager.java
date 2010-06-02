/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.user;

import eu.baltrad.dex.util.HibernateUtil;
import eu.baltrad.dex.util.MessageDigestUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;

/**
 * User manager class implementing user-object handling functionality.
 *
 * @author szewczenko
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
     * Method retrieves user with a given role from the database.
     * Not applicable to multiple users of the same role.
     *
     * @param role User role
     * @return User object
     */
    public User getUserByRole( String role ) {
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            user = ( User )session.createQuery(
                    "FROM User WHERE role = ?" ).setString( 0, role ).uniqueResult();
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
