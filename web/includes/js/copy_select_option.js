/*******************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

// Copy option between 2 regular and 2 hidden select boxes.
function copyOption(sel_from, sel_to, sel_from_hid, sel_to_hid) {
    
    var selFrom = document.getElementsByName(sel_from)[0];
    var selTo = document.getElementsByName(sel_to)[0];
    var selFromHid = document.getElementsByName(sel_from_hid)[0];
    var selToHid = document.getElementsByName(sel_to_hid)[0];
    
    var selIndex = selFrom.selectedIndex;
    var option = selFrom.options[selIndex];
    var optionHid = selFromHid.options[selIndex];
    
    removeErrorOption(sel_to);
    
    selTo.add(option);
    selToHid.add(optionHid);
    
    selectAll([sel_from_hid, sel_to_hid]);
}

// Select all options in the select boxes. 
function selectAll(sels) {
    for (var i in sels) {
        var sel = document.getElementsByName(sels[i])[0];
        for (var j = 0; j < sel.options.length; j++) {
            sel.options[j].selected = true;				
        }
    }
}

// Remove error option from select box.
function removeErrorOption(sel) {
    var selTo = document.getElementsByName(sel)[0];
    for (var i = 0; i < selTo.options.length; i++) {
        if (selTo.options[i].id == "error") {
            selTo.remove(i);
        }				
    }
}


