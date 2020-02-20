<?php require_once('../../../private/init.php'); ?>

<?php

$response = new Response();
$errors = new Errors();

if(Helper::is_post()){
    $api_token = Helper::post_val("api_token");
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();
        $page = Helper::post_val("page");

        if(!empty($setting)){
            $categories = new Category();
            
            if($page) {
                $start = ($page - 1) * API_PAGINATION;
                $categories = $categories->where(["admin_id"=>$setting->admin_id])
                    ->andWhere(["status"=>1])->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();

            }else $categories = $categories->where(["admin_id"=>$setting->admin_id])->andWhere(["status"=>1])->orderBy("id")->desc()->all();

            if(count($categories) > 0) $response->create(200, "Success.", $categories);
            else $response->create(200, "Noting Found.", []);


        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>