###################################################################################
#                                                                                 #
# Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW     #
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
# Initializes keystore, creates and stores certificate to be used for message 
# authentication. Stores necessary parameters (keystore password, certificate 
# alias) in application's properties files.                             
#                                                                                       
# Parameters:                                                                            
#
# $1 - key alias
# $2 - keystore path /absolute path to the keystore file
# $3 - keystore password
# $4 - certificate owner
# $5 - organizational unit
# $6 - organization
# $7 - city
# $8 - state /country
# $9 - two-letter country code  
#

#!/bin/bash 

etc_dir=${PWD}/etc
default_props_file='dex.default.properties'
user_props_file='dex.user.properties'
key_alias_prop='key.alias'
keystore_pass_prop='keystore.pass'

# initialize keystore and save certificate
echo 'Checking whether certificate exists in the keystore...'

keytool -list -v -keystore $2 -alias $1
if [ "$?" = "1" ] 
then
	echo 'Certificate not found, generating new certificate...'
	keytool -genkey -alias "$1" -keystore "$2"  -keypass "$3" -keyalg DSA \
		-sigalg DSA -validity 1825 -storepass "$3" -dname "cn=$4, ou=$5, o=$6, l=$7, st=$8, c=$9"
else
	echo 'Certificate found, skipping...'
fi

# update properties files
for props in $default_props_file $user_props_file
do
	for p in $key_alias_prop $keystore_pass_prop
	do
		if [ "$p" = "$key_alias_prop" ]
		then
			cmd_param=$1
		else
			cmd_param=$3
		fi

		more "$etc_dir"/"$props" |grep "$p"
        	if [ "$?" = "1" ]
        	then
                	echo "Adding $p property to $props file..."
                	echo "$p=$cmd_param" >> "$etc_dir"/"$props"
        	else
                	echo "Updating $p property in $props file..."
                	sed -i".bak" "/$p/d" "$etc_dir"/"$props"
                	echo "$p=$cmd_param" >> "$etc_dir"/"$props"
                	rm "$etc_dir"/"$props".bak
        	fi
	done
done

##################################################################################
