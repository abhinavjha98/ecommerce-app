<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());
$deletable_image_ids = "";

if(!empty($admin)){

    $category = new Category();
    $category->admin_id = $admin->id;
    $category->status = 1;
    if(Helper::is_get() && isset($_GET["id"])){
        $category->id = $_GET["id"];
        $category = $category->where(["id" => $category->id])->andwhere(["admin_id" => $admin->id])->one();
    }
}else Helper::redirect_to("login.php");

?>

<?php require("common/php/php-head.php"); ?>

<body>

<?php require("common/php/header.php"); ?>

<div class="main-container">
    <?php require("common/php/sidebar.php"); ?>

    <div class="main-content">
        <div class="item-wrapper one">

            <div class="item">
                <?php if($message) echo $message->format(); ?>

                <form data-validation="true" action="../private/controllers/category.php" method="post" enctype="multipart/form-data">
                    <div class="item-inner">

                        <div class="item-header">
                            <h5 class="dplay-inl-b">Category</h5>

                            <h5 class="float-r oflow-hidden">
                                <label class="status switch">
                                    <input type="checkbox" name="status"
                                        <?php if($category->status == 1) echo "checked"; ?>/>
                                    <span class="slider round">
                                        <b class="active">Active</b>
                                        <b class="inactive">Inactive</b>
                                    </span>
                                </label>
                                <span class="toggle-title"></span>
                            </h5>
                        </div><!--item-header-->

                        <div class="item-content">
                            <input type="hidden" name="id" value="<?php echo $category->id; ?>">
                            <input type="hidden" name="admin_id" value="<?php echo $category->admin_id; ?>">
                            <input type="hidden" name="prev_image" value="<?php echo $category->image_name; ?>"/>

                            <label class="control-label" for="file">Primary Image(<?php echo "Max Image Size : " . MAX_IMAGE_SIZE . "MB. Required Format : png/jpg/jpeg"; ?>)</label>

                            <div class="image-upload">

                                <img src="<?php if(!empty($category->image_name))
                                    echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR . $category->image_name; ?>" alt="" class="uploaded-image"/>

                                <div class="h-100" class="upload-content">
                                    <div class="dplay-tbl">
                                        <div class="dplay-tbl-cell">
                                            <i class="ion-ios-cloud-upload"></i>
                                            <h5><b>Choose Your Image to Upload</b></h5>
                                            <h6 class="mt-10 mb-70">Or Drop Your Image Here</h6>
                                        </div>
                                    </div>
                                </div><!--upload-content-->
                                <input data-required="image" type="file" name="image_name" class="image-input"
                                       data-traget-resolution="image_resolution" value="<?php echo $category->image_name; ?>"/>
                                <input type="hidden" name="image_resolution" value="<?php echo $category->image_resolution; ?>"/>
                            </div>

                            <label>Title</label>
                            <input type="text" data-required="true" placeholder="Site Title" name="title" value="<?php echo $category->title; ?>"/>

                            <div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Save</b></button></div>

                            <?php if($errors) echo $errors->format(); ?>

                        </div><!--item-content-->
                    </div><!--item-inner-->

                </form>
            </div><!--item-->

        </div><!--item-wrapper-->
    </div><!--main-content-->
</div><!--main-container-->

<?php echo "<script>maxUploadedFile = '" . MAX_IMAGE_SIZE  . "'</script>"; ?>
<?php echo "<script>maxUploadedFileCount = '" . MAX_FILE_COUNT  . "'</script>"; ?>
<?php echo "<script>adminId = '" . $admin->id  . "'</script>"; ?>


<?php require("common/php/php-footer.php"); ?>

</body>