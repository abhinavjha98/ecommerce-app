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
            $favourite = new Favourite();
            $favourite->item_id = Helper::post_val("item_id");
            $favourite->user_id = Helper::post_val("user_id");
            $favourite->admin_id = $setting->admin_id;

            if($favourite->item_id && $favourite->user_id && $favourite->admin_id){
                $favourite->id = $favourite->save();
                if($favourite->id){
                    $response->create(200, "Success.", $favourite->to_valid_array());
                }else $response->create(201, "Something Wnt Wrong. Please try Again.", null);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>