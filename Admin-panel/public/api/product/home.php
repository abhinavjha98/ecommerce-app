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
            $response_arr = [];

            if($page ==  1){

                $slider = new Slider();
                $slider = $slider->where(["admin_id"=>$setting->admin_id])->andWhere(["status"=>1])->all();
                $response_arr["slider"] = $slider;

                $categories = new Category();
                $categories = $categories->where(["admin_id"=>$setting->admin_id])->andWhere(["status"=>1])->all();
                $response_arr["category"] = $categories;

                $featured_products = new Product();
                $featured_products = $featured_products->where(["admin_id"=>$setting->admin_id])
                    ->andWhere(["status"=>1])->orderBy('id')->desc()->andWhere(["featured"=>1])
                    ->limit(0, API_PAGINATION)->all();

                $existingProduct = [];

                $updated_featured = [];
                foreach($featured_products as $item){
                    $product = response_product($item);
                    array_push($existingProduct, $item->id);

                    if($user_id){
                        $fav = new Favourite();
                        $fav = $fav->where(["user_id"=>$user_id])->andWhere(["item_id"=>$item->id])->one();

                        if(!empty($fav)) $item->create_property("favourited", 1);
                        else $item->create_property("favourited", 2);
                    }
                    $item->description = null;
                    array_push($updated_featured, $item->to_valid_array());
                }

                $response_arr["featured"] = $updated_featured;


                $popular_products = new Product();
                $popular_products = $popular_products->where(["admin_id"=>$setting->admin_id])
                    ->andWhere(["status"=>1])->orderBy('sell')->desc()->not(["featured"=>1])
                    ->limit(0, API_PAGINATION)->all();

                $updated_popular = [];
                foreach($popular_products as $item){
                    array_push($existingProduct, $item->id);

                    $product = response_product($item);
                    if($user_id){
                        $fav = new Favourite();
                        $fav = $fav->where(["user_id"=>$user_id])->andWhere(["item_id"=>$item->id])->one();

                        if(!empty($fav)) $item->create_property("favourited", 1);
                        else $item->create_property("favourited", 2);
                    }

                    $item->description = null;
                    array_push($updated_popular, $item->to_valid_array());
                }

                $response_arr["popular"] = $updated_popular;

                $recent_products = new Product();
                $recent_products = $recent_products->where(["admin_id" => $setting->admin_id])
                    ->andWhere(["status"=>1])->orderBy('id')->desc()
                    ->limit(0, API_PAGINATION)->all();

                $updated_recent = [];
                foreach($recent_products as $key => $value){
                    if(in_array ($value->id, $existingProduct)){
                        unset($recent_products[$key]);
                    }else{
                        $product = response_product($value);

                        if($user_id){
                            $fav = new Favourite();
                            $fav = $fav->where(["user_id" => $user_id])->andWhere(["item_id" => $value->id])->one();

                            if(!empty($fav)) $value->create_property("favourited", 1);
                            else $value->create_property("favourited", 2);
                        }
                        $value->description = null;
                        array_push($updated_recent, $value->to_valid_array());
                    }
                }

                $response_arr["recent"] = $updated_recent;

                $response_arr["existing"] = $existingProduct;
                $response->create(200, "Success", $response_arr);

            }else if($page > 1){
                $start = ($page - 1) * API_PAGINATION;
                $existingProduct = Helper::post_val("existing");
                if($existingProduct) $existingProduct = json_decode('[' . $existingProduct . ']', true);
                else $existingProduct = [];

                $recent_products = new Product();
                $recent_products = $recent_products->where(["admin_id"=>$setting->admin_id])
                    ->andWhere(["status"=>1])->orderBy('id')->desc()
                    ->limit($start, API_PAGINATION)->all();
                
                $updated_recent = [];
                foreach($recent_products as $key => $value){
                    $value->admin_id = null;

                    if(!in_array (($value->id * 1), $existingProduct)){
                        $product = response_product($value);
                        if($user_id){
                            $fav = new Favourite();
                            $fav = $fav->where(["user_id" => $user_id])->andWhere(["item_id" => $value->id])->one();

                            if(!empty($fav)) $product->create_property("favourited", 1);
                            else $product->create_property("favourited", 2);
                        }
                        $product->description = null;
                        array_push($updated_recent, $product->to_valid_array());
                    }
                }

                $response_arr["recent"] = $updated_recent;
                $response->create(200, "Success", $response_arr);

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