<?php require_once('../private/init.php'); ?>

<?php

Session::unset_session(new Admin());
Helper::redirect_to("login.php");

?>