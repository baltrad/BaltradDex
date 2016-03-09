/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

// update messages timeout ID
var timeoutID;
// update messages toggle 
var toggleAutoUpdate = true;

function createXMLHttpObject() {
    var xmlHttp;
    // non-IE browsers
    if (window.XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest();
    }
    // IE
    else if (window.ActiveXObject) {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlHttp;
}

function loadXMLDoc(xmlHttp, url, cfunc) {
    xmlHttp.open('POST', url, true);
    xmlHttp.onreadystatechange = cfunc;
    xmlHttp.send();
}

function updateMessages() {
    xmlHttp = createXMLHttpObject();
    loadXMLDoc(xmlHttp,"messages_table.htm", function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
        	var el = document.getElementById("message-table");
        	if (el != null) {
                el.innerHTML = xmlHttp.responseText;
        	}
            //document.getElementById("message-table").innerHTML = 
            //    xmlHttp.responseText;
        }
    });
    timeoutID = setTimeout("updateMessages()", 1000);
}

function updateCounter() {
    xmlHttp1 = createXMLHttpObject();
    loadXMLDoc(xmlHttp1, "messages_sticky_counter.htm", function() {
        if (xmlHttp1.readyState == 4 && xmlHttp1.status == 200) {
            document.getElementById("sticky-counter").innerHTML = 
                xmlHttp1.responseText;
        }
    });
    setTimeout("updateCounter()", 5000);
}

function toggleTimeout(func, timeout) {
    toggleAutoUpdate = !toggleAutoUpdate;
    if (toggleAutoUpdate) {
        timeoutID = setTimeout(func, timeout)
        document.getElementById("auto-update-toggle").value = "Auto update on";
    } else {
        clearTimeout(timeoutID);
        document.getElementById("auto-update-toggle").value = "Auto update off";
    }
}

$(document).ready(function() {
    updateMessages();
    updateCounter();
});