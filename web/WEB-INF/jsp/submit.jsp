<%--
    Document   : Submit subscription page
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
    List chSubmit = ( List )request.getAttribute( "submitted_channels" );
    // Subscription status is not changed
    if( chSubmit == null ) {
        request.getSession().setAttribute( "subs_status", 0 );
    // User submitted empty list
    } else if( chSubmit.size() <= 0 ) {
        request.getSession().setAttribute( "subs_status", 1 );
    // User changed subscription status
    } else {
        request.getSession().setAttribute( "subs_status", 2 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Subscription order</title>
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
                        <h1>Subscription order</h1>
                        <br/>
                        <c:choose>
                            <c:when test="${subs_status == 2}">
                                <h2>
                                    <p>
                                    Confirm your subscription order by clicking on OK
                                    button.
                                    </p>
                                </h2>
                                <div id="table-content">
                                    <form action="status.htm">
                                        <display:table name="submitted_channels" id="channel"
                                            defaultsort="1" requestURI="submit.htm"
                                            export="false" cellpadding="0" cellspacing="2"
                                            class="tableborder">
                                            <display:column sortProperty="id" sortable="true"
                                                title="Channel ID" class="tdcenter">
                                                <fmt:formatNumber value="${channel.id}"
                                                    pattern="00" />
                                            </display:column>
                                            <display:column sortable="true" title="Channel name"
                                                sortProperty="name" paramId="name" paramProperty="name"
                                                class="tdcenter" value="${channel.name}">
                                            </display:column>
                                            <display:column class="tdhidden">
                                                <input type="checkbox" name="submitted_channels"
                                                       value="${channel.name}" checked/>
                                            </display:column>
                                        </display:table>
                                        <div id="table-footer-leftcol">
                                            <input type="submit" value="OK" name="submit_button"/>
                                        </div>
                                    </form>
                                    <div id="table-footer-rightcol">
                                        <form action="subscriptions.htm">
                                            <input type="submit" value="Cancel" name="cancel_button"/>
                                        </form>
                                    </div>
                                    <div id="table-footer">
                                        <a href="subscriptions.htm">&#60&#60 Subscription status</a>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${subs_status == 1}">
                                <div id="message-box">
                                    Click on Submit button now to cancel all your
                                    active subscriptions.
                                </div>
                                <div id="table-footer">
                                    <div id="table-footer-leftcol">
                                        <form action="status.htm">
                                            <input type="submit" value="Submit" name="submit_button"/>
                                        </form>
                                    </div>
                                    <div id="table-footer-rightcol">
                                        <form action="subscriptions.htm">
                                            <input type="submit" value="Cancel" name="cancel_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    Your subscription status was not changed.
                                    Click on OK button to go back to the selection page.
                                </div>
                                <div id="table-footer">
                                    <div id="table-footer-rightcol">
                                        <form action="subscriptions.htm">
                                            <input type="submit" value="OK" name="cancel_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
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





