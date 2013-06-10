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

function show_hide_status(id, class_expand, class_collapse) {
	
    var elem = document.getElementById(id);
	var icon = document.getElementById(id + "_icon");
    
	if(elem.style.display == "none" || elem.style.display == ""){
		elem.style.display = "block";
		icon.setAttribute("class", class_expand);
	} else {
		elem.style.display = "none";
		icon.setAttribute("class", class_collapse);
	}
    
}

