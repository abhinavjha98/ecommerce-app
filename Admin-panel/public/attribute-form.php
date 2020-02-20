<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());
$deletable_image_ids = "";

if(!empty($admin)){
    $attribute = new Attribute();
    $attribute->admin_id = $admin->id;

    $attribute_values = new Attribute_Value();
    $attribute_value_str = "";

    if(Helper::is_get() && isset($_GET["id"])){
        $attribute->id = $_GET["id"];
        $attribute = $attribute->where(["id" => $attribute->id])->andwhere(["admin_id" => $admin->id])->one();

        $attribute_values = $attribute_values->where(["attribute"=>$attribute->id])->all();
        foreach ($attribute_values as $av){
            $attribute_value_str .= $av->title . ",";
        }
        $attribute_value_str = rtrim($attribute_value_str, ",");
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

                <form data-validation="true" action="../private/controllers/attribute.php" method="post" enctype="multipart/form-data">
                    <div class="item-inner">

                        <div class="item-header">
                            <h5 class="dplay-inl-b">Attribute</h5>
                        </div><!--item-header-->

                        <div class="item-content">
                            <input type="hidden" name="id" value="<?php echo $attribute->id; ?>">
                            <input type="hidden" name="admin_id" value="<?php echo $attribute->admin_id; ?>">

                            <label>Title</label>
                            <input type="text" data-required="true" placeholder="Attribute Name" name="title" value="<?php echo $attribute->title; ?>"/>

                            <label>Values(Comma Separated)</label>
                            <input type="text" data-required="true" placeholder="eg. Xl, L, M, SM" name="attribute_values" value="<?php echo $attribute_value_str; ?>"/>

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