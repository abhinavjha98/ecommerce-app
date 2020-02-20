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
            $favourite->user_id = Helper::post_val("user_id");

            if($favourite->user_id){
                $page = Helper::post_val("page");
                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $fav_arr = $favourite->where(["user_id" => $favourite->user_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();
                }else $fav_arr = $favourite->where(["user_id" => $favourite->user_id])->orderBy("id")->desc()->all();

                $fav_products = [];
                foreach ($fav_arr as $item){
                    $product = new Product();
                    $product = $product->where(["id"=>$item->item_id])->one();
					if(!empty($product)) array_push($fav_products, response_product($product)->to_Valid_array());
                }

                if(count($fav_arr) > 0)$response->create(200, "Success.", $fav_products);
                else $response->create(200, "No Item Found.", []);
                    

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();


function response_product($value){
    $product = new Product();
    $product->id = $value->id;
    $product->image_name = $value->image_name;
    $product->image_resolution = $value->image_resolution;
    $product->title = $value->title;
    $product->prev_price = $value->prev_price;
    $product->current_price = $value->current_price;
    $product->sell = $value->sell;
    return $product;
}

?>