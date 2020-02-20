<?php require_once('../private/init.php'); ?>
<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

if(empty($admin)){
    Helper::redirect_to("login.php");
}else{
    $admobs = new Admob();
    $admobs = $admobs->where(["admin_id" => 1])->one();
}

?>

<?php require("common/php/php-head.php"); ?>

<body>

<?php require("common/php/header.php"); ?>

<div class="main-container">

	<?php require("common/php/sidebar.php"); ?>

	<div class="main-content">

            <div class="item-wrapper three">

                <form data-validation="true" method="post" action="../private/controllers/admob.php">

                    <input type="hidden" name="id" value="<?php echo $admobs->id; ?>"/>
                    <input type="hidden" name="admin_id" value="<?php echo $admobs->admin_id; ?>"/>

                    <div class="item">
                        
                        <?php if(Session::get_session_by_key("type") == "banner_ad"){
                            if($message) echo "<div class='mt-15'>" . $message->format() .'</div>';
                        } ?>

                        <div class="item-inner">
                            <div class="item-header">
                                <h5 class="dplay-inl-b">Banner Ad</h5>

                                <h5 class="float-r oflow-hidden">
                                    <label href="#" class="switch">
                                        <input type="checkbox" name="banner_status"
                                            <?php if($admobs->banner_status == 1) echo "checked"; ?>/>
                                        <span class="slider round"></span>
                                    </label>
                                    <span class="toggle-title"></span>
                                </h5>
                            </div>

                            <div class="item-content">

                                <label>App ID</label>
                                <input data-required="true"  type="text" placeholder="eg. ca-app-pub-8647879749090078~4918217822"
                                       value="<?php echo $admobs->banner_id; ?>" name="banner_id"/>

                                <label>Ad Unit ID</label>
                                <input data-required="true"  type="text" placeholder="eg. ca-app-pub-ca-app-pub-3940256099942544/5224354917"
                                       value="<?php echo $admobs->banner_unit_id; ?>" name="banner_unit_id"/>
                                <div class="btn-wrapper"><button type="submit" class="c-btn mb-10"><b>Update</b></button></div>

                                <?php if(Session::get_session_by_key("type") == "banner_ad"){
                                    Session::unset_session_by_key("type");
                                    if($errors) echo $errors->format();
                                } ?>

                            </div><!--item-content-->
                        </div><!--item-inner-->
                    </div><!--item-->
                </form>

                <form data-validation="true" method="post" action="../private/controllers/admob.php">

                    <input type="hidden" name="id" value="<?php echo $admobs->id; ?>"/>
                    <input type="hidden" name="admin_id" value="<?php echo $admobs->admin_id; ?>"/>

                    <div class="item">

                        <?php if(Session::get_session_by_key("type") == "interstitial_ad"){
                            if($message) echo "<div class='mt-15'>" . $message->format() .'</div>';
                        } ?>

                        <div class="item-inner">
                            <div class="item-header">
                                <h5 class="dplay-inl-b">Interstitial Ad</h5>

                                <h5 class="float-r oflow-hidden">
                                    <label href="#" class="switch">
                                        <input type="checkbox" name="interstitial_status"
                                            <?php if($admobs->interstitial_status== 1) echo "checked"; ?>/>
                                        <span class="slider round"></span>
                                    </label>
                                    <span class="toggle-title"></span>
                                </h5>
                            </div>

                            <div class="item-content">
                                <label>App ID</label>
                                <input data-required="true"  type="text" placeholder="eg. ca-app-pub-8647879749090078~4918217822"
                                       value="<?php echo $admobs->interstitial_id; ?>" name="interstitial_id"/>

                                <label>Ad Unit ID</label>
                                <input data-required="true" type="text" placeholder="eg. ca-app-pub-ca-app-pub-3940256099942544/5224354917"
                                       value="<?php echo $admobs->interstitial_unit_id; ?>" name="interstitial_unit_id" />
                                <div class="btn-wrapper"><button type="submit" class="c-btn mb-10"><b>Update</b></button></div>

                                <?php if(Session::get_session_by_key("type") == "interstitial_ad"){
                                    Session::unset_session_by_key("type");
                                    if($errors) echo $errors->format();
                                }?>

                            </div><!--item-content-->
                        </div><!--item-inner-->
                    </div><!--item-->
                </form>


                <!--Video Admob. To visible admob remove the class of dplay-none -->
                <form class="dplay-none" data-validation="true" method="post" action="../private/controllers/admob.php">

                    <input type="hidden" name="id" value="<?php echo $admobs->id; ?>"/>
                    <input type="hidden" name="admin_id" value="<?php echo $admobs->admin_id; ?>"/>

                    <div class="item">
                        <?php if(Session::get_session_by_key("type") == "video_ad"){
                            if($message) echo "<div class='mt-15'>" . $message->format() .'</div>';
                        } ?>

                        <div class="item-inner">

                            <div class="item-header">
                                <h5 class="dplay-inl-b">Video Ad</h5>

                                <h5 class="float-r oflow-hidden">
                                    <label href="#" class="switch">
                                        <input type="checkbox" name="video_status"
                                            <?php if($admobs->video_status== 1) echo "checked"; ?>/>
                                        <span class="slider round"></span>
                                    </label>
                                    <span class="toggle-title"></span>
                                </h5>
                            </div>

                            <div class="item-content">
                                <label>App ID</label>
                                <input data-required="true" type="text" placeholder="eg. ca-app-pub-8647879749090078~4918217822"
                                       value="<?php echo $admobs->video_id; ?>" name="video_id"/>

                                <label>Ad Unit ID</label>
                                <input data-required="true" type="text" placeholder="eg. ca-app-pub-ca-app-pub-3940256099942544/5224354917"
                                       value="<?php echo $admobs->video_unit_id; ?>" name="video_unit_id"/>
                                <div class="btn-wrapper"><button type="submit" class="c-btn mb-10"><b>Update</b></button></div>

                                <?php if(Session::get_session_by_key("type") == "video_ad"){
                                    Session::unset_session_by_key("type");
                                    if($errors) echo $errors->format();
                                }?>

                            </div><!--item-content-->

                        </div><!--item-inner-->
                    </div><!--item-->
                </form>
            </div><!--item-wrapper-->

	</div><!--main-content-->
</div><!--main-container-->


<?php require("common/php/php-footer.php"); ?>