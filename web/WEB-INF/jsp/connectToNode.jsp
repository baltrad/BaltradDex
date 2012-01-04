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
Document   : Page allowing to connect to remote node
Created on : Sep 24, 2010, 2:46 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Connect" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                <img src="includes/images/icons/connection.png" alt="">
                Connect to remote node
            </div>
            <div class="blttext">
                Select existing node connection or define new connection in order
                to access data sources available at the remote node.
            </div>
            <div class="table">
                <div class="connect">
                    <form method="post" action="dsConnect.htm">
                        <div class="bltseparator">Select connection</div>
                        <br>
                        <div class="rightcol">
                            <div class="row">
                                <div class="selectconnection">
                                    <select name="selectAddress"
                                            title="Select connection from the list">
                                        <c:forEach items="${connections}"
                                                   var="conn">
                                            <option value="${conn.nodeName}">
                                                <c:out value="${conn.nodeName}"/>
                                            </option>
                                        </c:forEach>
                                    </select>    
                                    <div class="hint">
                                        Select existing connection
                                    </div>
                                    <c:if test="${not empty selectAddressError}">
                                        <div class="error">
                                            <c:out value="${selectAddressError}"/>
                                        </div>
                                    </c:if>          
                                </div>
                            </div>
                        </div>
                        <div class="bltseparator">Define new connection</div>
                        <br>
                        <div class="rightcol">
                            <div class="row">
                                <div class="nodeaddress">
                                    <input type="text" name="enterAddress" 
                                           title="Enter node address">
                                    <div class="hint">
                                       Enter node's address, e.g. 
                                       http://baltrad.eu:8084/BaltradDex/dispatch.htm
                                    </div>
                                    <c:if test="${not empty enterAddressError}">
                                        <div class="error">
                                            <c:out value="${enterAddressError}"/>
                                        </div>
                                    </c:if> 
                                </div>
                            </div>
                        </div>
                        <div class="tablefooter">
                           <div class="buttons">
                               <button class="rounded" type="submit">
                                   <span>Connect</span>
                               </button>
                           </div>
                        </div>
                    </form>
                </div>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>