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
            $search = Helper::post_val("search");

            if($search){
                $products = new Product();
                if($page && is_numeric($page)) {
                    $start = ($page - 1) * API_PAGINATION;

                    $products = $products->where(["admin_id" => $setting->admin_id])
                        ->like(["title" => $search])->like(["tags" => $search])->search()
                        ->limit($start, API_PAGINATION)->all();

                }else $products = $products->where(["admin_id" => $setting->admin_id])
                    ->like(["title" => $search])->like(["tags" => $search])->search()
                    ->all();

                
                $search_terms_from_db = new Search_Terms();
                $search_terms_from_db = $search_terms_from_db->where(["admin_id" => $setting->admin_id])
                    ->orderBy("created")->orderType("ASC")->all();

                $search_terms = new Search_Terms();
                $search_terms->result_count = count($products);
                $search_terms->term = $search;
                $search_terms->admin_id = $setting->admin_id;

                if(count($search_terms_from_db) < DASHBOARD_ITEM_COUNT){
                    $search_terms->save();
                }else{
                    $search_term_id = $search_terms_from_db[0]->id;
                    $search_terms->created = date(DATE_FORMAT);
                    $search_terms->where(["id" => $search_term_id])->update();
                }


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