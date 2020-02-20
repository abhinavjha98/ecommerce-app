<?php require_once('../../../private/init.php'); ?>

<?php

$response = new Response();
$errors = new Errors();
$order = new Orders();

if(Helper::is_post()){
	$secret_data = Helper::post_val("secret_data");
	
	if($secret_data){
		$json_data = json_decode(Encryption::decrypt(ENCRYPTION_KEY, ENCRYPTION_IV, $secret_data), true);

		$api_token = $json_data["api_token"];  
		$order->method = $json_data["method"];
		$order->amount = $json_data["amount"];
		$order->user = $json_data["user"];
		$order->address = $json_data["address"];
		$ordered_products = $json_data["ordered_products"];
		
		
		if($api_token){
			$setting = new Setting();
			$setting = $setting->where(["api_token" => $api_token])->one();
			
			if(!empty($setting)){
			   
				$order->admin_id = $setting->admin_id;
				$order->status = 0;
				$order->notification = 0;

				if($order->user && $order->method && $order->amount && $order->address && $ordered_products){
					
					$ordered_arr = [];
					$ordered_arr = json_decode($ordered_products);
					
					if(!empty($ordered_arr)){
						
						$order->id = $order->save();
						if($order->id > 0) {
							foreach ($ordered_arr as $item){
								$ordered = new Ordered_Product();
								$ordered->product_order = $order->id;
								$ordered->product = $item->product;
								$ordered->price = $item->price;
								$ordered->admin_id = $setting->admin_id;
								$ordered->inventory = $item->inventory;
								$ordered->quantity = $item->quantity;

								$ordered_product = new Product();
								$ordered_product = $ordered_product->where(["id"=>$ordered->product])->one();
								if(!empty($ordered_product)){
									$ordered->revenue = ($ordered->price - $ordered_product->purchase_price) * $ordered->quantity;

									if($ordered->save()){

										$ordered_inventory = new Inventory();
										$ordered_inventory = $ordered_inventory->where(["id"=>$ordered->inventory])->one();

										if(!empty($ordered_inventory)){
											$updated_inv = new Inventory();
											$updated_inv->quantity = ($ordered_inventory->quantity - $ordered->quantity);
											if($updated_inv->quantity == 0) $updated_inv->quantity = DEFAULT_NEGATIVE;
											
											$updated_inv->where(["id"=>$ordered->inventory])->update();
										}
										
										$delete_cart = new Cart();
										$delete_cart->where(["user_id" => $order->user])
											->andWhere(["product_id" => $ordered->product])
											->andWhere(["inventory_id" => $ordered->inventory])
											->delete();
										
									}
								}
							}
							
							$date = date_create($item->created);
							$order_no = date_format($date, 'mjY');
							$order_no = $order_no . $item->id;
							
							$response_arr = $order->to_valid_array();
							$response_arr["txn_id"] = $order_no;
							$response_arr["date"] = $date;
							
							$response->create(200, "Success", $response_arr);
							
						}else $response->create(201, "Something Went Wrong", null);
					}else $response->create(201, "Product can't be empty", null);
				}else $response->create(201, "Invalid parameters", null);
			}else $response->create(201, "Invalid Api Token", null);
		}else $response->create(201, "No Api Token Found", null);
	}else $response->create(201, "Invalid Parameter", null);
	
    
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>