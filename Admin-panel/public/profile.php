<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

if(!empty($admin)){
	$admin = $admin->where(["id" => Session::get_session($admin)->id])->one();
	$address = new Admin_Address();
	$address = $address->where(["admin_id"=>$admin->id])->one();

	if($address->line_2 == "null") $address->line_2 = "";
	
}else  Helper::redirect_to("login.php");

?>


<?php require("common/php/php-head.php"); ?>

<body>

<?php require("common/php/header.php"); ?>

<div class="main-container">

	<?php require("common/php/sidebar.php"); ?>

	<div class="main-content">

		<div class="item-wrapper two profile-page">

			<div class="item">

				<?php if(Session::get_session_by_key("type") == "admin_credentials"){
					if($message) {
						Session::unset_session_by_key("type");
						echo $message->format();
					}
				}?>


				<div class="item-inner">
					<h4 class="item-header">Profile</h4>

					<div class="item-content">
						<form data-validation="true" action="../private/controllers/admin.php" method="post">

							<input type="hidden" name="id" value="<?php echo $admin->id; ?>">
							<label>Admin Username</label>
							<input data-required="true" type="text" class="form-control" name="username" value="<?php echo $admin->username; ?>">
							<label>Admin Email</label>
							<input data-required="true" type="text" class="form-control" name="email" value="<?php echo $admin->email; ?>">
							<label>Admin Previous Password</label>
							<input data-required="true" type="password" class="form-control" name="password" value="" placeholder="Enter Previous Password">
							<label>Admin New Password</label>
							<input data-required="true" type="password" class="form-control" name="new_pass" value="" placeholder="Enter New Password">
							<label>Admin Confirm Password</label>
							<input data-required="true" type="password" class="form-control" name="confirm_pass" value="" placeholder="Enter Confirm Password">

							<div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Update</b></button></div>

							<?php if(Session::get_session_by_key("type") == "admin_credentials"){
								if($errors) {
									Session::unset_session_by_key("type");
									echo $errors->format();
								}
							}?>

						</form>
					</div><!--item-content-->

				</div><!--item-inner-->
			</div><!--item-->


			<div class="item">

				<?php if(Session::get_session_by_key("type") == "admin_address"){
					if($message) {
						Session::unset_session_by_key("type");
						echo $message->format();
					}
				}?>

				<div class="item-inner">
					<h4 class="item-header">Address</h4>

					<div class="item-content">
						<form data-validation="true" action="../private/controllers/admin_address.php" method="post">

							<input type="hidden" name="id" value="<?php echo $address->id; ?>">
							<input type="hidden" name="admin_id" value="<?php echo $admin->id; ?>">

							<label>Company Name</label>
							<input data-required="true" type="text" class="form-control" name="company_name" value="<?php echo $address->company_name; ?>">

							
							<label>Address Line 1</label>
							<input data-required="true" type="text" class="form-control" name="line_1" value="<?php echo $address->line_1; ?>">
							<label>Address Line 2</label>
							<input type="text" class="form-control" name="line_2" value="<?php echo $address->line_2; ?>">


							<div class="oflow-hidden w-100">
								<div class="input-6 pr-7-5">
									<label>City</label>
									<input data-required="true" type="text" class="form-control" name="city" value="<?php echo $address->city; ?>" placeholder="City">
								</div>

								<div class="input-6 pl-7-5">
									<label>Zip</label>
									<input data-required="true" type="text" class="form-control" name="zip" value="<?php echo $address->zip; ?>" placeholder="Zip">
								</div>
							</div><!--oflow-hidden-->

							<div class="oflow-hidden w-100">
								<div class="input-6 pr-7-5">
									<label>State</label>
									<input data-required="true" type="text" class="form-control" name="state" value="<?php echo $address->state; ?>" placeholder="State">
								</div>

								<div class="input-6 pl-7-5">
									<label>Country</label>
									<input data-required="true" type="text" class="form-control" name="country" value="<?php echo $address->country; ?>" placeholder="Country">
								</div>
							</div><!--oflow-hidden-->

							<div class="btn-wrapper"><button type="submit" class="c-btn mb-10"><b>Update</b></button></div>

							<?php if(Session::get_session_by_key("type") == "admin_address"){
								if($errors) {
									Session::unset_session_by_key("type");
									echo $errors->format();
								}
							}?>
							
						</form>
					</div><!--item-content-->

				</div><!--item-inner-->
			</div><!--item-->


		</div><!--item-wrapper-->
	</div><!--main-content-->
</div><!--main-container-->



<?php require("common/php/php-footer.php"); ?>