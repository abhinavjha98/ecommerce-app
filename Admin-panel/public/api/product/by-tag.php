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
            $user_id = Helper::post_val("user_id");
            $page = Helper::post_val("page");
            $tag = Helper::post_val("tag");

            if($tag){
                $products = new Product();
                if($page) {
                    $start = ($page - 1) * API_PAGINATION;
                    $products = $products->like(["tags" => trim($tag)])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();

                }else $products = $products->like(["tags" => trim($tag)])->orderBy("id")->desc()->all();

                $product_arr = [];
                foreach($products as $item){
                    if($item->status != 1) continue;
                    $product = response_product($item);

                    if($user_id){
                        $fav = new Favourite();
                        $fav = $fav->where(["user_id" => $user_id])->andWhere(["item_id" => $item->id])->one();

                        if(!empty($fav)) $product->create_property("favourited", 1);
                        else $product->create_property("favourited", 2);
                    }

                    array_push($product_arr, $product->to_valid_array());
                }

                if(count($product_arr) > 0) $response->create(200, "Success.", $product_arr);
                else $response->create(200, "Noting Found.", []);

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