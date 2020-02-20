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
            $payment = new Payment();
            $payment = $payment->where(["admin_id"=>$setting->admin_id])->one();

            $payment->admin_id = null;
            $payment->id = null;

            $setting->create_property("payment", $payment->to_valid_array());
			
			$admob = new Admob();
            $admob = $admob->where(["admin_id"=>$setting->admin_id])->one();

            $admob->admin_id = null;
            $admob->id = null;

            $setting->create_property("admob", $admob->to_valid_array());
			
			$admin_address = new Admin_Address();
            $admin_address = $admin_address->where(["admin_id"=>$setting->admin_id])->one();
			
			$setting->create_property("admin_address", $admin_address->to_valid_array());
			
            $response->create(200, "Success", $setting->to_valid_array());

        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>
