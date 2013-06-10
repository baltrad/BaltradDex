<%-- 
    Document   : Page title bar
    Created on : Apr 17, 2013, 11:06:19 AM
    Author     : szewczenko
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="configurationManager"
             scope="session"
             class="eu.baltrad.dex.config.manager.impl.ConfigurationManager">
</jsp:useBean>

<%
    request.getSession().setAttribute("nodeName", 
            configurationManager.getAppConf().getNodeName());
%>

<div id="titlebar">
    <div id="title">
        <div class="leftcol">
            <c:out value="${nodeName}"/>
        </div>
        <div class="rightcol">
            | ${pageTitle}
        </div>
    </div>
</div>