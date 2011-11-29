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
Document   : System settings page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.util.InitAppUtil"%>
<%@page import="eu.baltrad.dex.user.model.User" %>

<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>

<%
    User user = (User)securityManager.getUser(request);
    HttpSession sess = request.getSession();
    String userName = user.getName();
    sess.setAttribute("userName", userName);
    sess.setAttribute("nodeName", InitAppUtil.getConf().getNodeName());
    sess.setAttribute("operator", InitAppUtil.getConf().getOrganization());
    sess.setAttribute("nodeVersion", InitAppUtil.getConf().getVersion());
    sess.setAttribute("nodeType", InitAppUtil.getConf().getNodeType());
    sess.setAttribute("address", InitAppUtil.getConf().getAddress());
    sess.setAttribute("timeZone", InitAppUtil.getConf().getTimeZone());
    sess.setAttribute("adminEmail", InitAppUtil.getConf().getEmail());
%>

<t:page_tabbed pageTitle="System settings" activeTab="settings">
    <div class="left">
        <t:menu_settings/>
    </div>
    <div class="right">
        <t:welcome_page/>
    </div>
</t:page_tabbed>
