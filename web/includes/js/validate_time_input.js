/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

/*
 * Time input field validator.
 */

function validateHour(input_id){
    var hour = document.getElementById(input_id).value;
    if(!isNumeric(hour) || hour < 0 || hour > 23) {
        document.getElementById(input_id).value = 0;
    }
}

function validateMinSec(input_id){
    var minute = document.getElementById(input_id).value;
    if(!isNumeric(minute) || minute < 0 || minute > 59) {
        document.getElementById(input_id).value = 0;
    }
}

function isNumeric(value){
    if(!value.toString().match(/^[-]?\d*\d*$/)) return false;
    return true;
}
