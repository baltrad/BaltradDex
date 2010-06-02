<%--
    Document   : Clear datadelivery register interface
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
    <title>Clear data delivery register</title>
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
                        <h1>Clear data delivery register</h1>
                        <br/>
                        <h2>
                            <p>
                            Do you want to delete all records from data delivery register?
                            </p>
                        </h2>
                        <form action="confirmclearregister.htm">
                            <div id="table-footer-leftcol">
                                <input type="submit" value="OK" name="submit_button"/>
                            </div>
                        </form>
                        <form action="admin.htm">
                            <div id="table-footer-rightcol">
                                <input type="submit" value="Cancel" name="cancel_button"/>
                            </div>
                        </form>
                        <div id="table-footer">
                            <a href="welcome.htm">&#60&#60 Home</a>
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




