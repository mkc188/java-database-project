<?php

$_CONF = array();

/*
 * Database information
 */
$_CONF['db'] = array();
$_CONF['db']['hostname'] = 'db12.cse.cuhk.edu.hk';
$_CONF['db']['username'] = 'c108';
$_CONF['db']['password'] = 'ufjsrhvp';
$_CONF['db']['database'] = '(DESCRIPTION=
									(ADDRESS_LIST=
										(ADDRESS=
											(PROTOCOL=TCP)
											(HOST='.$_CONF['db']['hostname'].')
											(PORT=1521)
										)
									)
									(CONNECT_DATA=
										(SERVER=DEDICATED)
										(SERVICE_NAME='.$_CONF['db']['hostname'].')
									)
								)';

// Database handler
$conn = oci_connect($_CONF['db']['username'], $_CONF['db']['password'], $_CONF['db']['database']);

if( !$conn ) {
	echo "Cannot establish the connection.\n";
}

// for safety, unset database information.
unset($_CONF['db']);

?>