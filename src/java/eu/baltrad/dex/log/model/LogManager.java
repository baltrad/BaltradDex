/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.log.model;

import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.util.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.Criteria;

import java.util.List;
import java.util.Date;
import java.util.Collections;
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
//---------------------------------------------------------------------------------------- Constants
    // Message type strings
    public final static String MSG_INFO = "INFO";
    public final static String MSG_WRN = "WARNING";
    public final static String MSG_ERR = "ERROR";
    public final static int LOG_ENTRY_LIST_SIZE = 12;

//------------------------------------------------------------------------------------------ Methods
    /**
     * Method gets all available log entries.
     * 
     * @return List object containing all available log entries 
     */
    public List getAllEntries() {
        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            logEntries = session.createQuery( "FROM LogEntry" ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        Collections.sort( logEntries, Collections.reverseOrder() );
        return logEntries;
    }
    /**
     * Method gets log entries with a given rank.
     *
     * @param rank Log entry rank
     * @return List object containing log entries with a given rank
     */
    public List getEntriesByRank( String rank ) {
        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            logEntries = session.createQuery( "FROM LogEntry WHERE id = ?" ).setString(
                                                                                0, rank ).list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
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
    public List getLastEntries( int numberOfEntries ) {
        List logEntries = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery( "FROM LogEntry logEntry" );
            Criteria criteria = session.createCriteria( LogEntry.class );
            criteria.setProjection( Projections.rowCount() );
            List list = criteria.list();
            int rows = ( Integer )list.get( 0 );
            if( rows > numberOfEntries ) {
                query.setMaxResults( numberOfEntries );
                query.setFirstResult( rows - numberOfEntries );
            }
            logEntries = query.list();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        Collections.sort( logEntries, Collections.reverseOrder() );
        return logEntries;
    }
    /**
     * Method adds log entry to the log entry list.
     *
     * @param logEntry Log entry
     */
    public void addEntry( LogEntry logEntry ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
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
     * @param type Log entry type
     * @param message Log entry message
     */
    public void addEntry( Date date, String type, String message ) {

        DateFormat dfDate = new SimpleDateFormat( "yyyy/MM/dd" );
        DateFormat dfTime = new SimpleDateFormat( "HH:mm:ss" );
        LogEntry logEntry = new LogEntry( dfDate.format( date ), dfTime.format( date ),
                                                                                type, message );
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
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
    public void deleteEntry( int id ) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
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
    public void deleteLastEntry() {
        LogEntry logEntry = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        // Get last entry
        session.beginTransaction();
        try {
            logEntry = ( LogEntry )session.createQuery( "FROM LogEntry logEntry " +
                                        "ORDER BY logEntry.id DESC LIMIT 1" ).uniqueResult();
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
    /**
     * Deletes all rows from log table.
     *
     * @return Number of deleted records
     */
    public int deleteAllEntries() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        int deletedEntries = 0;
	    session.beginTransaction();
        try {
            deletedEntries = session.createQuery( "DELETE FROM LogEntry" ).executeUpdate();
            session.getTransaction().commit();
        } catch( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return deletedEntries;
    }
}
//--------------------------------------------------------------------------------------------------
