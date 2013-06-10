<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
Document   : clock
Created on : Apr 4, 2013, 12:38:23 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" 
    "http://www.w3.org/TR/html4/strict.dtd">

<%@include file="/WEB-INF/jsp/include.jsp" %>

<%@page import="java.util.Date" %>
<%@page import="java.util.Locale" %>
<%@page import="java.text.SimpleDateFormat" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Clock</title>
    </head>
    <body>
        <%
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat(
                    "EEE, MMM d HH:mm:ss z yyyy", Locale.US);
            out.print(format.format(date));
        %>
    </body>
</html>
