<?php require_once('../../../private/init.php'); ?>


<?php
$response  = new Response();
$admin = Session::get_session(new Admin());
$DS = DIRECTORY_SEPARATOR;

if(!empty($admin)){
    if(Helper::is_post()){
        $item_image = new Item_Image();
        $item_image->image_name = Helper::post_val("image_name");
        $item_image->admin_id= Helper::post_val("admin_id");

        if($item_image->image_name && $item_image->admin_id){
            if($item_image->admin_id == $admin->id){
                if (Upload::delete($item_image->image_name)){

                    $response->create(200, "Success", $item_image->to_valid_array());

                }else $response->create(201, "Something Went Wrong.", null);
            }else $response->create(201, "Invalid User", null);
        }else $response->create(201, "Invalid Parameter", null);
    }else $response->create(201, "Invalid request", null);
}else $response->create(201, "Please Login First", null);

echo $response->print_response();

?>