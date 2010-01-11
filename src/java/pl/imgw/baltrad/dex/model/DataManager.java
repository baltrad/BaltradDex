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
 * Data manager class implementing product handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataManager {

    /**
     * Method creates list of all available products.
     *
     * @return List of all available products.
     */
    public List getProductList() {
        List productList = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try
        {
            productList = session.createQuery( "from Data" ).list();
            session.getTransaction().commit();
        }
        catch ( HibernateException e )
        {
            session.getTransaction().rollback();
            throw e;
        }

        return productList;
    }

    /**
     * Method creates list of products available for a given data channel.
     *
     * @param dataChannelName Name of the data channel.
     * @return List of products available for a given data channel.
     */
    public List getProductsFromDataChannel( String dataChannelName ) {
        List dataList = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try
        {
            dataList = session.createQuery(
                    "from Data" + " where stationName = ?" ).setString(
                        0, dataChannelName ).list();
            session.getTransaction().commit();
        }
        catch ( HibernateException e )
        {
            session.getTransaction().rollback();
            throw e;
        }
        return dataList;
    }
    

    public void storeProduct( Data product ) {}

    public void deleteProduct( int productID ) {}

}
