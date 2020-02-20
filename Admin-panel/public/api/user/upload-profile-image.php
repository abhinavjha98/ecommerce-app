<?php require_once('../../../private/init.php'); ?>

<?php

$response = new Response();
$errors = new Errors();
if(Helper::is_post()){
    $api_token = Helper::post_val("api_token");
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();

        if(!empty($setting)){
            $user = new User();
            $id = Helper::post_val("id");
            $uploaded_image = $_FILES["image"];

            if(!empty($uploaded_image) && $id){
                $upload = new Upload($uploaded_image);
                $upload->set_max_size(MAX_IMAGE_SIZE);
                if($upload->upload()) {
                    $user->image_name = $upload->get_file_name();
                    $user->image_resolution = $upload->resolution;
                }
                $errors = $upload->get_errors();

                if($errors->is_empty()){
                    if($user->where(["id"=>$id])->update()) {
                        $user->id = $id;
                        $user->verification_token = null;
                        $response->create(200, "Success.", $user->to_valid_array());
                    }else $response->create(201, "Something went wrong please try again.", null);

                }else $response->create(201, $upload->get_errors()->to_sting(), null);
            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>