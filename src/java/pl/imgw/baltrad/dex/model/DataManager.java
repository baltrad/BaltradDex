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

import java.util.List;

/**
 * Data manager class implementing data handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataManager {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method creates list of all available data.
     *
     * @return List of all available data.
     */
    public List getData() {
        List dataList = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            dataList = session.createQuery( "from Data" ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return dataList;
    }
    /**
     * Method creates list of data available from a given data channel.
     *
     * @param channelName Name of the data channel.
     * @return List of data available from a given data channel.
     */
    public List getDataFromChannel( String channelName ) {
        List dataList = null;
        channelName = channelName.toLowerCase();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            dataList = session.createQuery( "from Data where channelName = ?" ).setString(
                                                                        0, channelName ).list();
            session.getTransaction().commit();
        } catch ( HibernateException e ) {
            session.getTransaction().rollback();
            throw e;
        }
        return dataList;
    }
    public void storeData( Data data ) {}
    public void deleteData( int dataId ) {}
}
//--------------------------------------------------------------------------------------------------
