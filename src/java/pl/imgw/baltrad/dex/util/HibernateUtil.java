/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.util;

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

    // Hibernate session factory
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch( Throwable ex ) {
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
