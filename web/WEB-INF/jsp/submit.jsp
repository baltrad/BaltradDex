<%--
    Document   : Submit subscription page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page import="java.util.List"%>
<%
    // Check if subscription list is not empty
    List submittedSubscriptions = ( List )request.getAttribute( "submitted_subscriptions" );
    if( submittedSubscriptions == null || submittedSubscriptions.size() <= 0 ) {
        request.getSession().setAttribute( "list_size", 0 );
    } else {
        request.getSession().setAttribute( "list_size", 1 );
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
                <script type="text/javascript" src="includes/mainmenu.js"></script>
            </div>
            <div id="rightcol">
                <c:choose>
                    <c:when test="${list_size == 1}">
                        <div id="table-info">
                            Please confirm your subscription by clicking on "Subscribe"
                            button. Data from the selected channels will be sent until
                            subscription is cancelled.
                        </div>
                        <div id="table-content">
                            <form name="submit_subscription_form" action="status.htm">
                            <display:table name="submitted_subscriptions" id="subscription"
                                defaultsort="1" requestURI="submit.htm" cellpadding="5"
                                cellspacing="0" export="false" class="tableborder">
                                <display:caption class="tablecaption">
                                    Data channels selected for subscription
                                </display:caption>
                                <display:column sortProperty="id" sortable="true"
                                    title="Channel ID" class="tdcenter">
                                    <fmt:formatNumber value="${subscription.id}"
                                        pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="Channel name"
                                    sortProperty="name" paramId="name" paramProperty="name"
                                    class="tdcenter" value="${subscription.name}">
                                </display:column>
                            </display:table>
                            <div id="table-footer">
                                <input type="submit" value="Cancel" name="cancel_button"/>
                                <input type="submit" value="Subscribe" name="submit_button"/>
                            </div>
                            </form>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div id="message-box">
                            In order to complete subscription, please select
                            desired data channels from the list.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</div>
</html>





