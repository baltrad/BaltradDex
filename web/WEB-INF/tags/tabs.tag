<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="activeTab" required="true" description="name of the active tab"%>

<%@ tag import="eu.baltrad.dex.user.model.Role" %>

<jsp:useBean id="securityManager"
             scope="session"
             class="eu.baltrad.dex.auth.manager.SecurityManager">
</jsp:useBean>

<%
    Role sessionRole = (Role) securityManager.getSessionRole(session);
    if (sessionRole.getName().equals(Role.ADMIN)) {
        request.getSession().setAttribute("userRole", 0);
    }
    if (sessionRole.getName().equals(Role.OPERATOR)) {
        request.getSession().setAttribute("userRole", 1);
    }
    if (sessionRole.getName().equals(Role.USER)) {
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