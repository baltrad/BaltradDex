<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="activeTab" required="true" description="name of the active tab"%>

<%@ tag import="eu.baltrad.dex.user.model.User" %>
<%@ tag import="eu.baltrad.dex.log.model.MessageLogger" %>
<%@ tag import="org.apache.log4j.Logger" %>

<jsp:useBean id="securityManager"
             scope="session"
             class="eu.baltrad.dex.auth.util.SecurityManager">
</jsp:useBean>
<jsp:useBean id="userManager"
             scope="session"
             class="eu.baltrad.dex.user.model.UserManager">
</jsp:useBean>

<%
    Logger log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    User sessionUser = (User) securityManager.getSessionUser(session);
    User dbUser = userManager.getByName( sessionUser.getName() );
    if( dbUser.getRoleName().equals( User.ROLE_ADMIN ) ) {
        request.getSession().setAttribute( "userRole", 0 );
    }
    if( dbUser.getRoleName().equals( User.ROLE_OPERATOR ) ) {
        request.getSession().setAttribute( "userRole", 1 );
    }
    if( dbUser.getRoleName().equals( User.ROLE_PEER ) ) {
        request.getSession().setAttribute( "userRole", 2 );
    }
    if( dbUser.getRoleName().equals( User.ROLE_USER ) ) {
        request.getSession().setAttribute( "userRole", 3 );
    }    
%>

<div id="tab" class="${activeTab == 'home' ? 'active' : ''}">
    <a href="home.htm">Home</a>
</div>
<c:if test="${userRole == 0 || userRole == 1}">
    <div id="tab" class="${activeTab == 'exchange' ? 'active' : ''}">
        <a href="exchange.htm">Exchange</a>
    </div>
    <div id="tab" class="${activeTab == 'processing' ? 'active' : ''}">
        <a href="processing.htm">Processing</a>
    </div>
</c:if>
<c:if test="${userRole == 0}">
    <div id="tab" class="${activeTab == 'settings' ? 'active' : ''}">
        <a href="settings.htm">Settings</a>
    </div>
</c:if>
