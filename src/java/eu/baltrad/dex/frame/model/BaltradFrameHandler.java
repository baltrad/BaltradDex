/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.frame.model;

import eu.baltrad.dex.log.model.LogManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.HttpEntity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Date;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Class implementing Baltrad message handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class BaltradFrameHandler {
//---------------------------------------------------------------------------------------- Constants
    // XML element / document version
    private static final String XML_VERSION = "1.0";
    // XML element / document encoding
    private static final String XML_ENCODING = "UTF-8";
    // XML element / root node
    private static final String BF_ROOT_ELEM = "baltrad_frame";
    // XML element / header node
    private static final String BF_HEADER = "header";
    // XML element / MIME type attribute
    private static final String BF_MIME_TYPE = "mimetype";

    // XML elements / available MIME types
    public static final String BF_MIME_MULTIPART = "multipart/form-data";
    public static final String BF_MIME_APPLICATION = "application/octet-stream";
    public static final String BF_MIME_TEXT = "text/plain";
    public static final String BF_MIME_TEXT_XML = "text/xml";
    
    // XML element / user name attribute
    private static final String BF_USER_NAME = "user_name";
    // XML element / user's password attribute
    private static final String BF_PASSWD = "passwd";
    // XML element / sender node address
    private static final String BF_SENDER_NODE_ADDRESS = "sender_node_address";
    // XML element / sender node name
    private static final String BF_SENDER_NODE_NAME = "sender_node_name";

    // XML element / content node
    private static final String BF_CONTENT = "content";
    // XML element / content type
    private static final String BF_CONTENT_TYPE = "type";

    // frame content identifiers
    // XML element / message content frame
    public static final String BF_MSG = "message";
    // XML element / file content frame
    public static final String BF_FILE = "file";
    // XML element / object content frame
    public static final String BF_OBJECT = "object";

    // XML element / message type attribute
    private static final String BF_MSG_CLASS = "class";
    // XML elements / message type attribute values
    public static final String BF_MSG_INFO = "INFO";
    public static final String BF_MSG_WRN = "WARNING";
    public static final String BF_MSG_ERR = "ERROR";
    public static final String BF_MSG_REQUEST = "REQUEST";

    // channel list request string
    public static final String BF_MSG_CHANNEL_LISTING_REQUEST = "channel_list_request";
    // channel list object identifier
    public static final String BF_MSG_CHANNELS_LIST = "channels_list";
    // channel subscription request
    public static final String BF_MSG_CHANNEL_SUBSCRIPTION_REQUEST = "channel_subscription_request";
    // channel subscription request
    public static final String BF_MSG_CHANNEL_SUBSCRIPTION_CONFIRMATION =
            "channel_subscription_confirmation";
    // channel subscription change request
    public static final String BF_MSG_SUBSCRIPTION_CHANGE_REQUEST = "subscription_change_request";
    // channel subscription change success message
    public static final String BF_MSG_SUBSCRIPTION_CHANGE_SUCCESS = "subscription_change_success";
    // channel subscription change failure message
    public static final String BF_MSG_SUBSCRIPTION_CHANGE_FAILURE = "subscription_change_failure";

    // XML element / message text attribute
    private static final String BF_MSG_TEXT = "text";    
    
    // XML element / relative file name attribute
    private static final String BF_FILE_NAME = "name";
    // XML element / data channel attribute
    private static final String BF_CHANNEL_NAME = "channel";
    // Character set
    private static final Charset BF_CHARSET = Charset.forName( XML_ENCODING );
//---------------------------------------------------------------------------------------- Variables
    // Receiver's URL address
    private String url;
    // Reference to LoManager class object
    private LogManager logManager = new LogManager();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public BaltradFrameHandler() {}
    /**
     * Constructor sets field values.
     *
     * @param url Receiver's URL address
     */
    public BaltradFrameHandler( String url ) { this.url = url; }
    /**
     * Method posts data on the receiver's server.
     */
    public void handleBF( BaltradFrame baltradFrame ) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter( CoreProtocolPNames.PROTOCOL_VERSION,
                                                                            HttpVersion.HTTP_1_1 );
            HttpPost httpPost = new HttpPost( getUrl() );
            httpPost.setEntity( baltradFrame );
            HttpResponse response = httpClient.execute( httpPost );
            HttpEntity resEntity = response.getEntity();
            if( resEntity != null ) {
                resEntity.consumeContent();
            }
            httpClient.getConnectionManager().shutdown();
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Frame handler error:"
                    + e.getMessage() );
        }
    }
    /**
     * Creates XML header for file frame.
     *
     * @param mimeType MIME message type
     * @param userName User's name
     * @param passwd User's password
     * @param nodeName Sender node name
     * @param channel Channel of origin
     * @param absFilePath Absolute file path
     * @return XML header as string
     */
    public String createDataHdr( String mimeType, String userName, String passwd, String nodeName,
            String channel, String absFilePath ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set user name
            header.setAttribute( BF_USER_NAME, userName );
            // set password
            header.setAttribute( BF_PASSWD, passwd );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            root.appendChild( header );
            // File object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_FILE );
            content.setAttribute( BF_FILE_NAME, absFilePath.substring( 
                    absFilePath.lastIndexOf( File.separator ) + 1, absFilePath.length() ) );
            content.setAttribute( BF_CHANNEL_NAME, channel );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Creates XML header for file frame.
     *
     * @param mimeType MIME message type
     * @param nodeName Sender node name
     * @param channel Channel of origin
     * @param absFilePath Absolute file path
     * @return XML header as string
     */
    public String createDataHdr( String mimeType, String nodeName, String channel,
            String absFilePath ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            root.appendChild( header );
            // File object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_FILE );
            content.setAttribute( BF_FILE_NAME, absFilePath.substring(
                    absFilePath.lastIndexOf( File.separator ) + 1, absFilePath.length() ) );
            content.setAttribute( BF_CHANNEL_NAME, channel );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Creates XML header for message frame.
     *
     * @param mimeType MIME message type
     * @param userName User's name
     * @param passwd User's password
     * @param nodeAddress Sender node address
     * @param nodeName Sender node name
     * @param msgClass Message class
     * @param msgText Message text
     * @return XML header as string
     */
    public String createMsgHdr( String mimeType, String userName, String passwd, String nodeAddress,
            String nodeName, String msgClass, String msgText ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set user name
            header.setAttribute( BF_USER_NAME, userName );
            // set password
            header.setAttribute( BF_PASSWD, passwd );
            // set sender node address
            header.setAttribute( BF_SENDER_NODE_ADDRESS, nodeAddress );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            root.appendChild( header );
            // Message object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_MSG );
            content.setAttribute( BF_MSG_CLASS, msgClass );
            content.setAttribute( BF_MSG_TEXT, msgText );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Creates XML header for message frame.
     *
     * @param mimeType MIME message type
     * @param nodeAddress Sender node address
     * @param nodeName Sender node name
     * @param msgClass Message class
     * @param msgText Message text
     * @return XML header as string
     */
    public String createMsgHdr( String mimeType, String nodeAddress, String nodeName,
            String msgClass, String msgText ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set sender node address
            header.setAttribute( BF_SENDER_NODE_ADDRESS, nodeAddress );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            root.appendChild( header );
            // Message object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_MSG );
            content.setAttribute( BF_MSG_CLASS, msgClass );
            content.setAttribute( BF_MSG_TEXT, msgText );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Creates XML header for object frame.
     *
     * @param mimeType MIME message type
     * @param nodeAddress Sender node address
     * @param nodeName Sender node name
     * @param msgText Message text
     * @param absFilePath Absolute path to the object-holding file
     * @return XML header as string
     */
    public String createObjectHdr( String mimeType, String nodeAddress, String nodeName,
            String msgText, String absFilePath ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set sender node addressheader
            header.setAttribute( BF_SENDER_NODE_ADDRESS, nodeAddress );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            root.appendChild( header );
            // Message object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_OBJECT );
            content.setAttribute( BF_MSG_TEXT, msgText );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Creates XML header for object frame.
     *
     * @param mimeType MIME message type
     * @param nodeAddress Sender node address
     * @param nodeName Sender node name
     * @param localUserName User name on the local (receiving) server
     * @param msgText Message text
     * @param absFilePath Absolute path to the object-holding file
     * @return XML header as string
     */
    public String createObjectHdr( String mimeType, String nodeAddress, String nodeName,
            String localUserName, String msgText, String absFilePath ) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            // Create document root
            Element root = doc.createElement( BF_ROOT_ELEM );
            doc.appendChild( root );
            // Header definition tag
            Element header = doc.createElement( BF_HEADER );
            // MIME content-type identifier
            header.setAttribute( BF_MIME_TYPE, mimeType );
            // set sender node addressheader
            header.setAttribute( BF_SENDER_NODE_ADDRESS, nodeAddress );
            // set sender node name
            header.setAttribute( BF_SENDER_NODE_NAME, nodeName );
            // set local user name
            header.setAttribute( BF_USER_NAME, localUserName );
            root.appendChild( header );
            // Message object definition tag
            Element content = doc.createElement( BF_CONTENT );
            content.setAttribute( BF_CONTENT_TYPE, BF_OBJECT );
            content.setAttribute( BF_MSG_TEXT, msgText );
            root.appendChild( content );
        } catch( ParserConfigurationException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML parser error:"
                    + e.getMessage() );
        }
        // Transform XML header to string
        return xmlDocToString( XML_VERSION, XML_ENCODING, doc );
    }
    /**
     * Method transforms XML document into string.
     *
     * @param xmlVersion XML document version
     * @param xmlEncoding XML document encoding
     * @param doc XML document
     * @return XML document as string
     */
    public String xmlDocToString( String xmlVersion, String xmlEncoding, Document doc ) {
        OutputFormat format = new OutputFormat( doc );
        format.setVersion( xmlVersion );
        format.setEncoding( xmlEncoding );
        format.setIndenting( true );
        StringWriter xmlStringWriter = new StringWriter();
        XMLSerializer serializer = new XMLSerializer( xmlStringWriter, format );
        try {
            serializer.serialize( doc );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "XML document serialization"
                    + "error: " + e.getMessage() );
        }
        return xmlStringWriter.toString();
    }
    /**
     * Method transforms string into XML document.
     *
     * @param xmlString Input string
     * @return XML document
     */
    public Document stringToXMLDocument( String xmlString ) {
        Document doc = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse( new InputSource( new StringReader( xmlString ) ) );
            doc = parser.getDocument();
        } catch( SAXException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "String to XML document "
                    + "transformation error: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "String to XML document "
                    + "transformation error: " + e.getMessage() );
        }
        return doc;
    }
    /**
     * Method retrieves given element from XML document
     *
     * @param doc XML document
     * @param tagName Target XML tag name
     * @param attributeName Target attribute name
     * @return XML element value as string
     */
    public String getXMLHeaderElement( Document doc, String tagName, String attributeName ) {
        Node node = null;
        NodeList nodes = doc.getElementsByTagName( tagName );
        if( nodes.item( 0 ).hasAttributes() ) {
            NamedNodeMap map = nodes.item( 0 ).getAttributes();
            node = map.getNamedItem( attributeName );
        }
        return node.getNodeValue();
    }
    /**
     * Method gets message MIME type.
     *
     * @param xmlHdrStr XML header string
     * @return Message MIME type
     */
    public String getMimeType( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_HEADER,
                BF_MIME_TYPE );
    }
    /**
     * Method gets frame content type
     *
     * @param xmlHdrStr XML header string
     * @return Frame content type
     */
    public String getContentType( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_CONTENT,
                BF_CONTENT_TYPE );
    }
    /**
     * Method gets user name.
     *
     * @param xmlHdrStr XML header string
     * @return User name
     */
    public String getUserName( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_HEADER,
                BF_USER_NAME );
    }
    /**
     * Method gets user's password.
     *
     * @param xmlHdrStr XML header string
     * @return User's password
     */
    public String getPassword( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_HEADER,
                BF_PASSWD );
    }
    /**
     * Gets sender node address.
     *
     * @param xmlHdrStr XML header string
     * @return Sender node address
     */
    public String getSenderNodeAddress( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_HEADER,
                BF_SENDER_NODE_ADDRESS );
    }
    /**
     * Gets sender node name.
     *
     * @param xmlHdrStr XML header string
     * @return Sender node name
     */
    public String getSenderNodeName( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_HEADER,
                BF_SENDER_NODE_NAME );
    }
    /**
     * Method gets data channel name.
     *
     * @param xmlHdrStr XML header string
     * @return Data channel name
     */
    public String getChannel( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_CONTENT,
                BF_CHANNEL_NAME );
    }
    /**
     * Method gets data file name.
     *
     * @param xmlHdrStr XML header string
     * @return Data file name
     */
    public String getFileName( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_CONTENT,
                BF_FILE_NAME );
    }
    /**
     * Method gets message type.
     *
     * @param xmlHdrStr XML header string
     * @return Message type identifier
     */
    public String getMessageClass( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_CONTENT,
                BF_MSG_CLASS );
    }
    /**
     * Method gets message text.
     *
     * @param xmlHdrStr XML header string
     * @return Message text
     */
    public String getMessageText( String xmlHdrStr ) {
        return getXMLHeaderElement( stringToXMLDocument( xmlHdrStr ), BF_CONTENT,
                BF_MSG_TEXT );
    }
    /**
     * Method gets receiver's URL address.
     *
     * @return Receiver's URL address
     */
    public String getUrl() { return url; }
    /**
     * Method sets receiver's URL address.
     *
     * @param url Receiver's URL address
     */
    public void setUrl(String url) { this.url = url; }
    /**
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------
