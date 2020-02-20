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
            $admobs = new Admob();
            $admobs = $admobs->where(["admin_id" => $setting->admin_id])->one();

            if(!empty($admobs)) $response->create(200, "Success.", $admobs->to_valid_array());
            else $response->create(201, "No Configuration Found.", null);

        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>
