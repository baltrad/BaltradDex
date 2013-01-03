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
Document   : Data processing management page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.util.InitAppUtil"%>
<%@page import="eu.baltrad.dex.user.model.User" %>

<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.auth.manager.SecurityManager">
</jsp:useBean>

<%
    User user = (User) securityManager.getSessionUser(session);
    HttpSession sess = request.getSession();
    String userName = user.getName();
    sess.setAttribute("userName", userName);
    sess.setAttribute("nodeName", InitAppUtil.getConf().getNodeName());
    sess.setAttribute("nodeVersion", InitAppUtil.getConf().getVersion());
    sess.setAttribute("nodeType", InitAppUtil.getConf().getNodeType());
    sess.setAttribute("orgName", InitAppUtil.getConf().getOrgName());
    sess.setAttribute("orgUnit", InitAppUtil.getConf().getOrgUnit());
    sess.setAttribute("locality", InitAppUtil.getConf().getLocality());
    sess.setAttribute("state", InitAppUtil.getConf().getState());
    sess.setAttribute("countryCode", InitAppUtil.getConf().getCountryCode());
    sess.setAttribute("timeZone", InitAppUtil.getConf().getTimeZone());
    sess.setAttribute("adminEmail", InitAppUtil.getConf().getEmail());
%>

<t:page_tabbed pageTitle="Data processing" activeTab="processing">
    <div class="left">
      <t:menu_processing/>
    </div>
    <div class="right">
        <t:welcome_page/>
    </div>
</t:page_tabbed>
