<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
----------------------------------------------------------------------------------------------------
Document   : Home page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.util.InitAppUtil"%>
<%@page import="eu.baltrad.dex.user.model.User" %>

<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>

<%
    InitAppUtil init = InitAppUtil.getInstance();
    User user = (User)securityManager.getUser(request);
    HttpSession sess = request.getSession();
    String userName = user.getName();
    sess.setAttribute("userName", userName);
    sess.setAttribute("nodeName", init.getConfiguration().getNodeName());
    sess.setAttribute("operator", init.getConfiguration().getOrganization());
    sess.setAttribute("nodeVersion", init.getConfiguration().getVersion());
    sess.setAttribute("nodeType", init.getConfiguration().getNodeType());
    sess.setAttribute("address", init.getConfiguration().getAddress());
    sess.setAttribute("timeZone", init.getConfiguration().getTimeZone());
    sess.setAttribute("adminEmail", init.getConfiguration().getEmail());
%>

<t:page_tabbed pageTitle="Home" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            Welcome to Baltrad Radar Data Exchange and Processing System!
        </div>
        <div class="blttext">
            <p>Baltrad is running on ${nodeName} operated by ${operator}.</p>
            <p>You have signed in as user ${userName}.</p>
            <p>Following is the information about local Baltrad node.</p>
        </div>
        <div class="bltseparator"></div>
        <div class="table">
            <div class="leftcol">
                <div class="row">Operator:</div>
                <div class="row">Node location:</div>
                <div class="row">Node name:</div>
                <div class="row">Software version:</div>
                <div class="row">Node type:</div>
                <div class="row">Local time zone:</div>
                <div class="row">Admin e-mail:</div>
            </div>
            <div class="rightcol">
                <div class="row">${operator}</div>
                <div class="row">${address}</div>
                <div class="row">${nodeName}</div>
                <div class="row">${nodeVersion}</div>
                <div class="row">${nodeType}</div>
                <div class="row">${timeZone}</div>
                <div class="row">${adminEmail}</div>
            </div>
        </div>
    </div>
  </jsp:body>
</t:page_tabbed>
