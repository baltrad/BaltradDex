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
Document   : Connect with remote host
Author     : anders
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remote host connection">
    <jsp:body>
        <t:message_box errorHeader="Problems encountered." errorBody="${emessage}"/>
        <div class="connect_with_remote_host">
            <div class="table">
                <div class="header">
                    <div class="row">Remote host connection</div>
                </div>
                <div class="header-text">
                    Request key approval from remote host administrator. Enter a URL to the remote host, a message and
                    press "Connect". For example http://127.0.0.1/BaltradDex
                </div>
                <form  method="POST" action="connect_with_remote_host.htm" commandName="connect_with_remote_host">
                    <div class="body">
                        <div class="row">
                            <div class="leftcol">
                                Connection URL:
                            </div>
                            <div class="rightcol">
                                <input type="text" name="connectionURL" title="Connection url"
                                       value="<c:out value="${connectionURL}" />" />                            
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Message:</div>
                            <div class="rightcol">
                                <textarea name="message" title="Message">${message}</textarea>
                            </div>
                        </div>      
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <c:choose>
                                        <c:when test="${canRemove == true}">
                                            <input class="button" name="submitButton" type="submit" value="Remove"/>
                                        </c:when>
                                    </c:choose>
                                    <input class="button" name="submitButton" type="submit" value="Connect"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div> 
    </jsp:body>
</t:generic_page>
