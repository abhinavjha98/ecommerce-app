<?php require_once('../../../private/init.php'); ?>
<?php

$response = new Response();
$errors = new Errors();
$send_mail = false;

if(Helper::is_post()){
    $api_token = Helper::post_val("api_token");
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();

        if(!empty($setting)){
           if(isset($_POST["email"])){
                $user = new User();
                $user->email = trim($_POST["email"]);
                $user->validate_with(["email"]);
                $errors = $user->get_errors();

                if($errors->is_empty()){
                    $user = $user->where(["email" => $user->email])->one();
                    if(!empty($user)){

                        $new_user = new User();
                        $new_user->id = $user->id;
                        $new_user->email = $user->email;
                        $new_user->image_name = $user->image_name;
                        $new_user->image_resolution = $user->image_resolution;
                        $response->create(200, "Successfully Sent Verification Token", $new_user->to_valid_array());

                        $send_mail = true;
                        /*if(!$mailer->send()) $response->create(201, "Something Went Wrong. Please try Again", null);*/

                    }else $response->create(201, "Invalid Email", null);
                }else $response->create(201, $errors, null);
            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

if($send_mail){
    Helper::curl_mail_sender("send-code.php", $user->id, $setting->api_token);
}

echo $response->print_response();

?>
