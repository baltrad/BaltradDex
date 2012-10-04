<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="activeTab" required="true" description="name of the active tab"%>

<%@ tag import="eu.baltrad.dex.user.model.User" %>

<jsp:useBean id="securityManager"
             scope="session"
             class="eu.baltrad.dex.auth.util.SecurityManager">
</jsp:useBean>

<%
    User sessionUser = (User) securityManager.getSessionUser(session);
    if (sessionUser.getRoleName().equals(User.ROLE_ADMIN)) {
        request.getSession().setAttribute("userRole", 0);
    }
    if (sessionUser.getRoleName().equals(User.ROLE_OPERATOR)) {
        request.getSession().setAttribute("userRole", 1);
    }
    if (sessionUser.getRoleName().equals(User.ROLE_USER)) {
        request.getSession().setAttribute("userRole", 2);
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
<c:if test="${userRole == 1 || userRole == 2}">
    <div id="tab" class="${activeTab == 'settings' ? 'active' : ''}">
        <a href="user_settings.htm">Settings</a>
    </div>  
</c:if>