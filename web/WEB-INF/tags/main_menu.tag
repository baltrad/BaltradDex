<%-- 
    Document   : Main menu
    Created on : Apr 15, 2013, 8:46:19 AM
    Author     : szewczenko
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="includes/js/main_menu.js"></script>

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
                <span>
                    <a href="anomaly_detectors.htm">Quality controls</a>
                </span>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Routes
                </span>
                <ul class="submenu">
                    <li>
                        <a href="routes.htm">Show</a>
                    </li>
                    <li>
                        <a href="route_create_groovy.htm">Create script</a>
                    </li>
                    <li>
                        <a href="route_create_composite.htm">
                            Create composite</a>
                    </li>
                    <li>
                        <a href="route_create_site2d.htm">Create Site2D</a>
                    </li>
                    <li>
                        <a href="route_create_google_map.htm">
                            Create Google map</a>
                    </li>
                    <li>
                        <a href="route_create_volume.htm">Create volume</a>
                    </li>
                    <li>
                        <a href="route_create_distribution.htm">
                            Create distribution</a>
                    </li>
                    <li>
                        <a href="route_create_acrr.htm">Create ACRR</a>
                    </li>
                    <li>
                        <a href="route_create_gra.htm">Create GRA</a>
                    </li>
                    <li>
                        <a href="route_create_wrwp.htm">Create WRWP</a>
                    </li>
                    <li>
                        <a href="route_create_scansun.htm">Create ScanSun</a>
                    </li>
                    <li>
                        <a href="route_create_bdb_trim_count.htm">
                            Create DB trim count</a>
                    </li>
                    <li>
                        <a href="route_create_bdb_trim_age.htm">
                            Create DB trim age</a>
                    </li>
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
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Radars
                </span>
                <ul class="submenu">
                    <li><a href="radars_show.htm">Show</a></li>
                    <li><a href="radars_save.htm">Add</a></li>
                    <li><a href="radars_remove.htm">Remove</a></li>
                </ul>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Data sources
                </span>
                <ul class="submenu">
                    <li><a href="datasources_save.htm">Add</a></li>
                    <li><a href="datasources_edit.htm">Edit</a></li>
                    <li><a href="datasources_remove.htm">Remove</a></li>
                </ul>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Subscriptions
                </span>
                <ul class="submenu">
                    <li>
                        <a href="subscription_remove_downloads_peers.htm">
                            Remove downloads</a>
                    </li>
                    <li>
                        <a href="subscription_remove_uploads.htm">
                            Remove uploads</a>
                    </li>
                </ul>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Delivery registry
                </span>
                <ul class="submenu">
                    <li><a href="registry_configure.htm">Configure</a></li>
                    <li><a href="registry_delete.htm">Delete</a></li>
                </ul>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    Messages
                </span>
                <ul class="submenu">
                    <li><a href="messages_configure.htm">Configure</a></li>
                    <li><a href="messages_delete.htm">Delete</a></li>
                </ul>
            </li>
            <li>
                <span>
                    <div class="expand" id="menu-icon"></div>
                    User accounts
                </span>
                <ul class="submenu">
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
	