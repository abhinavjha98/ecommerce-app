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
            $address->id = Helper::post_val("id");

            if($address->id){

                if($address->where(["id" => $address->id])->delete()){
                    $response->create(200, "Success.", $address->to_valid_array());
                }else $response->create(201, "Something Wnt Wrong. Please try Again.", null);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>