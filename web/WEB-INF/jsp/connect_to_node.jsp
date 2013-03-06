<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
Document   : Connect to peer node by selecting address from the drop-down list
             or entering address in the text box
Created on : Sep 24, 2010, 2:46 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Connect to peer node" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                <img src="includes/images/icons/connection.png" alt="">
                Connect to peer node
            </div>
            <div class="blttext">
                <p>
                    Enter node's URL address and click <i>Send key</i> to send 
                    your public key to the peer node.
                </p>
                <p>
                    Select peer node from the drop-down list or enter URL 
                    address and click <i>Connect</i> to access data sources 
                    available at the peer node.
                </p>    
            </div>
            <div class="table">
                <%@include file="/WEB-INF/jsp/messages.jsp"%>
                <div class="connect">
                    <form method="post" action="node_connected.htm">
                        <div class="bltseparator">Select peer node</div>
                        <br>
                        <div class="rightcol">
                            <div class="row">
                                <div class="selectconnection">
                                    <select name="node_select"
                                            title="Select peer node to connect to">
                                        <option selected/>
                                        <c:forEach items="${nodes}" var="node">
                                            <option value="${node}"> 
                                                <c:out value="${node}"/>
                                            </option>
                                        </c:forEach>
                                    </select>             
                                    <div class="hint">
                                        Select peer node to connect to
                                    </div>       
                                </div>
                            </div>
                        </div>
                        <div class="bltseparator">Peer node's URL address</div>
                        <br>
                        <div class="rightcol">
                            <div class="row">
                                <div class="nodeaddress">
                                    <input type="text" name="url_input" 
                                           title="Enter peer node's URL address">
                                    <div class="hint">
                                       Enter peer node's URL address, e.g. 
                                       http://baltrad.eu:8084
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="tablefooter">
                           <div class="buttons">
                               <button class="rounded" type="submit" 
                                       name="send_key">
                                   <span>Send key</span>
                               </button>
                               <button class="rounded" type="submit" 
                                       name="connect">
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