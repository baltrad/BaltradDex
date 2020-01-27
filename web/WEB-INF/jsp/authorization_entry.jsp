<%------------------------------------------------------------------------------
Copyright (C) 2019- SMHI

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
Document   : Authorization key
Author     : anders
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Authorization entry">
    <jsp:body>
        <div class="authorization_entry">
            <div class="table">
                <div class="header">
                    <div class="row">Authorization entry</div>
                </div>
                <div class="header-text">
                    Update an authorization entry.  
                </div>
                <form method="POST" commandName="update_authorization" action="authorization_entry.htm">
                    <t:message_box msgHeader="Success."
                                   msgBody="${success_message}"
                                   errorHeader="Problems encountered."
                                   errorBody="${error_message}"/>
                    <div class="body">
                        <input type="hidden" name="uuid" value="<c:out value="${authorization.connectionUUID}" />" />
                        <div class="row">
                            <div class="leftcol">
                                Node name:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="readonlyNodeName" title="Node name"
                                       value="<c:out value="${authorization.nodeName}" />" readonly />                            
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Email:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="nodeEmail" title="Email"
                                       value="<c:out value="${authorization.nodeEmail}" />" />
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Node address:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="nodeAddress" title="Node address"
                                       value="<c:out value="${authorization.nodeAddress}" />" />
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Last update:
                            </div>
                            <div class="rightcol">
                                <fmt:formatDate value="${authorization.lastUpdated}" pattern="yyyy-MM-dd HH:mm:ss" var="lastUpdated"/>
                                <input type="text" name="lastUpdated" title="Last updated"
                                       value="<c:out value="${lastUpdated}" />" readonly />
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Authorized:
                            </div>
                            <div class="rightcol">
                               <c:set var="authorized" value = ""/>
                               <c:set var="readonlyflag" value =""/>
                               <c:choose>
                                   <c:when test="${authorization.authorized}">
                                     <c:set var="authorized" value = "checked"/>
                                   </c:when>
                               </c:choose>
                               <c:choose>
                                   <c:when test="${authorization.local == true}">
                                     <c:set var="readonlyflag" value="disabled" />
                                   </c:when>
                               </c:choose>
                               <input type="checkbox" name="authorized" <c:out value="${authorized}" /> <c:out value="${readonlyflag}" />/> 
                            </div>                            
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Injector:
                            </div>
                            <div class="rightcol">
                               <c:set var="injector" value = ""/>
                               <c:set var="readonlyflag" value =""/>
                               <c:choose>
                                   <c:when test="${authorization.injector}">
                                     <c:set var="injector" value = "checked"/>
                                   </c:when>
                               </c:choose>
                               <c:choose>
                                   <c:when test="${authorization.local == true}">
                                     <c:set var="readonlyflag" value="disabled" />
                                   </c:when>
                               </c:choose>
                               <input type="checkbox" name="injector" <c:out value="${injector}" />  <c:out value="${readonlyflag}" />/> 
                            </div>                            
                        </div>                        
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" name="submitButton" type="submit" value="Back"/>
                                    <input class="button" name="submitButton" type="submit" value="Connect"/>
                                    <input class="button" name="submitButton" type="submit" value="Save"/>
                                    <c:choose>
                                      <c:when test="${authorization.local == false}">
                                        <input class="button" name="submitButton" type="submit" value="Delete"  />
                                      </c:when>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div> 
    </jsp:body>
</t:generic_page>
