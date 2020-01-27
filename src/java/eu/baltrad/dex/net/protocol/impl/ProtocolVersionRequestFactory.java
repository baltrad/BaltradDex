package eu.baltrad.dex.net.protocol.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol20;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol21;
import eu.baltrad.dex.user.model.User;

public class ProtocolVersionRequestFactory implements RequestFactory {
  public final static String PROTOCOL_VERSION_20 = "2.0";
  public final static String PROTOCOL_VERSION_21 = "2.1";
  
  public final static String DEFAULT_PROTOCOL_VERSION = PROTOCOL_VERSION_20;
  
  final static String[] SCHEMES = {"http", "https"};
  
  final static String DEFAULT_BASE_PATH = "BaltradDex";
  final static String SCHEME_SEPARATOR = "://";
  final static String PORT_SEPARATOR = ":";
  final static String PATH_SEPARATOR = "/";
  final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
  
  protected JsonProtocol protocol = null;
  protected URI uri = null;
  protected SimpleDateFormat dateFormat;
  protected String protocolVersion = PROTOCOL_VERSION_20;
  
  private static Logger logger = LogManager.getLogger(ProtocolVersionRequestFactory.class);
  
  /**
   * Constructor
   * @param serverURI the base server uri. E.g http://127.0.0.1:8080
   * @param protocol the json protocol to use
   */
  public ProtocolVersionRequestFactory(URI serverURI, String protocolVersion) {
    UrlValidator validator = new UrlValidator(SCHEMES);
    if (!validator.isValid(serverURI.toString())) {
      throw new IllegalArgumentException("Invalid URI format: " + serverURI);
    }
    logger.info("Protocol version: " + protocolVersion);
    if (!protocolVersion.equals(PROTOCOL_VERSION_20) && !protocolVersion.equals(PROTOCOL_VERSION_21)) {
      throw new IllegalArgumentException("Invalid protocol version. Only valid versions are 2.0 and 2.1");
    }
    this.uri = serverURI;
    this.protocolVersion = protocolVersion;
    this.protocol = getJsonProtocolForVersion(protocolVersion);
    this.dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
  }
  
  /**
   * Constructor. Will use the default protocol version
   * @param serverURI the base server uri. E.g http://127.0.0.1:8080
   */
  public ProtocolVersionRequestFactory(URI serverURI) {
    this(serverURI, DEFAULT_PROTOCOL_VERSION);
  }
  
  /**
   * Return the protocol version used by the request factory
   * @return the protocol version
   */
  public String getProtocolVersion() {
    return this.protocolVersion;
  }
  
  /**
   * @see RequestFactory#createDataSourceListingRequest(User)
   */
  @Override
  public HttpUriRequest createDataSourceListingRequest(User user) {
    HttpPost httpPost = createHttpPostBase("datasource_listing.htm", user.getName(), "application/json");
    String json = protocol.userAccountToJson(user);
    httpPost.setEntity(createStringEntity(json, "UTF-8"));
    httpPost.addHeader("Content-MD5", Base64.encodeBase64String(json.getBytes()));
    return httpPost;
  }

  /**
   * @see RequestFactory#createStartSubscriptionRequest(User, Set)
   */
  @Override
  public HttpUriRequest createStartSubscriptionRequest(User user, Set<DataSource> dataSources) {
      HttpPost httpPost = createHttpPostBase("start_subscription.htm", user.getName(), "application/json");
      String json = protocol.dataSourcesToJson(dataSources);
      httpPost.setEntity(createStringEntity(json, "UTF-8"));
      httpPost.addHeader("Content-MD5", Base64.encodeBase64String(json.getBytes()));
      return httpPost;
  }

  /**
   * @see RequestFactory#createUpdateSubscriptionRequest(User, List)
   */
  @Override
  public HttpUriRequest createUpdateSubscriptionRequest(User user,
      List<Subscription> subscriptions) {
    HttpPost httpPost = createHttpPostBase("update_subscription.htm", user.getName(), "application/json");
    String json = protocol.subscriptionsToJson(subscriptions);
    httpPost.setEntity(createStringEntity(json, "UTF-8"));
    httpPost.addHeader("Content-MD5", Base64.encodeBase64String(json.getBytes()));
    return httpPost;
  }

  /**
   * @see RequestFactory#createPostFileRequest(User, byte[])
   */
  @Override
  public HttpUriRequest createPostFileRequest(User user, byte[] fileContent) {
    HttpPost httpPost = createHttpPostBase("post_file.htm", user.getName(), "application/x-hdf5");
    httpPost.setEntity(createByteArrayEntity(fileContent));
    httpPost.addHeader("Content-MD5", Base64.encodeBase64String(httpPost.getURI().toString().getBytes()));
    return httpPost;
  }

  /**
   * @see RequestFactory#createPostMessageRequest(User, String)
   */
  @Override
  public HttpUriRequest createPostMessageRequest(User user, String message) {
    HttpPost httpPost = createHttpPostBase("post_message.htm", user.getName(), "text/html");
    httpPost.setEntity(createStringEntity(message, "UTF-8"));
    httpPost.addHeader("Content-MD5", Base64.encodeBase64String(message.getBytes()));
    return httpPost;
  }

  /**
   * @see RequestFactory#createPostKeyRequest(User, byte[])
   */
  @Override
  public HttpUriRequest createPostKeyRequest(User user, byte[] keyContent) {
    HttpPost httpPost = createHttpPostBase("post_key.htm", user.getName(), "application/zip");
    httpPost.setEntity(createByteArrayEntity(keyContent));
    httpPost.addHeader("Content-MD5", DigestUtils.md5Hex(keyContent));
    return httpPost;
  }

  /**
   * Creates a http post base object with common information for all dex messages
   * @param request the request uri
   * @param nodename the node name
   * @param contentType the content type
   * @return an http post instance
   */
  protected HttpPost createHttpPostBase(String request, String nodename, String contentType) {
    HttpPost httpPost = new HttpPost(getRequestUri(request));
    httpPost.addHeader("Node-Name", nodename);
    httpPost.addHeader("Content-Type", contentType);  
    httpPost.addHeader("DEX-Protocol-Version", protocolVersion);
    httpPost.addHeader("Date", dateFormat.format(new Date()));
    return httpPost;
  }
  
  /**
   * Creates a string entity for use in a http post
   * @param str the string to be wrapped in the string entity
   * @param encoding the encoding string
   * @return the string entity
   */
  protected StringEntity createStringEntity(String str, String encoding) {
    try {
      return new StringEntity(str, encoding);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unknown encoding");
    }
  }
  
  /**
   * Creates a string entity for use in a http post
   * @param str the string to be wrapped in the string entity
   * @param encoding the encoding string
   * @return the string entity
   */
  protected ByteArrayEntity createByteArrayEntity(byte[] arr) {
    return new ByteArrayEntity(arr);
  }
  
  /**
   * Validates port number.
   * @param port Port number
   * @return True if port is equal or greater than zero
   */
  protected boolean validatePort(int port) {
      if (port > 0) {
          return true;
      }
      return false;
  }
  
  /**
   * Validates resource path.
   * @param path Resource path
   * @return True if a given path is a valid resource path, false otherwise
   */
  protected boolean validatePath(String path) {
      if (path != null) {
          if (!path.trim().isEmpty() && !path.trim().equals("/")) {
              return true;
          }
      }
      return false;
  }
  
  /**
   * Removes leading and trailing slashes from resource path.
   * @param path Resource path
   * @return Resource path without leading and trailing slashes 
   */
  private String removeSlashes(String path) {
      while (path.startsWith("/")) {
          path = path.replaceFirst("/", "");
      }
      while (path.endsWith("/")) {
          path = path.substring(0, path.length() - 1);
      }
      return path;
  }
  
  /**
   * Constructs request URI by appending resource path to server URI.
   * @param path Resource path
   * @return Request URI
   */
  protected URI getRequestUri(String path) {
      URI requestUri = null;
      try {
          requestUri = URI.create(
              uri.getScheme() + SCHEME_SEPARATOR
              + uri.getHost() 
              + (validatePort(uri.getPort()) ? PORT_SEPARATOR 
                  + Integer.toString(uri.getPort()) : "") 
              + (validatePath(uri.getPath()) ? 
                  PATH_SEPARATOR + removeSlashes(uri.getPath()) 
                  : PATH_SEPARATOR + DEFAULT_BASE_PATH) + PATH_SEPARATOR 
                  + removeSlashes(path));
      } catch(IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid request URI: " +
                  requestUri);
      }
      return requestUri;
  }
  
  /**
   * Returns the json protocol that will be used for the specified version
   * @param version the dex protocol version
   * @return the json protocol (defaults to JsonProtocol21) if there is no protocol with specified version
   */
  public static JsonProtocol getJsonProtocolForVersion(String version) {
    if (version == null || version.equals("")) {
      return new JsonProtocol20();
    } else if (version.equals(PROTOCOL_VERSION_20)) {
      return new JsonProtocol20();
    }
    return new JsonProtocol21();
  }
}
