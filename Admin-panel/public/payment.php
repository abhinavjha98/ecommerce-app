<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

if(!empty($admin)){
	$pay = new Payment();
	$pay = $pay->where(["admin_id" => $admin->id])->one();

}else Helper::redirect_to("login.php");


?>

<?php require("common/php/php-head.php"); ?>

<body>

<?php require("common/php/header.php"); ?>

<div class="main-container">

	<?php require("common/php/sidebar.php"); ?>

	<div class="main-content">
		<div class="item-wrapper three">

			<div class="item">

                <?php if(Session::get_session_by_key("type") == "braintree"){
                    if($message) echo $message->format();
                }?>

				<div class="item-inner">
					<h4 class="item-header">Braintree Configuration</h4>

					<div class="item-content">
						<form data-validation="true" action="../private/controllers/payment.php" method="post">
							<input type="hidden" name="id" value="<?php echo $pay->id; ?>"/>
							<input type="hidden" name="admin_id" value="<?php echo $pay->admin_id; ?>"/>
                            
							<label>Environment</label>
							<input type="text" data-required="true" placeholder="Environment" name="environment" value="<?php echo $pay->environment; ?>"/>

							<label>Merchant ID</label>
							<input type="text" data-required="true" placeholder="Merchant ID" name="merchant_id" value="<?php echo $pay->merchant_id; ?>" />

							<label>Public Key</label>
							<input type="text" data-required="true" placeholder="Public Key" name="public_key" value="<?php echo $pay->public_key; ?>" />

							<label>Private Key</label>
							<input type="text" data-required="true" placeholder="Private Key" name="private_key" value="<?php echo $pay->private_key; ?>" />

							<div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Update</b></button></div>

                            <?php if(Session::get_session_by_key("type") == "braintree"){
                                Session::unset_session_by_key("type");
                                if($errors) echo $errors->format();
                            }?>

						</form>
					</div><!--item-content-->

				</div><!--item-inner-->
			</div><!--item-->



		</div><!--item-wrapper-->
	</div><!--main-content-->
</div><!--main-container-->

<?php echo "<script>maxUploadedFile = '" . MAX_IMAGE_SIZE  . "'</script>"; ?>

<?php require("common/php/php-footer.php"); ?>