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

<%@page import="eu.baltrad.dex.config.manager.impl.ConfigurationManager"%> 
<%@page import="eu.baltrad.dex.user.model.User"%>

<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.auth.manager.SecurityManager">
</jsp:useBean>
<jsp:useBean id="configManager" scope="request"
             class="eu.baltrad.dex.config.manager.impl.ConfigurationManager">
</jsp:useBean>

<%
    User user = (User) securityManager.getSessionUser(session);
    HttpSession sess = request.getSession();
    String userName = user.getName();
    sess.setAttribute("userName", userName);
    sess.setAttribute("nodeName", configManager.getAppConf().getNodeName());
    sess.setAttribute("nodeVersion", configManager.getAppConf().getVersion());
    sess.setAttribute("nodeType", configManager.getAppConf().getNodeType());
    sess.setAttribute("orgName", configManager.getAppConf().getOrgName());
    sess.setAttribute("orgUnit", configManager.getAppConf().getOrgUnit());
    sess.setAttribute("locality", configManager.getAppConf().getLocality());
    sess.setAttribute("state", configManager.getAppConf().getState());
    sess.setAttribute("countryCode", configManager.getAppConf().getCountryCode());
    sess.setAttribute("timeZone", configManager.getAppConf().getTimeZone());
    sess.setAttribute("adminEmail", configManager.getAppConf().getAdminEmail());
%>

<t:page_tabbed pageTitle="Home" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <t:welcome_page/>
    </div>
  </jsp:body>
</t:page_tabbed>
