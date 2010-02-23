<%--
    Document   : Transmitter module control page
    Created on : May 22, 2009, 11:39:59 AM
    Author     : szewczenko
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css">
    <title>Baltrad Data Exchange System</title>
</head>

<body>       
    <jsp:useBean id="applicationSecurityManager" scope="session"
            class="pl.imgw.baltrad.dex.util.ApplicationSecurityManager"/>

    <jsp:useBean id="logManager" scope="session" class="pl.imgw.baltrad.dex.model.LogManager"/>

    <%@page import="java.util.Date"%>

    <%
        applicationSecurityManager.setTransmitterOff();
        logManager.addLogEntry( new Date(), "INFO", "Server process stopped" );
    %>

    <jsp:forward page="admin.jsp"/>
</body>




