<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Channel subscription</title>
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
                        <h1>Channel subscription status</h1>
                        <br/>
                        <h2>
                            <p>
                            List of active subscriptions.
                            Click on a check box to subscribe or unsubscribe a desired data channel.
                            </p>
                        </h2>
                        <form action="submit.htm">
                            <display:table name="subscriptions" id="subscription" defaultsort="1"
                                requestURI="subscriptions.htm" cellpadding="0" cellspacing="2"
                                export="false" class="tableborder">
                                <display:column sortProperty="id" sortable="true"
                                    title="Channel ID" class="tdcenter">
                                    <fmt:formatNumber value="${subscription.id}"
                                    pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="Channel name"
                                    sortProperty="name" paramId="name" paramProperty="name"
                                    class="tdcenter" value="${subscription.name}">
                                </display:column>
                                <c:choose>
                                    <c:when test="${subscription.selected == true}">
                                        <display:column sortable="false" title="Subscription status"
                                            class="tdcheck"> <input type="checkbox"
                                            name="selected_channels" value="${subscription.name}"
                                            checked/>
                                        </display:column>
                                    </c:when>
                                    <c:otherwise>
                                        <display:column sortable="false" title="Subscription status"
                                            class="tdcheck"> <input type="checkbox"
                                            name="selected_channels" value="${subscription.name}"/>
                                        </display:column>
                                    </c:otherwise>
                                </c:choose>
                            </display:table>
                            <div id="table-footer">
                                <input type="submit" value="Submit" name="submitButton"/>
                            </div>
                        </form>
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
