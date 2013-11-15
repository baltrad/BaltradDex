<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : Node status page
Created on : Apr 22, 2013, 10:43 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Status">
    <jsp:body>
        <div class="status-info">
            <div class="table">	
                <div class="header">
                    <div class="row">System information</div>
                </div>
                <div class="body">
                    <div class="row">
                        <div class="leftcol">
                            Server name:
                        </div>
                        <div class="rightcol">
                            <c:out value="${server_name}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Current time:
                        </div>
                        <div class="rightcol">
                            <c:out value="${current_time}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Software version:
                        </div>
                        <div class="rightcol">
                            <c:out value="${software_version}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Operator:
                        </div>
                        <div class="rightcol">
                            <c:out value="${operator_name}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Current user:
                        </div>
                        <div class="rightcol">
                            <c:out value="${current_user}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Active downloads:
                        </div>
                        <div class="rightcol">
                            <c:out value="${active_downloads}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Active uploads:
                        </div>
                        <div class="rightcol">
                            <c:out value="${active_uploads}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            File entries in the DB:
                        </div>
                        <div class="rightcol">
                            <c:out value="${db_file_entries}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Entries in system log:
                        </div>
                        <div class="rightcol">
                            <c:out value="${log_entries}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Entries in delivery registry:
                        </div>
                        <div class="rightcol">
                            <c:out value="${delivery_entries}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Disk space available:
                        </div>
                        <div class="rightcol">
                            <c:out value="${disk_space}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Last configuration saved on:
                        </div>
                        <div class="rightcol">
                            <c:out value="${last_config_saved}"></c:out>
                        </div>
                    </div>
                </div>
            </div>
        </div>            
        <div class="status-exchange">
            <div class="table">
                <div class="header">
                    <div class="row">Data exchange</div>
                </div>
                <c:choose>
                    <c:when test="${active_downloads == 0 
                                    && active_uploads == 0}">
                        <div class="header-text">
                            No active transfers found. Use 
                            <a href="node_connect.htm">node 
                            connection</a> functionality to connect to 
                            peer nodes and subscribe selected data 
                            sources.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            Click node name to access file transfer 
                            information. 
                        </div>
                        <c:forEach var="peer" items="${nodes}">
                            <form method="POST">
                                <ul class="exchange-menu">
                                    <li>
                                        <c:set var="show_transfers" value="${peers_status[peer]}"/>
                                        <c:choose>
                                            <c:when test="${show_transfers == true}">
                                                <div class="collapse-node"></div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="expand-node"></div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="node-name">
                                            <input name="peer_name" type="submit" value="${peer}">
                                            <ul class="exchange-submenu">
                                                <c:if test="${show_transfers == true}">
                                                    <c:if test="${fn:length(peers_downloads[peer]) gt 0}">
                                                        <ul class="exchange-submenu">
                                                            <li>
                                                                <div id="downloads-icon" 
                                                                     class="expand-downloads-icon">
                                                                </div>
                                                                <div id="toggle-download" 
                                                                     class="transfer-name">
                                                                    Downloads
                                                                </div>
                                                                <ul class="exchange-subsubmenu" 
                                                                    id="list-downloads">
                                                                    <div class="header">
                                                                        <div class="data-source-name">
                                                                            Data source name
                                                                        </div>
                                                                        <div class="subscription-start">
                                                                            Subscription started
                                                                        </div>
                                                                        <div class="subscription-status">
                                                                            Subscription status
                                                                        </div>
                                                                    </div>
                                                                    <c:forEach var="download" items="${peers_downloads[peer]}">
                                                                        <div class="data-source">
                                                                            <div class="data-source-name"
                                                                                 title="${download.dataSource}">
                                                                                <c:out value="${download.dataSource}"/>
                                                                            </div>
                                                                            <div class="subscription-start">
                                                                                <fmt:formatDate value="${download.date}" 
                                                                                                pattern="yyyy/MM/dd HH:mm:ss"/>
                                                                            </div>
                                                                            <div class="subscription-status">
                                                                                <c:out value="${download.active == true 
                                                                                                ? 'Active' : 'Off'}"/>
                                                                            </div>
                                                                        </div>
                                                                    </c:forEach>       
                                                                </ul>
                                                            </li>    
                                                        </ul>    
                                                    </c:if>
                                                    <c:if test="${fn:length(peers_uploads[peer]) gt 0}">
                                                        <ul class="exchange-submenu">
                                                            <li>
                                                                <div id="uploads-icon" 
                                                                     class="expand-uploads-icon">
                                                                </div>
                                                                <div id="toggle-upload" 
                                                                     class="transfer-name">
                                                                    Uploads
                                                                </div>
                                                                <ul class="exchange-subsubmenu"
                                                                    id="list-uploads">
                                                                    <div class="header">
                                                                        <div class="data-source-name">
                                                                            Data source name
                                                                        </div>
                                                                        <div class="subscription-start">
                                                                            Subscription started
                                                                        </div>
                                                                        <div class="files-sent">
                                                                            Files uploaded
                                                                        </div>
                                                                        <div class="failures">
                                                                            Upload failures
                                                                        </div>
                                                                    </div>
                                                                    <c:forEach var="upload" items="${peers_uploads[peer]}">
                                                                        <div class="data-source">
                                                                            <div class="data-source-name"
                                                                                 title="${upload.dataSource}">
                                                                                <c:out value="${upload.dataSource}"/>
                                                                            </div>
                                                                            <div class="subscription-start">
                                                                                <fmt:formatDate value="${upload.date}" 
                                                                                                pattern="yyyy/MM/dd HH:mm:ss"/>
                                                                            </div>
                                                                            <div class="files-sent">
                                                                                <c:out value="${upload.filesSent}"/>
                                                                            </div>
                                                                            <div class="failures">
                                                                                <c:out value="${upload.failures}"/>
                                                                            </div> 
                                                                        </div> 
                                                                    </c:forEach>
                                                                </ul>
                                                            </li>    
                                                        </ul>
                                                    </c:if>
                                                </c:if>
                                            </ul>
                                        </div>     
                                    </li>
                                </ul>
                            </form>      
                        </c:forEach>                                            
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </jsp:body>
</t:generic_page>
