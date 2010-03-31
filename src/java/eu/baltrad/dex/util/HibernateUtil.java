/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Class implements Hibernate utility class.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class HibernateUtil {
//---------------------------------------------------------------------------------------- Variables
    // Hibernate session factory
    private static final SessionFactory sessionFactory;
//------------------------------------------------------------------------------------------ Methods
    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch( Throwable t ) {
            System.out.println( "Failed to create Hibernate SessionFactory:" + t.getMessage() );
            throw new ExceptionInInitializerError( t );
        }
    }
    /**
     * Method gets reference to session factory object.
     *
     * @return Reference to session factory object
     */
    public static SessionFactory getSessionFactory() { return sessionFactory; }
}
//--------------------------------------------------------------------------------------------------
