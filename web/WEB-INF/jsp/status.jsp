<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
    String submitOption = request.getParameter( "submitButton" );
    if( submitOption != null ) {
        request.getSession().setAttribute( "selectedOption", 1 );
    } else {
        request.getSession().setAttribute( "selectedOption", 0 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css">
    <title>Baltrad Data Exchange System</title>
</head>

<div id="content">
    <div id="header">
        <img src="includes/images/baltrad_header.png">
    </div>
    <div id="container1">
        <div id="container2">
            <div id="leftcol">
                <a href="welcome.htm">Home</a>
                <br>
                <a href="channels.htm">Data channels</a>
                <a href="subscriptions.htm">Subscriptions</a>
                <a href="log.htm">View logs</a>
                <a href="welcome.htm">Help</a>
                <a href="welcome.htm">Links</a>
                <br>
                <a href="admin.htm">System management</a>
                <br>
                <a href="signout.htm">Logout</a>
                <br>
            </div>
            <div id="rightcol">
                <c:choose>
                    <c:when test="${selectedOption == 1}">
                        <div id="table-info">
                            <div id="message-box">
                                Your subscription is now submitted.
                                Following is your subscription status.
                            </div>
                        </div>
                        <div id="table-content">
                            <display:table name="userSubscriptions" id="subscription"
                                defaultsort="1" requestURI="submit.htm" cellpadding="5"
                                cellspacing="0" export="false" class="tableborder">
                                <display:caption>Subscribed data channels:</display:caption>
                                <display:column sortProperty="dataChannelID" sortable="true"
                                    title="Channel ID" class="tdcenter">
                                    <fmt:formatNumber value="${subscription.dataChannelID}"
                                        pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="Channel name"
                                    sortProperty="name" paramId="name" paramProperty="name"
                                    class="tdcenter" value="${subscription.name}">
                                </display:column>
                            </display:table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div id="message-box">
                            Your subscriptions have been cancelled. Please use "Subscriptions"
                            option to subscribe / unsubscribe to desired data channels.
                        </div>
                    </c:otherwise>
                </c:choose>
                <div id="operator-logo">
                    <img src="includes/images/logo.png">
                </div>
            </div>
        </div>
    </div>
    <div id="footer">
        <div class="leftcol">
            Baltrad DEX v.0.1
        </div>
        <div class="rightcol">
            BALTRAD Project Group &#169 2009
        </div>
    </div>
</div>
</html>






