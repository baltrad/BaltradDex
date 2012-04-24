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
# Runs baltrad-db server for integration tests
#
# $1 - baltrad node installation prefix 
# $2 - configuration file 
# $3 - bdb PID file
# $4 - bdb log file
#

#!/bin/bash

bltnode_prefix="$1"
bdb_conf_file="$2"
bdb_pid_file="$3"
bdb_log_file="$4"
bdb_url="$5"
bdb_sources_file="$6"
bltnode_etc="$bltnode_prefix/etc"
bdb_bin_path="$bltnode_prefix/baltrad-db/bin"

echo 'Starting baltrad-bdb server for integration tests...'

if [ ! -f "$bltnode_etc"/bltnode.rc ];
then
   echo 'bltnode.rc not found.'
   exit 1
fi

. "$bltnode_etc"/bltnode.rc
if [ "$?" -ne 0 ];
then
   echo 'Failed to initialize bltnode environment.'
   exit 1
fi

"$bdb_bin_path"/baltrad-bdb-drop --conf="$bdb_conf_file"
if [ "$?" -eq 0 ];
then
   echo "baltrad-bdb successfully dropped."
else
   echo "Failed to drop baltrad-bdb." 
fi

"$bdb_bin_path"/baltrad-bdb-create --conf="$bdb_conf_file"
if [ "$?" -eq 0 ];
then
   echo "baltrad-bdb successfully created."
else
   echo "Failed to create baltrad-bdb." 
fi

"$bdb_bin_path"/baltrad-bdb-server --conf="$bdb_conf_file" \
        --pidfile="$bdb_pid_file" --logfile="$bdb_log_file"
if [ "$?" -eq 0 ];
then
   echo "baltrad-bdb server started."
else
   echo "Failed to start baltrad-bdb server." 
fi

# give the server some time to come up
sleep 1

"$bdb_bin_path"/baltrad-bdb-client import_sources --url="$bdb_url" \
        "$bdb_sources_file"
if [ "$?" -eq 0 ];
then
   echo "Successfully imported baltrad-bdb sources."
else
   echo "Failed to import baltrad-bdb sources." 
fi

##################################################################################
