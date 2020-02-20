<?php
$s_config = new Site_Config();
if(!empty($admin->id)){

    $s_config = $s_config->where(["admin_id" => $admin->id])->one();
}else{
    $s_config->title = "Welcome";
    $s_config->tag_line = "A Simple Website";
    $s_config->image_name = "";
}
?>

<!DOCTYPE HTML>
<html lang="en">
<head>
	<title><?php echo $s_config->title . " - " . $s_config->tag_line; ?></title>
    <link rel="icon" href="<?php echo "uploads" . DIRECTORY_SEPARATOR . $s_config->image_name; ?>">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta charset="UTF-8">

	<!-- Fonts -->
	<link href="https://fonts.googleapis.com/css?family=Lato:400,700%7CRoboto:400,500" rel="stylesheet">

	<!-- Font Icons -->
	<link rel="stylesheet" href="fonts/ionicons.css">

	<!-- Styles -->
	<link rel="stylesheet" href="common/other/styles.css">
</head>

