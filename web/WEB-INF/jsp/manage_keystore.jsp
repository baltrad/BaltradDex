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
Document   : Manage keystore page
Created on : Mar 4, 2013, 12:31 PM
Author     : szewczenko
------------------------------------------------------------------------------%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Manage keystore" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Manage keystore
            </div>
            <div class="blttext">
                <p>
                    Use functionality below to manage keys. Click <i>Access</i>
                    button in order to grant or revoke access for a particular 
                    key.
                </p>
                <p>
                    Click <i>Delete</i> button to permanently delete a given 
                    key from the keystore.  
                </p>    
            </div>
            <c:choose>
                <c:when test="${not empty keys}">
                    <div class="table">
                        <div class="keystore">
                            <div class="tableheader">
                                <div id="cell" class="name">
                                    Key name
                                </div>
                                <div id="cell" class="checksum">
                                    Checksum
                                </div>
                                <div id="cell" class="authorized">
                                    Access
                                </div>
                                <div id="cell" class="delete">
                                    Delete
                                </div>
                            </div>
                            <c:forEach var="key" items="${keys}">
                                <form method="post">
                                    <div class="entry">
                                        <div class="hidden">
                                            <c:out value="${key.id}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <c:out value="${key.name}"/>
                                        </div>
                                        <div id="cell" class="checksum">
                                            <c:out value="${key.checksum}"/>
                                        </div>
                                        <div id="cell" class="authorized">
                                            <c:choose>
                                                <c:when test="${key.name == local_node_name}">
                                                    <button type="button">
                                                        <img src="includes/images/icons/collapse.png"
                                                             alt="Local key" 
                                                             title="Access can't be modified for local key">
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${key.authorized == false}">
                                                            <button type="submit" name="grant" 
                                                                    value="${key.id}">
                                                                <img src="includes/images/icons/stop.png"
                                                                     alt="Revoked" 
                                                                     title="Key access revoked">
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="submit" name="revoke"
                                                                    value="${key.id}">
                                                                <img src="includes/images/icons/success.png"
                                                                     alt="Granted" 
                                                                     title="Key access granted">
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose> 
                                                </c:otherwise>
                                            </c:choose>
                                        </div>  
                                        <div id="cell" class="delete">
                                            <c:choose>
                                                <c:when test="${key.name == local_node_name}">
                                                    <button type="button" class="delete-key">
                                                        <img src="includes/images/icons/collapse.png"
                                                             alt="Local key" 
                                                             title="Local key can't be deleted">
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${delete_key_id == key.id}">
                                                            <button type="submit"
                                                                    name="confirm_delete"
                                                                    class="confirm-delete"
                                                                    value="${key.id}"
                                                                    title="Confirm key deletion">
                                                                Delete
                                                            </button>
                                                            <button type="submit"
                                                                    name="cancel_delete"
                                                                    class="cancel-delete"
                                                                    title="Cancel key deletion">
                                                                Cancel
                                                            </button>        
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="submit" name="delete"
                                                                    class="delete-key"
                                                                    value="${key.id}">
                                                                <img src="includes/images/icons/failure.png"
                                                                     alt="Granted" 
                                                                     title="Delete key permanently">
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>    
                                    </div>
                                </form>
                            </c:forEach>      
                        </div>                            
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        No keys found in the keystore.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:page_tabbed>
