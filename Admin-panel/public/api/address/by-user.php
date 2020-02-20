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
            $address = new User_Address();
            $user_id = Helper::post_val("user_id");

            if($user_id){
                $address = $address->where(["user" => $user_id])->all();

                if (!empty($address)) $response->create(200, "Success.", $address);
                else $response->create(200, "No Item Found.", []);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>