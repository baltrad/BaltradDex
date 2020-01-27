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
Document   : Key authorization list
Author     : anders
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Key authorization list">
    <jsp:body>
        <t:message_box errorHeader="Problems encountered." errorBody="${emessage}"/>
    
        <c:choose>
        <c:when test="${not empty requests}">
        <div class="authorization_list">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Authorization requests waiting for answer.
                    </div>
                </div>
                        <div class="header-text">
                            Select to approve/deny an incomming request or resend/cancel an outgoing request.  
                        </div>
                        <form name="requests" action="authorization_keys.htm" >
                            <div class="body">
                                <div class="header-row">
                                    <div class="name">Node name</div>
                                    <div class="email">Email</div>
                                    <div class="type">Type</div>
                                </div>
                                <c:forEach var="request" items="${requests}">
                                   <c:choose>
                                     <c:when test="${request.outgoing == true}">
                                       <c:url var="url" value="connect_with_remote_host.htm">
                                         <c:param name="connectionURL" value="${request.remoteAddress}" />
                                         <c:param name="message" value="${request.message}" />
                                       </c:url>
                                     </c:when>
                                     <c:otherwise>
                                       <c:url var="url" value="authorization_request.htm">
                                         <c:param name="uuid" value="${request.requestUUID}" />
                                       </c:url>
                                     </c:otherwise>
                                   </c:choose>
                                    <div class="row">
                                        <div class="name">
                                          <a href="${url}">
                                            <c:choose>
                                              <c:when test="${request.nodeName == localNodeName }">
                                                <c:out value="${fn:substring(request.nodeAddress, 0, 15)}..." />
                                              </c:when>
                                              <c:otherwise>
                                                <c:out value="${request.nodeName}" />
                                              </c:otherwise>
                                            </c:choose>
                                          </a>                                          
                                        </div>
                                        <div class="email">
                                            <c:out value="${request.nodeEmail}" />
                                        </div>
                                        <div class="type">
                                          <a href="${url}">
                                            <c:choose>
                                              <c:when test="${request.outgoing == true}">
                                                Outgoing
                                              </c:when>
                                              <c:otherwise>
                                                Incomming
                                              </c:otherwise>
                                            </c:choose>
                                          </a>                                          
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </form>                                                     
            </div>
        </div>
        </c:when>
        </c:choose>                        
        <div class="authorization_list">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Authorization list
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty authorizations}">
                        <div class="header-text">
                            Click <i>Connect</i> button in order to establish a connection to a remote host.
                            Click on any of the autorization keys to get to edit screen.
                        </div>
                        <form name="authorization_list" action="authorization_list.htm" >
                            <div class="body">
                                <div class="header-row">
                                    <div class="name">Node name</div>
                                    <div class="email">Email</div>
                                    <div class="authorized">Authorized</div>
                                    <div class="injector">Injector</div>
                                    <div class="local">Local</div>
                                </div>
                                <c:forEach var="auth" items="${authorizations}">   
                                    <div class="row">
                                        <div class="name">
                                          <c:url var="url" value="authorization_entry.htm">
                                             <c:param name="uuid" value="${auth.connectionUUID}" />
                                          </c:url>
                                          <a href="${url}">
                                            <c:out value="${auth.nodeName}"/>
                                          </a>                                    
                                        </div>
                                        <div class="email">
                                            <c:out value="${auth.nodeEmail}"/>
                                        </div>
                                        <div class="authorized">
                                           <c:choose>
                                             <c:when test="${auth.authorized == false}">
                                               <c:url var="url" value="authorization_entry.htm">
                                                 <c:param name="uuid" value="${auth.connectionUUID}" />
                                               </c:url>
                                               <a href="${url}">
                                                 <img src="includes/images/stop.png" alt="Revoked" />
                                               </a>
                                             </c:when>
                                             <c:otherwise>
                                               <c:url var="url" value="authorization_entry.htm">
                                                 <c:param name="uuid" value="${auth.connectionUUID}" />
                                               </c:url>   
                                               <a href="${url}">
                                                 <img src="includes/images/log-info.png" alt="Granted" />
                                               </a>                                                
                                             </c:otherwise>
                                           </c:choose> 
                                         </div>
                                        <div class="injector">
                                           <c:choose>
                                             <c:when test="${auth.injector == false}">
                                               <c:url var="url" value="authorization_entry.htm">
                                                 <c:param name="uuid" value="${auth.connectionUUID}" />
                                               </c:url>
                                               <a href="${url}">
                                                 <img src="includes/images/stop.png" alt="No" />
                                               </a>
                                             </c:when>
                                             <c:otherwise>
                                               <c:url var="url" value="authorization_entry.htm">
                                                 <c:param name="uuid" value="${auth.connectionUUID}" />
                                               </c:url>   
                                               <a href="${url}">
                                                 <img src="includes/images/log-info.png" alt="Yes" />
                                               </a>                                                
                                             </c:otherwise>
                                           </c:choose> 
                                         </div>                                         
                                         <div class="local">
                                           <c:choose>
                                             <c:when test="${auth.local == true}">
                                               Local
                                             </c:when>
                                           </c:choose>
                                         </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="header-text">
                              <p/>&nbsp;<p/>
                              Press "Connect" to get to the connection dialog for establishing a connection with a remote host.
                            </div>
                            <div class="table-footer">
                              <div class="buttons">
                                <div class="button-wrap">
                                  <input class="button" name="submitButton" type="submit" value="Connect" />
                                </div>
                              </div>
                            </div>
                        </form>                                                     
                    </c:when>
                </c:choose>                        
            </div>
        </div>        
    </jsp:body>
</t:generic_page>
