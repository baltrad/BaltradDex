################################################################################
#                                                                              #
# Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW  #
#                                                                              #
# This file is part of the BaltradDex software.                                #
#                                                                              #
# BaltradDex is free software: you can redistribute it and/or modify           #
# it under the terms of the GNU Lesser General Public License as published by  #
# the Free Software Foundation, either version 3 of the License, or            #
# (at your option) any later version.                                          #
#                                                                              #
# BaltradDex is distributed in the hope that it will be useful,                #
# but WITHOUT ANY WARRANTY; without even the implied warranty of               #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                #
# GNU Lesser General Public License for more details.                          #
#                                                                              #
# You should have received a copy of the GNU Lesser General Public License     #
# along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.# 
#                                                                              #
################################################################################
#
# Deploys BaltradDex on test server using node installer.                 
#                                                                                       
# Parameters:                                                                            
#
# $1 - BaltradDex project directory
# $2 - Node-installer project directory 
# $3 - Node installation prefix, e.g. /opt/baltrad
# $4 - Node installation parameters stored in the file

#!/bin/bash 

dex_dir=$1
node_installer_dir=$2
node_prefix=$3
args_file=$4

usage() {
    echo 'Usage: ./test-deploy.sh dex_dir node_installer_dir node_prefix \
         args_file'
    echo -e "\tdex_dir :: BaltradDex project directory"
    echo -e "\tnode_installer_dir :: Node installer project directory"
    echo -e "\tnode_prefix :: Node installation prefix, e.g. /opt/baltrad"
    echo -e "\targs_file :: Node installation parameters stored in a file"
}

build_dex() {
    cd $dex_dir
    ant dist
    if [ "$?" -eq 0 ];
    then
        echo "Successfully build BaltradDex."
    else
        echo "Failed to build BaltradDex." 
    fi
}

deploy_dex() {
    install_cmd="./setup $(cat $args_file) \
        --warfile=$dex_dir/dist/BaltradDex.war install"
    cd $node_installer_dir
    source "$node_prefix/etc/bltnode.rc"
    $install_cmd
    if [ "$?" -eq 0 ];
    then
        echo "Successfully deployed BaltradDex."
    else
        echo "Failed to deploy BaltradDex." 
    fi
} 

if [ "$#" = "4" ] 
then
    build_dex
    deploy_dex    
else
    usage
fi





