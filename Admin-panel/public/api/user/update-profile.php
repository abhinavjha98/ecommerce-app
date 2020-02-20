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
            $user_id = Helper::post_val("id");

            if($user_id){
                $user = new User();
                $user = $user->where(["id"=>$user_id])->one();

                if(count($user) > 0){
                    $username = Helper::post_val("username");
                    $password = Helper::post_val("password");
                    if($username && !$password){
                        $updated = new User();
                        $updated->username = $username;
                        $updated->id = $user->id;
                        if($updated->where(["id"=>$user->id])->update()) $response->create(200, "Updated Successfully", $updated->to_valid_array());
                        else $response->create(201, "Something Went Wrong. Try Again.", null);

                    }else if($username && $password){
                        $new_password = Helper::post_val("new_password");
                        if($new_password){

                            if(password_verify($password, $user->password)){
                                $updated = new User();
                                $updated->id = $user->id;
                                $updated->username = $username;
                                $updated->password = $new_password;

                                if($updated->where(["id"=>$user->id])->update()) $response->create(200, "Updated Successfully", $updated->to_valid_array());
                                else $response->create(201, "Something Went Wrong. Try Again.", null);

                            }else $response->create(201, "Wrong Password", null);

                        }else $response->create(201, "New Password is Missing", null);
                    }else $response->create(201, "Invalid Parameter", null);
                }else $response->create(201, "Invalid User", null);
            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>
