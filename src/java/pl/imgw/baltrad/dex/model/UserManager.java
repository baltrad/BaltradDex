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
                    "from User" + " where id = ?" ).setInteger( 0, id ).uniqueResult();
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
                    "from User" + " where name = ?" ).setString( 0, name ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }
    /**
     * Method retrieves user with a given role from the database.
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
                    "from User where role = ?" ).setString( 0, role ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return user;
    }

    public void addUser() {}
    public void deleteUser( int id ) {}
    public void deleteUser( String name ) {}
}
//--------------------------------------------------------------------------------------------------
