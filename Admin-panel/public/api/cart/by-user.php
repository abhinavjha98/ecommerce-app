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

            if($user_id){
				
                $page = Helper::post_val("page");
				
				$cart_from_db = new Cart();
				
				
						
						
                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $cart_from_db = $cart_from_db->where(["user_id" => $user_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();
                }else $cart_from_db = $cart_from_db->where(["user_id" => $user_id])->orderBy("id")->desc()->all();



						
						
                $cart_from_db_arr = [];
                foreach ($cart_from_db as $item){
                    $product = new Product();
                    $product = $product->where(["id"=>$item->product_id])->one();
					
					
					
					if(!empty($product)){
						
						
						$inventory = new Inventory();
						$inventory = $inventory->where(["id" => $item->inventory_id])->one();
						
						
						
						if(!empty($inventory)){
							
							$attr_arr = explode(",", $inventory->attributes);
							
							$inv_arr = [];
							$inv_str = "";
							
							foreach ($attr_arr as $item){
								
								$attr_value_arr = explode("-", $item);
								
								$attribute = new Attribute();
								$attribute = $attribute->where(["id" => trim($attr_value_arr[0])])->one();
								
								
								
								$attribute_val = new Attribute_Value();
								$attribute_val = $attribute_val->where(["id" => trim($attr_value_arr[1])])->one();
								
								
								if(!empty($attribute)) $inv_str .= $attribute->title . " : ". $attribute_val->title . ", ";

							}
							
							$inv_arr["id"] = $inventory->id;
							$inv_arr["quantity"] = $inventory->quantity;
							
							if(!empty($inv_str)) $inv_arr["attribute"] = substr($inv_str, 0, -2);
							else $inv_arr["attribute"] = "";
						
							
							
						}
						
						$response_product = response_product($product)->to_Valid_array();
						
						$response_product['inventory'] =  $inv_arr;
						
						array_push($cart_from_db_arr, $response_product);
						
					} 
                }

                $response->create(200, "Success.", $cart_from_db_arr);


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