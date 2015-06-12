<?php
// connection
include("database.php");

// original form
$filtered_keyword    = "";
$option_part         = false;
$option_category     = false;
$option_manufacturer = false;

// errors
$error_nokey    = false;
$error_nooption = false;

// search result
$result = array();

if( isset($_POST["search"]) && $_POST["search"] == "post" ) {
	$commit_search       = true;
	$option_part         = ( isset($_POST["option_part"]) );
	$option_category     = ( isset($_POST["option_category"]) );
	$option_manufacturer = ( isset($_POST["option_manufacturer"]) );
	$filtered_keyword    = str_replace("'", "\'", trim($_POST["keyword"]));
	
	// detect no keyword specified
	if( strlen($filtered_keyword) == 0 ) {
		$error_nokey   = true;
		$commit_search = false;
	}

	// detect no search option specified
	if( !( $option_part || $option_category || $option_manufacturer ) ) {
		$error_nooption = true;
		$commit_search  = false;
	}

	// do the search and place result in array
	if( $commit_search ) {
		$options = array();
		if( $option_part )         $options[] = "p.name LIKE '%" . $filtered_keyword . "%'";
		if( $option_category )     $options[] = "c.name LIKE '%" . $filtered_keyword . "%'";
		if( $option_manufacturer ) $options[] = "m.name LIKE '%" . $filtered_keyword . "%'";

		$where_keywords = implode(" OR ", $options);
		$query  = "SELECT p.pid as PID, p.name as PNAME, c.name as CATEGORY_NAME, m.name as MANUFACTURER_NAME, p.available_quantity as AVAILABLE_QTY";
		$query .= " FROM category c, manufacturer m, part p WHERE (" . $where_keywords . ") AND";
		$query .= " p.available_quantity > 0 AND p.manufacturer_id = m.mid AND p.category_id = c.cid ORDER BY p.pid ASC";

		$stid = oci_parse($conn, $query);
		oci_execute($stid);

		while( $row = oci_fetch_object($stid) ) {
			$result[] = array(
				"pid"           => $row->PID,
				"pname"         => $row->PNAME,
				"category"      => $row->CATEGORY_NAME,
				"manufacturer"  => $row->MANUFACTURER_NAME,
				"available_qty" => $row->AVAILABLE_QTY
			);
		}
	}
}

$box_error_nokey       = ( $error_nokey && !$error_nooption ) ? '': 'style="display: none"';
$box_error_nooption    = ( $error_nooption && !$error_nokey ) ? '': 'style="display: none"';
$box_error_both        = ( $error_nokey && $error_nooption ) ? '': 'style="display: none"';
$checkbox_part         = ( $option_part ) ? 'checked="checked"': '';
$checkbox_category     = ( $option_category ) ? 'checked="checked"': '';
$checkbox_manufacturer = ( $option_manufacturer ) ? 'checked="checked"': '';

// recover the quote char to normal
$filtered_keyword = str_replace("\'", "'", trim($_POST["keyword"]));
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	<title>Sale System: Part Search</title>

	<link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
	<div id="site_container">
		<form method="POST">
		<input type="hidden" name="search" value="post" />
		<table id="table_top" border="0" cellpadding="5" cellspacing="0">
			<tr class="box_search">
				<td id="box_search_keyword">
					<div id="box_error">
						<div class="box_error_msg" <?php echo $box_error_nokey; ?>>
							<b>Error</b>: no search key is specified.
						</div>
						<div class="box_error_msg" <?php echo $box_error_nooption; ?>>
							<b>Error</b>: no search option is specified.
						</div>
						<div class="box_error_msg" <?php echo $box_error_both; ?>>
							<b>Error</b>: no keyword and option specified.
						</div>
					</div>
					</div>
					<div style="margin-left: 80px;">
						Keyword<br />
						<input type="text" name="keyword" id="keyword" size="50" value="<?php echo $filtered_keyword; ?>" />
					</div>
				</td>
				<td>
					<input type="checkbox" name="option_part" id="option_part" <?php echo $checkbox_part; ?>/>&nbsp;By Part Name<br />
					<input type="checkbox" name="option_category" id="option_category" <?php echo $checkbox_category; ?>/>&nbsp;By Category Name<br />
					<input type="checkbox" name="option_manufacturer" id="option_manufacturer" <?php echo $checkbox_manufacturer; ?>/>&nbsp;By Manufacturer Name<br />
					<div style="margin-left: 40px;"><input type="submit" name="submit" id="submit" value="Search" onclick="return search();" /></div>
				</td>
			</tr>
			<tr>
				<td colspan="2" rowspan="1">
					<table id="table_buttom" border="0" cellpadding="5" cellspacing="0">
						<tr class="result_title">
							<td>Part ID</td>
							<td>Part Name</td>
							<td>Category</td>
							<td>Manufacturer</td>
							<td>Available Quantity</td>
						</tr>
<?php
$result_count = count($result);
if( $result_count > 0 ) {
	for( $i = 0; $i < $result_count; $i++ ) {
		$tr_class = ( $i+1 == $result_count ) ? 'class="result_last"' : '';
		$current = $result[$i];

		// highlight matched string
		if( $commit_search ) {
			$current["pname"]        = ( $option_part ) ? str_replace($filtered_keyword, '<span style="background-color: #FFFF00">'.$filtered_keyword.'</span>', $current["pname"]) : $current["pname"];
			$current["category"]     = ( $option_category ) ? str_replace($filtered_keyword, '<span style="background-color: #FFFF00">'.$filtered_keyword.'</span>', $current["category"]) : $current["category"];
			$current["manufacturer"] = ( $option_manufacturer ) ? str_replace($filtered_keyword, '<span style="background-color: #FFFF00">'.$filtered_keyword.'</span>', $current["manufacturer"]) : $current["manufacturer"];
		}
?>
						<tr <?php echo $tr_class; ?>>
							<td><?php echo $current["pid"]; ?></td>
							<td><?php echo $current["pname"]; ?></td>
							<td><?php echo $current["category"]; ?></td>
							<td><?php echo $current["manufacturer"]; ?></td>
							<td><?php echo $current["available_qty"]; ?></td>
						</tr>
<?php
	}
} else {
?>
						<tr>
							<td class="result_none" colspan="5" rowspan="1">no result found</td>
						</tr>
<?php
}
?>
					</table>
				</td>
			</tr>
		</table>
		</form>
	</div>
</body>
</html>
<?php

// close connection if exist
if( @$conn ) {
	oci_close($conn);
}

?>
