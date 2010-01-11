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
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Class implementing log handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class LogManager {

//------------------------------------------------------------------------------------------ Methods
    /**
     * Method gets all available log entries.
     * 
     * @return List object containing all available log entries 
     */
    public List getLogEntries() {

        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try
        {
            logEntries = session.createQuery( "from LogEntry" ).list();
            session.getTransaction().commit();
        }
        catch ( HibernateException e )
        {
            session.getTransaction().rollback();
            throw e;
        }
        return logEntries;
    }

    /**
     * Method gets log entries with a given rank.
     *
     * @param rank Log entry rank
     * @return List object containing log entries with a given rank
     */
    public List getLogEntriesByRank( String rank ) {
        
        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            logEntries = session.createQuery( "from LogEntry where id = ?" ).setString(
                                                                                0, rank ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return logEntries;
    }

    /**
     * Method gets last log entries.
     *
     * @param numberOfEntries Number of log entries to retrieve
     * @return List object containing a given number of last log entries.
     */
    public List getLastLogEntries( int numberOfEntries ) {

        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            logEntries = session.createQuery( "from LogEntry logEntry " +
                                "order by logEntry.id desc limit " + numberOfEntries ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return logEntries;
    }

    /**
     * Method adds log entry to the log entry list.
     *
     * @param logEntry Log entry
     */
    public void addLogEntry( LogEntry logEntry ) {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( logEntry );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Method adds log entry to the log entry list.
     *
     * @param date Log entry date
     * @param rank Log entry rank
     * @param text Log entry text
     */
    public void addLogEntry( Date date, String rank, String text ) {

        DateFormat dfDate = new SimpleDateFormat( "yyyy/MM/dd" );
        DateFormat dfTime = new SimpleDateFormat( "HH:mm:ss" );
        LogEntry logEntry = new LogEntry( rank, dfDate.format( date ), dfTime.format( date ), text );

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate( logEntry );
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Method deletes single entry from log entry list.
     *
     * @param id Log entry ID
     */
    public void deleteLogEntry( int id ) {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
	    session.beginTransaction();
        try {
            session.delete( session.load( Subscription.class, new Integer( id ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /**
     * Method deletes last entry from the log entry list.
     */
    public void deleteLastLogEntry() {
        LogEntry logEntry = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        // Get last entry
        session.beginTransaction();
        try {
            logEntry = ( LogEntry )session.createQuery( "from LogEntry logEntry " +
                                        "order by logEntry.id desc limit 1" ).uniqueResult();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        // Delete last entry
        session.beginTransaction();
        try {
            session.delete( session.load( Subscription.class, new Integer(
                                                                    logEntry.getId() ) ) );
            session.flush();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
    }

}

//--------------------------------------------------------------------------------------------------
