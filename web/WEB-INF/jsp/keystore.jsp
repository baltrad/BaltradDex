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
Document   : Keystore management page
Created on : May 28, 2013, 8:43 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Keystore management">
    <jsp:body>
        <div class="keystore">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Keystore management
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty keys}">
                        <div class="header-text">
                            Click <i>Access</i> button in order to grant or 
                            revoke access for a particular key.
                            Click <i>Delete</i> button to permanently delete 
                            a given key from the keystore.  
                        </div>
                        <form method="POST">
                            <div class="body">
                                <div class="header-row">
                                    <div class="name">Key name</div>
                                    <div class="checksum">Checksum</div>
                                    <div class="authorized">Access</div>
                                    <div class="delete">Delete</div>
                                </div>
                                <c:forEach var="key" items="${keys}">
                                    <div class="row">
                                        <div class="hidden">
                                            <c:out value="${key.id}"/>
                                        </div>
                                        <div class="name">
                                            <c:out value="${key.name}"/>
                                        </div>
                                        <div class="checksum">
                                            <c:out value="${key.checksum}"/>
                                        </div>
                                        <div class="authorized">
                                            <c:choose>
                                                <c:when test="${key.name == local_node_name}">
                                                    <button type="button"
                                                            title="Access can't be modified for local key">
                                                        <img src="includes/images/unavailable.png"
                                                             alt="Local key">
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${key.authorized == false}">
                                                            <button type="submit" name="grant" 
                                                                    value="${key.id}"
                                                                    title="Key access revoked">
                                                                <img src="includes/images/stop.png"
                                                                     alt="Revoked">
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="submit" name="revoke"
                                                                    value="${key.id}"
                                                                    title="Key access granted">
                                                                <img src="includes/images/log-info.png"
                                                                     alt="Granted">
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="delete">
                                            <c:choose>
                                                <c:when test="${key.name == local_node_name}">
                                                    <button type="button"
                                                            class="delete-button"
                                                            title="Local key can't be deleted">
                                                        <img src="includes/images/unavailable.png"
                                                             alt="Local key">
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${delete_key_id == key.id}">
                                                            <button type="submit"
                                                                    name="confirm_delete"
                                                                    value="${key.id}"
                                                                    class="delete-confirm"
                                                                    title="Confirm key deletion">
                                                                Delete
                                                            </button>
                                                            <button type="submit"
                                                                    name="cancel_delete"
                                                                    class="delete-cancel"
                                                                    title="Cancel key deletion">
                                                                Cancel
                                                            </button>        
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="submit" name="delete"
                                                                    value="${key.id}"
                                                                    class="delete-button"
                                                                    title="Delete key permanently">
                                                                <img src="includes/images/log-error.png"
                                                                     alt="Granted" >
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </fomr>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No keys found in the keystore.
                        </div>
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" type="button" 
                                           value="Home"
                                           onclick="window.location.href='status.htm'"/>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>    
                
            </div>
        </div>
    </jsp:body>
</t:generic_page>
