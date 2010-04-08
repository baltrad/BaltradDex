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

import java.util.Properties;
import java.io.InputStream;

/**
 * Class implements Hibernate utility class.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class HibernateUtil {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.hibernate.properties";
//---------------------------------------------------------------------------------------- Variables
    // Hibernate session factory
    private static SessionFactory sessionFactory;
//------------------------------------------------------------------------------------------ Methods
    static {
        try {
            InputStream is = HibernateUtil.class.getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            props.load( is );
            sessionFactory =
                        new Configuration().setProperties( props ).configure().buildSessionFactory();
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
