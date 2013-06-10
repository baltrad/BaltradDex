<%-- 
    Document   : Main menu
    Created on : Apr 15, 2013, 8:46:19 AM
    Author     : szewczenko
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ tag import="eu.baltrad.dex.user.model.Role" %>

<jsp:useBean id="securityManager"
             scope="session"
             class="eu.baltrad.dex.auth.manager.SecurityManager">
</jsp:useBean>

<%
    Role role = (Role) securityManager.getSessionRole(session);
    if (role.getName().equals(Role.ADMIN)) {
        request.getSession().setAttribute("sessionRole", 1);
    }
    if (role.getName().equals(Role.OPERATOR)) {
        request.getSession().setAttribute("sessionRole", 2);
    }
    if (role.getName().equals(Role.USER)) {
        request.getSession().setAttribute("sessionRole", 3);
    }
%>

<div class="menu">
    <ul class="topmenu">    
        <div class="separator">
            <div class="status"></div>
        </div>
        <li>
            <span>
                <a href="status.htm">Status</a>
            </span>
        </li>
        <li>
            <span><a href="messages_live.htm">Messages</a></span>
        </li>
        <li>
            <span><a href="datasources_show.htm">Data sources</a></span>
        </li>
        <li>
            <span><a href="file_browser.htm">Browse files</a></span>
        </li>
        <c:if test="${sessionRole == 1 || sessionRole == 2}">
            <div class="separator">
                <div class="exchange"></div>
            </div>
            <li>
                <span><a href="node_connect.htm">Connect</a></span>
            </li>
            <li>
                <span><a href="subscription_peers.htm">Subscriptions</a></span>
            </li>
            <li>
                <span><a href="registry_show.htm">Delivery registry</a></span>
            </li>
            <div class="separator">
                <div class="processing"></div>
            </div>
            <li>
                <span><a href="adaptors.htm">Adaptors</a></span>
            </li>
            <li>
                <span><a href="anomaly_detectors.htm">Quality controls</a></span>
            </li>
            <li onmouseout="javascript:hide('routes', 'routes_icon');">
                <span onclick="javascript:toggle('routes', 'routes_icon');">
                    <div id="routes_icon" class="expand"></div>
                    Routes
                </span>
                <ul class="submenu" id="routes"
                    onmouseover="javascript:show('routes', 'routes_icon');">
                    <li><a href="routes.htm">Show</a></li>
                    <li><a href="route_create_groovy.htm">Create script</a></li>
                    <li><a href="route_create_composite.htm">Create composite</a></li>
                    <li><a href="route_create_google_map.htm">Create Google map</a></li>
                    <li><a href="route_create_volume.htm">Create volume</a></li>
                    <li><a href="route_create_distribution.htm">Create distribution</a></li>
                    <li><a href="route_create_bdb_trim_count.htm">Create DB trim count</a></li>
                    <li><a href="route_create_bdb_trim_age.htm">Create DB trim age</a></li>
                </ul>
            </li>
            <li>
                <span><a href="schedule.htm">Schedule</a></span>
            </li>
        </c:if>
        <c:if test="${sessionRole == 1}">
            <div class="separator">
                <div class="settings"></div>
            </div>
            <li onmouseout="javascript:hide('radars', 'radars_icon');">
                <span onclick="javascript:toggle('radars', 'radars_icon');">
                    <div id="radars_icon" class="expand"></div>
                    Radars
                </span>
                <ul class="submenu" id="radars"
                    onmouseover="javascript:show('radars', 'radars_icon');">
                    <li><a href="radars_show.htm">Show</a></li>
                    <li><a href="radars_save.htm">Add</a></li>
                    <li><a href="radars_remove.htm">Remove</a></li>
                </ul>
            </li>
            <li onmouseout="javascript:hide('sources', 'sources_icon');">
                <span onclick="javascript:toggle('sources', 'sources_icon');">
                    <div id="sources_icon" class="expand"></div>
                    Data sources
                </span>
                <ul class="submenu" id="sources"
                    onmouseover="javascript:show('sources', 'sources_icon');">
                    <li><a href="datasources_save.htm">Add</a></li>
                    <li><a href="datasources_edit.htm">Edit</a></li>
                    <li><a href="datasources_remove.htm">Remove</a></li>
                </ul>
            </li>
            <li onmouseout="javascript:hide('subscriptions', 'subscriptions_icon');">
                <span onclick="javascript:toggle('subscriptions', 'subscriptions_icon');">
                    <div id="subscriptions_icon" class="expand"></div>
                    Subscriptions
                </span>
                <ul class="submenu" id="subscriptions"
                    onmouseover="javascript:show('subscriptions', 'subscriptions_icon');">
                    <li><a href="subscription_remove_downloads.htm">Remove downloads</a></li>
                    <li><a href="subscription_remove_uploads.htm">Remove uploads</a></li>
                </ul>
            </li>
            <li onmouseout="javascript:hide('registry', 'registry_icon');">
                <span onclick="javascript:toggle('registry', 'registry_icon');">
                    <div id="registry_icon" class="expand"></div>
                    Delivery registry
                </span>
                <ul class="submenu" id="registry"
                    onmouseover="javascript:show('registry', 'registry_icon');">
                    <li><a href="registry_configure.htm">Configure</a></li>
                    <li><a href="registry_delete.htm">Delete</a></li>
                </ul>
            </li>
            <li onmouseout="javascript:hide('conf_messages', 'conf_messages_icon');">
                <span onclick="javascript:toggle('conf_messages', 'conf_messages_icon');">
                    <div id="conf_messages_icon" class="expand"></div>
                    Messages
                </span>
                <ul class="submenu" id="conf_messages"
                    onmouseover="javascript:show('conf_messages', 'conf_messages_icon');">
                    <li><a href="messages_configure.htm">Configure</a></li>
                    <li><a href="messages_delete.htm">Delete</a></li>
                </ul>
            </li>
            <li onmouseout="javascript:hide('users', 'users_icon');">
                <span onclick="javascript:toggle('users', 'users_icon');">
                    <div id="users_icon" class="expand"></div>
                    User accounts
                </span>
                <ul class="submenu" id="users"
                    onmouseover="javascript:show('users', 'users_icon');">
                    <li><a href="user_save.htm">Add user</a></li>
                    <li><a href="user_edit.htm">Edit user</a></li>
                    <li><a href="user_remove.htm">Remove user</a></li>
                </ul>
            </li>
            <li>
                <span><a href="keystore.htm">Keystore</a></span>
            </li>
            <li>
                <span><a href="node_settings.htm">Node settings</a></span>
            </li>  
            <li>
                <span><a href="supervisor_settings.htm">Supervisor</a></span>
            </li>
        </c:if>
        <c:if test="${sessionRole == 2 || sessionRole == 3}">
            <div class="separator">
                <div class="settings"></div>
            </div>
            <li>
                <span><a href="user_settings.htm">Password</a></span>
            </li>
        </c:if>
    </ul>
</div>
		
	