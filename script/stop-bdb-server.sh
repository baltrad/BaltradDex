###################################################################################
#                                                                                 #
# Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW     #
#                                                                                 #
# This file is part of the BaltradDex software.                                   #
#                                                                                 #
# BaltradDex is free software: you can redistribute it and/or modify              #
# it under the terms of the GNU Lesser General Public License as published by     #
# the Free Software Foundation, either version 3 of the License, or               #
# (at your option) any later version.                                             #
#                                                                                 #
# BaltradDex is distributed in the hope that it will be useful,                   #
# but WITHOUT ANY WARRANTY; without even the implied warranty of                  #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   #
# GNU Lesser General Public License for more details.                             #
#                                                                                 #
# You should have received a copy of the GNU Lesser General Public License        #
# along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.   # 
#                                                                                 #
###################################################################################
#
# Stops baltrad-db server
#
# $1 - bdb PID file

#!/bin/bash

bdb_pid_file=$1

echo 'Stopping baltrad-bdb server...'

pid=`cat $bdb_pid_file`

kill -9 "$pid"
if [ $? -eq 0 ];
then
    echo "baltrad-bdb server stopped."
else
    echo "Failed to stop baltrad-bdb server."
fi

rm $bdb_pid_file
if [ $? -eq 0 ]
then
    echo "$bdb_pid_file successfully removed."
else
    echo "Failed to remove $bdb_pid_file."
fi

##################################################################################
