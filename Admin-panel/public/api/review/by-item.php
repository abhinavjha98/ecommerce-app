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
                $review = new Review();

                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $review_arr = $review->where(["item_id" => $item_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();

                }else $review_arr = $review->where(["item_id" => $item_id])->orderBy("id")->desc()->all();

                $response_arr = [];
                $reviews = [];

                foreach ($review_arr as $item){
                    $user = new User();
                    $user = $user->where(["id"=>$item->user_id])->one();
                    
                    if(!empty($user)){
                        $item->create_property("user_name" , $user->username);
                        $item->create_property("image_name", $user->image_name);
                    }else{
                        $item->create_property("user_name", "Unknown");
                        $item->create_property("image_name", PROFILE_DEFAULT);
                    }

                    $review_img = new Review_Image();
                    $review_img = $review_img->where(["review_id" => $item->id])->all();

                    $item->create_property("review_images", $review_img);

                    $item->admin_id = null;
					
					$item = $item->to_valid_array();
					
					if($item["review"] == null) $item["review"] = "";
					
                    array_push($reviews, $item);
                }



                if($page == 1 || !$page){
                    $star_count = [];
                    for($i = 5; $i > 0; $i--){
                        $reveiw_star = new Review();
                        $star_count[$i] = $reveiw_star->where(["item_id" => $item_id])->andWhere(["rating" => $i])->count();
                    }
                    $response_arr["star_count"] = $star_count;

                    $response_arr["avg_rating"] = round($review->where(["item_id" => $item_id])->avg("rating"), 2);
                    $response_arr["rating_count"] = $review->where(["item_id" => $item_id])->count();

                }

                $response_arr["reviews"] = $reviews;

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