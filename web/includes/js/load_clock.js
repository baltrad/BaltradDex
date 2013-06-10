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

function loadClock() {
    var self = this;
    // Non-IE browser
    if (window.XMLHttpRequest) {
        self.ajaxRequest = new XMLHttpRequest();
    }
    // IE
    else if (window.ActiveXObject) {
        self.ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
    }
    self.ajaxRequest.open('POST', 'clock.htm', true);
    self.ajaxRequest.setRequestHeader('Content-Type',
        'application/x-www-form-urlencoded' );

    var LOADED = 4;
    self.ajaxRequest.onreadystatechange = function() {
        if (self.ajaxRequest.readyState == LOADED) {
            doUpdate(self.ajaxRequest.responseText);
        }
    }
    queryString = '';
    self.ajaxRequest.send(queryString);
    self.setTimeout('loadClock()', 1000);
}
function doUpdate(message) {
    document.getElementById("clock").innerHTML = message;
}
$(document).ready(function() {
    loadClock();
});




