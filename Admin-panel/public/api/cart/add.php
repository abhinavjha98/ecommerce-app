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
            $product_id = Helper::post_val("product_id");
            $inventory_id = Helper::post_val("inventory_id");

            if($user_id && $product_id && $inventory_id){

                $product_from_db = new Product();
				$product_from_db = $product_from_db->where(['id' => $product_id])->one();
				
				if(!empty($product_from_db)){
					
					
					$cart = new Cart();
					$cart->user_id = $user_id;
					$cart->product_id = $product_id;
					$cart->inventory_id = $inventory_id;
					
					$cart_from_db = new Cart();
					$cart_from_db= $cart_from_db->where(["user_id" => $user_id])->andWhere(["product_id" => $product_id])
						->andWhere(["inventory_id" => $inventory_id])->one();
					
					if(empty($cart_from_db)){
						
						$cart->id = $cart->save();
					
						if($cart->id > 0) $response->create(200, "Success.", $cart);
						else $response->create(201, "Something went wrong. Please try again.", null);
					
					}else $response->create(200, "Product already added to the cart.", $cart_from_db);
					
					
					
				}else $response->create(201, "Invalid Product", null);
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