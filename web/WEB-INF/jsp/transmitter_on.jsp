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

    <jsp:useBean id="transmitterController" scope="session"
            class="pl.imgw.baltrad.dex.controller.TransmitterController"/>
    <jsp:useBean id="transmitter" scope="session"
            class="pl.imgw.baltrad.dex.model.Transmitter"/>
    <jsp:useBean id="dataManager" scope="session"
            class="pl.imgw.baltrad.dex.model.DataManager"/>
    <jsp:useBean id="userManager" scope="session"
            class="pl.imgw.baltrad.dex.model.UserManager"/>
    <jsp:useBean id="dataChannelManager" scope="session"
            class="pl.imgw.baltrad.dex.model.DataChannelManager"/>
    <jsp:useBean id="subscriptionManager" scope="session"
            class="pl.imgw.baltrad.dex.model.SubscriptionManager"/>
    <jsp:useBean id="deliveryRegisterManager" scope="session"
            class="pl.imgw.baltrad.dex.model.DeliveryRegisterManager"/>

    <%
        transmitter.setDataManager( dataManager );
        transmitter.setUserManager( userManager );
        transmitter.setDataChannelManager( dataChannelManager );
        transmitter.setSubscriptionManager( subscriptionManager );
        transmitter.setDeliveryRegisterManager( deliveryRegisterManager );
        transmitter.setTransmitterController( transmitterController );
        transmitterController.setTransmitter( transmitter );
        transmitterController.setTransmitterOn();
    %>

    <jsp:forward page="admin.jsp"/>
</body>





