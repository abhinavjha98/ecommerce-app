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
            if(isset($_POST["email"]) && isset($_POST["verification_token"]) && isset($_POST["password"])){

                $user = new User();
                $user->email = trim($_POST["email"]);
                $user->verification_token = trim($_POST["verification_token"]);
                $user->password = trim($_POST["password"]);
                $user->validate_with(["email", "verification_token", "password"]);
                $errors = $user->get_errors();
                
                if($errors->is_empty()){

                    $user_form_db = $user->where(["email" => $user->email])->one();
                    if(!empty($user_form_db)){
                        if($user_form_db->status == 1){
                            if($user_form_db->verification_token === $user->verification_token){
                                if($user->where(["id" => $user_form_db->id])->update()){

                                    $user->verification_token = "";
                                    $user->status = "";
                                    $user->type = $user_form_db->type;
                                    $user->id = $user_form_db->id;
									
									$cart_count = new Cart();
									$cart_count = $cart_count->where(["user_id" => $user->id])->count();
									
									$response_user = $user->to_valid_array();
									$response_user["cart_count"] = $cart_count;
									
                                    $response->create(200, "Successfully Updated Password.", $response_user);

                                }else $response->create(201, "Something Went Wrong", null);
                            }else $response->create(201, "Invalid Token", null);
                        }else $response->create(201, "Please Verify Your Email First.", null);
                    } else $response->create(201, "Invalid User.", null);
                }else $response->create(201, $errors, null);
            } else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>
