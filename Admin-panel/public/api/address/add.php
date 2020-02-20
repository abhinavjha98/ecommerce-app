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
            $address->line_1 = Helper::post_val("line_1");
            $address->line_2 = Helper::post_val("line_2");
            $address->city = Helper::post_val("city");
            $address->zip = Helper::post_val("zip");
            $address->state = Helper::post_val("state");
            $address->country = Helper::post_val("country");
            $address->user = Helper::post_val("user_id");
            $address->id = Helper::post_val("id");

			
            if($address->user  && $address->line_1 && $address->city && $address->zip && $address->state && $address->country){
                
				if(!$address->id){
					$address->id = $address->save();
					if($address->id > 0) $response->create(200, "Success.", $address->to_valid_array());
					else $response->create(201, "Something Went Wrong. Please try Again.", null);
				}else{
					if($address->where(["id"=>$address->id])->update()) $response->create(200, "Success.", $address->to_valid_array());
					else $response->create(201, "Something Went Wrong. Please try Again.", null);
				}
				
            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>