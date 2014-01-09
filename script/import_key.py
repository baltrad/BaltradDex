'''-----------------------------------------------------------------------------
Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
-----------------------------------------------------------------------------'''

import sys, os, getopt, shutil, hashlib, psycopg2

def copy_key(key, dst):
	""" Copy key file from local file system.
	
	key -- key directory path 
	dst -- destination directory path
	"""	
	print "Copying the key from", key, " ..."
	try:
		sep = os.sep
		if dst[len(dst) - 1] != sep:
			dst += sep
		dst += extract_dir(key)
		shutil.copytree(key, dst)
		print "... OK."
	except IOError as err:
		print "Failed to copy the key. %s" % err

def ssh_copy_key(user, host, key, dst):
	""" Copy the key from remote host to local file system.
	
	user -- user at the remote host
	host -- remote host address
	key -- key directory path
	dst -- destination directory path
	"""
	print "Copying the key from", host, " ..."
	code = os.system('scp -r %s@%s:%s %s' % (user, host, key, dst))
	if code == 0:
		print "... OK."
	else:
		print "Failed to copy the key."
	
def store_key(user, host, db, pwd, key, checksum, authorized, injector):
	""" Store key's metadata in the database.
	
	user -- database server user
	host -- database server address
	db -- database name
	pwd -- database connection password
	key -- key directory path
	checksum -- key checksum
	authorized -- key authorization toggle
	injector -- key is injector toggle
	"""
	sql = "INSERT INTO dex_keys (name, checksum, authorized, injector) " \
			"VALUES (%s, %s, %s, %s)"
	conn = None
	cursor = None
	print "Storing key in the database", db, "..."
	try:
		conn = psycopg2.connect(
				host = host, 
				database=db, 
				user = user,
				password = pwd)
				 
		cursor = conn.cursor()
		cursor.execute(sql, (key, checksum, authorized, injector))	
		conn.commit();
		print "... OK."
	except psycopg2.DatabaseError as err:
		print "Failed to store the key. %s" % err
		sys.exit(1)
	finally:
		cursor.close()
		conn.close()
		
def extract_dir(path):
	""" Extract path substring after last ocurrence of separator character
	and return extracted directory string.
	
	path -- the path to extract dir from
	"""
	sep = os.sep

	if len(path) > 1 and path.endswith(sep): 
		path = path[:-1]
	if path.rfind(sep) > -1:
		return path[path.rfind(sep) + 1:]
	else:
		return path
		
def key_checksum(key):
	""" Calculate checksum for a given key.
	
	key -- folder containing the key
	"""
	md5 = hashlib.md5()
	if not os.path.exists(key):
		return 1
		
	try:
		print "Calculating key checksum ..."
		for root, dirs, files in os.walk(key):
			for name in files:
				filepath = os.path.join(root, name)
				try: 
					try:
						f = open(filepath, 'rb')
						buf = f.read(1024)
						while len(buf) > 0:
							md5.update(buf)
							buf = f.read(1024)
					finally:
						f.close()
				except:
					print "Failed to open file for checksum calculation"
					return 1
		digest = md5.hexdigest()
		print "... OK."
		return digest
	except: 	
		print "Failed to calculate key checksum"
		return 1
	
def usage():
	""" Print usage information.
	"""
	usage = "Usage: import_key.py --key=<keydir> --dst=<destdir>" \
			" [--user=<user> --host=<address>] --dbuser=<dbuser>" \
			" --dbhost=<dbaddress> --db=<dbname> --pwd=<dbpwd>" \
			"\n\n\tkey - key directory path" \
			"\n\tdst - destination directory path" \
			"\n\tuser - remote host user" \
			"\n\thost - remote host address" \
			"\n\tdbuser - database user" \
			"\n\tdbhost - database server address" \
			"\n\tdb - database name" \
			"\n\tpwd - database connection password"		
	print usage	
	
def main(argv):
	"""	Main method calling processing methods depending on the arguments used
	"""
	# source key directory
	key = None
	# destination directory
	dst = None
	# remote host user
	user = None
	# remote host address 
	host = None
	# database user
	dbuser = None
	# database host address
	dbhost = None
	# database name
	db = None
	# database password
	pwd = None
	
	try: 
		opts, args = getopt.getopt(argv, "hi:o:u:a:s:t:n:p:", 
			["key=","dst=","user=","host=","dbuser=","dbhost=","db=","pwd="])
	except getopt.GetoptError as err:
		print str(err)
		usage()
		sys.exit(2)
	
	if len(opts) == 0:
		usage()
		sys.exit()
	else:	
		for opt, arg in opts:
			if opt in ('-h', '--help'):
				usage()
				sys.exit()
			elif opt in ("-i", "--key"):
				key = arg
			elif opt in ("-o", "--dst"):
				dst = arg
			elif opt in ("-u", "--user"):
				user = arg
			elif opt in ("-a", "--host"):
				host = arg
			elif opt in ("-s", "--dbuser"):
				dbuser = arg		
			elif opt in ("-t", "--dbhost"):
				dbhost = arg
			elif opt in ("-n", "--db"):
				db = arg
			elif opt in ("-p", "--pwd"):
				pwd = arg
					
	if key and dst and dbuser and dbhost and db and pwd:
		if user and host:
			ssh_copy_key(user, host, key, dst)
		else:
			copy_key(key, dst)				

		checksum = key_checksum(key)
		key = extract_dir(key)
		store_key(dbuser, dbhost, db, pwd, key, checksum, False, False)
	else:
		usage()
	
if __name__ == '__main__':
	main(sys.argv[1:])

