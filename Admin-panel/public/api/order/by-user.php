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
            $orders = new Orders();
            $user_id = Helper::post_val("user_id");

            if($user_id){
                $page = Helper::post_val("page");
                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $orders = $orders->where(["user" => $user_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();
                }else $orders = $orders->where(["user" => $user_id])->orderBy("id")->desc()->all();

                $order_arr = [];

                if (count($orders) > 0) {
                    foreach ($orders as $item){

                        $order = new Orders();
						
						$date = date_create($item->created);
                        $order_no = date_format($date, 'mjY');
                        $order_no = $order_no . $item->id;
									
                        $order->txn_id = $order_no;
                        $order->method = $item->method;
                        $order->amount = $item->amount;
                        $order->status = ($item->status == 2) ? "Clear" : "Pending";
                        $order->created = $item->created;

                        $ordered_products = new Ordered_Product();
                        $ordered_products = $ordered_products->where(["product_order"=>$item->id])->all();
                        $ordered_products_arr = [];

                        $total_price = 0;
                        foreach ($ordered_products as $op) {

                            $product = new Product();
                            $product = $product->where(["id"=>$op->product])->one("title, image_name");

                            $total_price += $op->price * $op->quantity;
                            $ordered = new Ordered_Product();
                            $ordered->price = $op->price;
                            $ordered->quantity = $op->quantity;
                            $ordered->create_property("product_title", $product->title);
                            $ordered->create_property("product_image", $product->image_name);

                            $inventory = new Inventory();
                            $inventory = $inventory->where(["id"=>$op->inventory])->one();

                            $inventory_arr = explode(",", $inventory->attributes);
                            $bought_attr = "";

                            foreach($inventory_arr as $attr_val){
								if(!empty($attr_val)){
									 $attr_arr = explode("-", $attr_val);
									$attr = new Attribute();
									$attr = $attr->where(["id"=>$attr_arr[0]])->one();

									$attr_val = new Attribute_Value();
									$attr_val = $attr_val->where(["id"=>$attr_arr[1]])->one();

									if(!empty($attr) && !empty($attr_val)) $bought_attr .= $attr->title . " : " . $attr_val->title . ", ";
								}
                            }
                            $bought_attr = rtrim($bought_attr, ", ");
                            $ordered->create_property("attribute", $bought_attr);
                            array_push($ordered_products_arr, $ordered->to_valid_array());
                        }

                        $order->create_property("tax", ($order->amount - $total_price));
                        $order->create_property("ordered_products", $ordered_products_arr);
                        array_push($order_arr, $order->to_valid_array());
                    }

                    $response->create(200, "Success.", $order_arr);
                }else $response->create(200, "No Item Found.", []);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>