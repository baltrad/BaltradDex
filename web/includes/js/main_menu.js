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

function toggle(menu_id, icon_id) {
	var menu = document.getElementById(menu_id);
	var icon = document.getElementById(icon_id);
	if(menu.style.display=="none" || menu.style.display==""){
		menu.style.display="block";
		icon.setAttribute("class", "collapse");
	} else {
		menu.style.display="none";
		icon.setAttribute("class", "expand");
	}
}

function show(menu_id, icon_id) {
	var menu = document.getElementById(menu_id);
	var icon = document.getElementById(icon_id);
	menu.style.display="block";
	icon.setAttribute("class", "collapse");
}

function hide(menu_id, icon_id) {
	var menu = document.getElementById(menu_id);
	var icon = document.getElementById(icon_id);
	menu.style.display="none";
	icon.setAttribute("class", "expand");
}

