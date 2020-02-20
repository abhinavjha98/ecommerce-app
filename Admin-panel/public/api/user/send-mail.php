<?php require_once('../../../private/init.php'); ?>
<?php

$response = new Response();
$errors = new Errors();

if(Helper::is_get()){
    $api_token = Helper::get_val("api_token");
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();

        if(!empty($setting)){
            $user = new User();
            $id = Helper::get_val("id");
            if($id){

                $user = $user->where(["id" => $id])->one();
                $mailer = new Mailer($user);
                $mailer->send();

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

$response->create(200, "Success", null);

/*echo $response->print_response();*/

?>
