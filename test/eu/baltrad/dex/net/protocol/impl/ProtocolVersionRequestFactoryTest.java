package eu.baltrad.dex.net.protocol.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol20;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol21;
import eu.baltrad.dex.user.model.User;

public class ProtocolVersionRequestFactoryTest extends EasyMockSupport {
  interface Methods {
    HttpPost createHttpPostBase(String request, String nodename, String contentType);
    StringEntity createStringEntity(String str, String encoding);
    ByteArrayEntity createByteArrayEntity(byte[] arr);
  };
  
  ProtocolVersionRequestFactory classUnderTest = null;
  Methods methods = null;
  JsonProtocol protocol = null;
      
  @Before
  public void setUp() throws Exception {
    methods = createMock(Methods.class);
    classUnderTest = new ProtocolVersionRequestFactory(URI.create("http://127.0.0.1:8080")) {
      protected HttpPost createHttpPostBase(String request, String nodename, String contentType) {
        return methods.createHttpPostBase(request, nodename, contentType);
      }
      protected StringEntity createStringEntity(String str, String encoding) {
        return methods.createStringEntity(str, encoding);
      }
      protected ByteArrayEntity createByteArrayEntity(byte[] arr) {
        return methods.createByteArrayEntity(arr);
      }
    };
    protocol = createMock(JsonProtocol.class);
    classUnderTest.protocol = protocol;
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    methods = null;
    protocol = null;
  }
  
  @Test
  public void createDataSourceListingRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    StringEntity entity = new StringEntity("abc");
    expect(methods.createHttpPostBase("datasource_listing.htm", "abc", "application/json")).andReturn(post);
    expect(protocol.userAccountToJson(usr)).andReturn("a json string");
    expect(methods.createStringEntity("a json string", "UTF-8")).andReturn(entity);
    post.setEntity(entity);
    post.addHeader("Content-MD5", Base64.encodeBase64String("a json string".getBytes()));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createDataSourceListingRequest(usr);
    
    verifyAll();
    
    assertSame(post, result);
    
  }
  
  @Test
  public void createStartSubscriptionRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    Set<DataSource> sources = new HashSet<DataSource>();
    StringEntity entity = new StringEntity("abc");
    expect(methods.createHttpPostBase("start_subscription.htm", "abc", "application/json")).andReturn(post);
    expect(protocol.dataSourcesToJson(sources)).andReturn("a json string");
    expect(methods.createStringEntity("a json string", "UTF-8")).andReturn(entity);
    post.setEntity(entity);
    post.addHeader("Content-MD5", Base64.encodeBase64String("a json string".getBytes()));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createStartSubscriptionRequest(usr, sources);
    
    verifyAll();
    
    assertSame(post, result);
  }
  
  @Test
  public void createUpdateSubscriptionRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    List<Subscription> subscriptions = new ArrayList<Subscription>();
    StringEntity entity = new StringEntity("abc");
    expect(methods.createHttpPostBase("update_subscription.htm", "abc", "application/json")).andReturn(post);
    expect(protocol.subscriptionsToJson(subscriptions)).andReturn("a json string");
    expect(methods.createStringEntity("a json string", "UTF-8")).andReturn(entity);
    post.setEntity(entity);
    post.addHeader("Content-MD5", Base64.encodeBase64String("a json string".getBytes()));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createUpdateSubscriptionRequest(usr, subscriptions);
    
    verifyAll();
    
    assertSame(post, result);
  }
  
  @Test
  public void createPostFileRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    byte[] arr = new byte[0];
    ByteArrayEntity entity = new ByteArrayEntity(arr);
    expect(methods.createHttpPostBase("post_file.htm", "abc", "application/x-hdf5")).andReturn(post);
    expect(methods.createByteArrayEntity(arr)).andReturn(entity);
    post.setEntity(entity);
    expect(post.getURI()).andReturn(URI.create("http://127.0.0.1:8080/BaltradDex/post_file.htm"));
    post.addHeader("Content-MD5", Base64.encodeBase64String("http://127.0.0.1:8080/BaltradDex/post_file.htm".getBytes()));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createPostFileRequest(usr, arr);
    
    verifyAll();
    
    assertSame(post, result);
  }
  
  @Test
  public void createPostMessageRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    StringEntity entity = new StringEntity("abc");
    expect(methods.createHttpPostBase("post_message.htm", "abc", "text/html")).andReturn(post);
    expect(methods.createStringEntity("a message", "UTF-8")).andReturn(entity);
    post.setEntity(entity);
    post.addHeader("Content-MD5", Base64.encodeBase64String("a message".getBytes()));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createPostMessageRequest(usr, "a message");
    
    verifyAll();
    
    assertSame(post, result);
  }
  
  @Test
  public void createPostKeyRequest() throws Exception {
    User usr = new User("abc", "secret");
    HttpPost post = createMock(HttpPost.class);
    byte[] arr = new byte[0];
    ByteArrayEntity entity = new ByteArrayEntity(arr);
    expect(methods.createHttpPostBase("post_key.htm", "abc", "application/zip")).andReturn(post);
    expect(methods.createByteArrayEntity(arr)).andReturn(entity);
    post.setEntity(entity);
    post.addHeader("Content-MD5", DigestUtils.md5Hex(arr));
    
    replayAll();
    
    HttpUriRequest result = classUnderTest.createPostKeyRequest(usr, arr);
    
    verifyAll();
    
    assertSame(post, result);
  }
  
  @Test
  public void createHttpPostBase() throws Exception {
    classUnderTest = new ProtocolVersionRequestFactory(URI.create("http://127.0.0.1:8080"));
    HttpPost post = classUnderTest.createHttpPostBase("start_somthing.htm", "myname", "app/text");
    assertEquals("http://127.0.0.1:8080/BaltradDex/start_somthing.htm", post.getURI().toString());
    assertEquals("myname", post.getHeaders("Node-Name")[0].getValue());
    assertEquals("app/text", post.getHeaders("Content-Type")[0].getValue());
    assertEquals("2.0", post.getHeaders("DEX-Protocol-Version")[0].getValue());
    assertNotNull(post.getHeaders("Date")[0].getValue());
  }
  
  @Test
  public void testDefaultVersion() throws Exception {
    classUnderTest = new ProtocolVersionRequestFactory(URI.create("http://127.0.0.1:8080"));
    assertSame(JsonProtocol20.class, classUnderTest.protocol.getClass());
    assertEquals(ProtocolVersionRequestFactory.PROTOCOL_VERSION_20, classUnderTest.getProtocolVersion());
  }

  @Test
  public void testVersion_20() throws Exception {
    classUnderTest = new ProtocolVersionRequestFactory(URI.create("http://127.0.0.1:8080"), 
        ProtocolVersionRequestFactory.PROTOCOL_VERSION_20);
    assertSame(JsonProtocol20.class, classUnderTest.protocol.getClass());
    assertEquals(ProtocolVersionRequestFactory.PROTOCOL_VERSION_20, classUnderTest.getProtocolVersion());
  }

  @Test
  public void testVersion_21() throws Exception {
    classUnderTest = new ProtocolVersionRequestFactory(URI.create("http://127.0.0.1:8080"), 
        ProtocolVersionRequestFactory.PROTOCOL_VERSION_21);
    assertSame(JsonProtocol21.class, classUnderTest.protocol.getClass());
    assertEquals(ProtocolVersionRequestFactory.PROTOCOL_VERSION_21, classUnderTest.getProtocolVersion());
  }

  @Test
  public void getRequestUri_ServerWithoutSlash() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/resource.htm"),
          classUnderTest.getRequestUri("resource.htm"));   
  }
  
  @Test
  public void getRequestUri_ServerWithSlash() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084/")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/resource.htm"),
          classUnderTest.getRequestUri("resource.htm"));
  }
  
  @Test
  public void getRequestUri_ServerWithContext() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084/BaltradDex")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/resource.htm"),
          classUnderTest.getRequestUri("resource.htm"));   
  }
  
  @Test
  public void getRequestUri_ServerWithContextAndSlash() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084/BaltradDex/")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/resource.htm"),
          classUnderTest.getRequestUri("resource.htm"));
  }
  
  @Test
  public void getRequestUri_MultipleSlashes() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084/BaltradDex/")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/resource.htm"),
          classUnderTest.getRequestUri("//resource.htm//"));
  }
  
  @Test
  public void getRequestUri_ServerWithContextAndPath() {
      classUnderTest = new ProtocolVersionRequestFactory(
          URI.create("http://example.com:8084/BaltradDex/")
      );
      assertEquals(
          URI.create("http://example.com:8084/BaltradDex/res/resource.htm"),
          classUnderTest.getRequestUri("res/resource.htm"));   
  }
}
