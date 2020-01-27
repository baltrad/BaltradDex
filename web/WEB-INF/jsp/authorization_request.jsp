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

<t:generic_page pageTitle="Authorization request">
    <jsp:body>
        <div class="authorization_request">
            <div class="table">
                <div class="header">
                    <div class="row">Authorization request</div>
                </div>
                <div class="header-text">
                    Approve or deny authorization request.  
                </div>
                <form method="POST" name="authorization_request" action="authorization_request.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <input type="hidden" name="uuid" title="" value="<c:out value="${request.requestUUID}" />"
                        <div class="row">
                            <div class="leftcol">
                                Node name:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="readonlyNodeName" title="Node name"
                                       value="<c:out value="${request.nodeName}" />" readonly />                            
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Email:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="nodeEmail" title="Email"
                                       value="<c:out value="${request.nodeEmail}" />" />
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Node address:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="nodeAddress" title="Node address"
                                       value="<c:out value="${request.nodeAddress}" />" />
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Remote host:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="remoteHost" title="Remote host"
                                       value="<c:out value="${request.remoteHost}" />" readonly />
                            </div>
                        </div>          
                        <div class="row">
                            <div class="leftcol">
                                Received time:
                            </div>
                            <div class="rightcol">
                                <fmt:formatDate value="${request.receivedAt}" pattern="yyyy-MM-dd HH:mm:ss" var="receivedAt"/>
                                <input type="text" name="receivedAt" title="Received request at"
                                       value="<c:out value="${receivedAt}" />" readonly />
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Message:</div>
                            <div class="rightcol">
                                <textarea name="message" title="Message">${request.message}</textarea>
                            </div>
                        </div> 
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                  <c:choose>
                                    <c:when test="${request.outgoing == true}">
                                      <input class="button" name="submitButton" type="submit" value="Resend" />
                                      <input class="button" name="submitButton" type="submit" value="Delete" />
                                    </c:when>
                                    <c:otherwise>
                                      <input class="button" name="submitButton" type="submit" value="Accept" />
                                      <input class="button" name="submitButton" type="submit" value="Deny" />
                                      <input class="button" name="submitButton" type="submit" value="Delete" />
                                    </c:otherwise>
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
