<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="java.util.List" %>
<%
    // Check if subscription list is not empty
    List chStatus = ( List )request.getAttribute( "user_subscriptions" );
    if( chStatus == null || chStatus.size() <= 0 ) {
        request.getSession().setAttribute( "subs_status", 0 );
    } else {
        request.getSession().setAttribute( "subs_status", 1 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Subscription confirmation</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Subscription confirmation</h1>
                        <br/>
                        <h2>
                            <p>
                            Your subscription is now submitted.
                            Following is your current subscription status.
                            </p>
                        </h2>
                        <c:choose>
                            <c:when test="${subs_status == 1}">
                                <display:table name="user_subscriptions" id="channel"
                                    defaultsort="1" requestURI="submit.htm" cellpadding="0"
                                    cellspacing="2" export="false" class="tableborder">
                                    <display:column sortProperty="id" sortable="true"
                                        title="Channel ID" class="tdcenter">
                                        <fmt:formatNumber value="${channel.id}"
                                            pattern="00" />
                                    </display:column>
                                    <display:column sortable="true" title="Channel name"
                                        sortProperty="name" paramId="name" paramProperty="name"
                                        class="tdcenter" value="${channel.name}">
                                    </display:column>
                                </display:table>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    You have no active subscriptions.
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div id="table-footer">
                            <a href="subscriptions.htm">&#60&#60 Subscription status</a>
                        </div>
                    </div>
                    <div id="left">
                        <script type="text/javascript" src="includes/mainmenu.js"></script>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>