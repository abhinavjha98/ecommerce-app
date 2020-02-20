<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

if(!empty($admin)){
    $site_cofig = new Site_Config();
    $site_cofig = $site_cofig->where(["admin_id" => $admin->id])->one();

    $smtp_config = new Smtp_Config();
    $smtp_config = $smtp_config->where(["admin_id" => $admin->id])->one();

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

                <?php if(Session::get_session_by_key("type") == "site_configuration"){
                    if($message) echo $message->format();
                }?>

				<div class="item-inner">
					<h4 class="item-header">Site Configuration</h4>

					<div class="item-content config">
						<form data-validation="true" action="../private/controllers/site_config.php" method="post" enctype="multipart/form-data">
							<input type="hidden" name="id" value="<?php echo $site_cofig->id; ?>"/>
							<input type="hidden" name="admin_id" value="<?php echo $site_cofig->admin_id; ?>"/>
							<input type="hidden" name="prev_image" value="<?php echo $site_cofig->image_name; ?>"/>

							<label class="control-label" for="file">Logo(<?php echo "Max Image Size : " . MAX_IMAGE_SIZE . "MB. Required Format : png/jpg/jpeg"; ?>)</label>

                            <div class="image-upload">
								<div class="dplay-tbl">
									<div class="dplay-tbl-cell">
										<img class="max-h-200x uploaded-image" src="<?php if(!empty($site_cofig->image_name))
											echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR. $site_cofig->image_name; ?>" alt=""/>
									</div>
								</div>

                                <div class="h-100 upload-content">
                                    <div class="dplay-tbl">
                                        <div class="dplay-tbl-cell">
                                            <i class="ion-ios-cloud-upload"></i>
                                            <h5><b>Choose Your Image to Upload</b></h5>
                                            <h6 class="mt-10 mb-70">Or Drop Your Image Here</h6>
                                        </div>
                                    </div>
                                </div><!--upload-content-->
                                <input type="file" name="image_name" class="image-input" value="<?php echo $site_cofig->image_name; ?>"/>
                            </div>

							<label>Site Title</label>
							<input type="text" data-required="true" placeholder="Site Title" name="title" value="<?php echo $site_cofig->title; ?>"/>

							<label>Site Tag Line</label>
							<input type="text" data-required="true" placeholder="Site Tag Line" name="tag_line" value="<?php echo $site_cofig->tag_line; ?>" />

							<div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Update</b></button></div>

                            <?php if(Session::get_session_by_key("type") == "site_configuration"){
                                Session::unset_session_by_key("type");
                                if($errors) echo $errors->format();
                            }?>

						</form>
					</div><!--item-content-->

				</div><!--item-inner-->
			</div><!--item-->

			<div class="item">

                <?php if(Session::get_session_by_key("type") == "smtp_config"){
                    if($message) echo $message->format();
                } ?>

				<div class="item-inner">
					<h4 class="item-header">SMTP Configuration</h4>

					<div class="item-content">
						<form data-validation="true" method="post" action="../private/controllers/smtp_config.php">

							<input type="hidden" name="id" value="<?php echo $smtp_config->id; ?>"/>
							<input type="hidden" name="admin_id" value="<?php echo $smtp_config->admin_id; ?>"/>

							<label>Host</label>
							<input data-required="true" type="text" placeholder="eg. smtp.gmail.com" name="host" value="<?php echo $smtp_config->host; ?>">

							<label>Sender Email</label>
							<input data-required="true" type="text" placeholder="eg. doe@gmail.com" name="sender_email" value="<?php echo $smtp_config->sender_email; ?>">

							<label>Username</label>
							<input data-required="true" type="text" placeholder="eg. abc" name="username" value="<?php echo $smtp_config->username; ?>">

							<label>Password</label>
							<input data-required="true" type="password" placeholder="eg. password" name="password" value="<?php echo $smtp_config->password; ?>">

							<label>Port</label>
							<input data-required="true" type="text" placeholder="eg. 465" name="port" value="<?php echo $smtp_config->port; ?>">

                            <label>Encryption</label>
							<input data-required="true" type="text" placeholder="eg. tls" name="encryption" value="<?php echo $smtp_config->encryption; ?>">

                            <div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Update</b></button></div>

						</form>

                        <?php if(Session::get_session_by_key("type") == "smtp_config"){
							Session::unset_session_by_key("type");
							if($errors) echo $errors->format();
						}?>

					</div><!--item-content-->

				</div><!--item-inner-->
			</div><!--item-->


		</div><!--item-wrapper-->
	</div><!--main-content-->
</div><!--main-container-->

<?php echo "<script>maxUploadedFile = '" . MAX_IMAGE_SIZE  . "'</script>"; ?>

<?php require("common/php/php-footer.php"); ?>