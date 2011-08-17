/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

/*
 * Time input field validator.
 */

function validateStartHour(){
    var hour = document.forms["fileBrowser"]["startHour"].value;
    if(!isNumeric(hour) || hour < 0 || hour > 23) {
        document.forms["fileBrowser"]["startHour"].value = 0;
    }
}
function validateEndHour(){
    var hour = document.forms["fileBrowser"]["endHour"].value;
    if(!isNumeric(hour) || hour < 0 || hour > 23) {
        document.forms["fileBrowser"]["endHour"].value = 0;
    }
}
function validateStartMinute(){
    var minute = document.forms["fileBrowser"]["startMinute"].value;
    if( !isNumeric(minute) || minute < 0 || minute > 59 ) {
        document.forms["fileBrowser"]["startMinute"].value = 0;
    }
}
function validateEndMinute(){
    var minute = document.forms["fileBrowser"]["endMinute"].value;
    if( !isNumeric(minute) || minute < 0 || minute > 59 ) {
        document.forms["fileBrowser"]["endMinute"].value = 0;
    }
}
function validateStartSecond(){
    var second = document.forms["fileBrowser"]["startSecond"].value;
    if( !isNumeric(second) || second < 0 || second > 59 ) {
        document.forms["fileBrowser"]["startSecond"].value = 0;
    }
}
function validateEndSecond(){
    var second = document.forms["fileBrowser"]["endSecond"].value;
    if( !isNumeric(second) || second < 0 || second > 59 ) {
        document.forms["fileBrowser"]["endSecond"].value = 0;
    }
}
function isNumeric(value){
    if(!value.toString().match(/^[-]?\d*\d*$/)) return false;
    return true;
}
//--------------------------------------------------------------------------------------------------