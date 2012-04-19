/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.baltrad.dex.log.controller;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author szewczenko
 */
public class Log4jLogControllerTest extends TestCase {

    private static Logger logger;

    public Logger getLogger( String name ) {
        logger = Logger.getLogger( name );
        logger.addAppender( new LogAppender( name ) );
        return logger;
    }

    @Override
    public void setUp() {
        //logger = getLogger( Log4jLogControllerTest.class );
    }
    
    public void testLog() {
        logger = getLogger( "dex" );
        logger.info( "Info message" );
        logger.warn( "Warning message" );
        logger.error( "Error message" );
        logger = getLogger( "beast" );
        logger.info( "Info message" );
        logger.warn( "Warning message" );
        logger.error( "Error message" );
    }

    class LogAppender extends AppenderSkeleton {

        public LogAppender( String name ) {
            setName( name );
        }

        public boolean requiresLayout() { return true; }
        public void close() {}
        public void append( LoggingEvent event ) {
            //System.out.println( "____________________msg: " + event.getRenderedMessage() +
            //        " level: " + event.getLevel().toString() + " logger name " + getName() );
        }

    }

}
