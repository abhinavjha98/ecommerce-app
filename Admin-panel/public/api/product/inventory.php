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
            $inventory = Helper::post_val("inventory");

            if($inventory){

                $inventory_arr = array_map("trim", explode(",", $inventory));
                $inv_from_db = [];

                foreach ($inventory_arr as $item){
                    $inv = new Inventory();
                    $inv = $inv->where(["id"=>$item])->one();
                    if(!empty($inv)) array_push($inv_from_db, $inv);
                }
                
                if(count($inv_from_db) > 0) $response->create(200, "Success.", $inv_from_db);
                else $response->create(201, "Noting Found.", null);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();



?>