<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$admin = Session::get_session(new Admin());

if(!empty($admin)){
	Helper::redirect_to("index.php");
    $admin = $admin->where(["id" => $admin->id])->one();
}else $admin = new Admin();

?>

<?php require("common/php/php-head.php"); ?>

<body>

<div class="dplay-tbl">
	<div class="dplay-tbl-cell">
		<div class="item-wrapper one pb-100">
			<div class="item">
				<div class="item-inner">
					<h4 class="item-header">Login</h4>
					<div class="item-content">
						<form data-validation="true" action="../private/controllers/admin_login.php" method="post">

							<label>Username</label>
							<input data-required="true" type="text" class="form-control" name="username"
								   value="<?php $admin->username; ?>" placeholder="Username">

							<label>Password</label>
							<input data-required="true" type="password" class="form-control" name="password"
								   value="<?php $admin->email; ?>" placeholder="Password">

							<div class="btn-wrapper"><button type="submit" class="c-btn mb-10"><b>Login</b></button></div>

							<?php if($errors) echo $errors->format(); ?>

						</form>
					</div><!--item-content-->
				</div><!--item-inner-->
			</div><!--item-->
		</div><!--item-wrapper-->
	</div><!--dplay-tbl-cell-->
</div><!-- dplay-tbl -->

<!-- jQuery library -->
<script src="plugin-frameworks/jquery-3.2.1.min.js"></script>


<!-- Main Script -->
<script src="common/other/script.js"></script>


</body>
</html>