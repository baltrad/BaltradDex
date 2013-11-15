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

$(document).ready(function() {
    $(this).find("#toggle-download").click(function() {
        $(".exchange-submenu").find("#downloads-icon")
            .toggleClass("collapse-downloads-icon");
        $(".exchange-submenu").find("#list-downloads").slideToggle(200);
    });
    
    $(this).find("#toggle-upload").click(function() {
        $(".exchange-submenu").find("#uploads-icon")
            .toggleClass("collapse-uploads-icon");
        $(".exchange-submenu").find("#list-uploads").slideToggle(200);
    }); 
});

