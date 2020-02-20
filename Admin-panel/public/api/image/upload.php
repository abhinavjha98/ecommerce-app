<?php require_once('../../../private/init.php'); ?>

<?php
$response  = new Response();
$admin = Session::get_session(new Admin());
if(!empty($admin)){
    if(Helper::is_post()){
        if(isset($_FILES["image_name"])){

            $upload = new Upload($_FILES["image_name"]);
            $upload->set_max_size(MAX_IMAGE_SIZE);
            if($upload->upload()){

                $response->create(200, "Success", $upload);

            }else $response->create(201, $upload->get_errors()->format(), null);
        }else $response->create(201, "Invalid Parameter", null);
    }else $response->create(201, "Invalid request", null);
}else $response->create(201, "Please Login First", null);

echo $response->print_response();

?>