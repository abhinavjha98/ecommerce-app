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
            $item_id = Helper::post_val("item_id");


            if($item_id){
                $page = Helper::post_val("page");
                $review_images = new Review_Image();

                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $review_images = $review_images->where(["item_id" => $item_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();

                }else $review_images = $review_images->where(["item_id" => $item_id])->orderBy("id")->desc()->all();


                $response_arr["review_images"] = $review_images;

               $response->create(200, "Success.", $response_arr);
             

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