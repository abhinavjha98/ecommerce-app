<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{

    $errors = new Errors();
    $message = new Message();
    $product = new Product();

    if (Helper::is_post()) {
        $product->admin_id = trim($_POST['admin_id']);
        
        if(empty($_POST['id'])){
            $product->title = trim($_POST['title']);
            $product->status = (isset($_POST['status'])) ? 1 : 2;
            $product->purchase_price = trim($_POST['purchase_price']);
            $product->prev_price = trim($_POST['prev_price']);
            $product->current_price = trim($_POST['current_price']);
            $product->description = trim($_POST['description']);
            $product->tags = trim($_POST['tags']);
            $product->category = trim($_POST['category']);
            $single_inventory = trim($_POST['inventory']);
            $product->featured = (isset($_POST['featured'])) ? 1 : 2;

            if(!$product->prev_price) $product->prev_price = -1;
            $product->image_name = $_FILES["image_name"]["name"];

            $product->validate_except(["id", "image_resolution", "sell", "inventory", "prev_price", "group_by"]);
            $errors = $product->get_errors();

            $has_qty_error = false;
            $inventory = [];

            foreach($_POST as $post_item => $qty){
                if(strpos($post_item, "qty") !== false){
                    $post_item = str_replace("qty", "", $post_item);
                    $post_item = str_replace("_", ",", $post_item);
                    if(empty($qty)) $has_qty_error = true;
                    else{
                        $inventory[$post_item] = $qty;
                    }
                }
            }

            if(count($inventory) > 0){
                if($has_qty_error) $errors->add_error("Quantity can't be empty");
            }else{
                if(empty($single_inventory)) $errors->add_error("Inventory can't be empty");
            }

            if($errors->is_empty()){
                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()) {
                        $product->image_name = $upload->get_file_name();
                        $product->image_resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    $id = $product->save();
                    $has_error_creation = false;
                    if($id){
                        if(!empty($inventory)){
                            foreach ($inventory as $key => $value){
                                $inventory = new Inventory();
                                $inventory->attributes = $key;
                                $inventory->product = $id;
                                $inventory->quantity = $value;

                                if(!$inventory->save()){
                                    $has_error_creation = true;
                                    $errors->add_error("Something went wrong. Please Try Again.");
                                }
                            }
                        }elseif(!empty($single_inventory)){
                            $inventory = new Inventory();
                            $inventory->product = $id;
                            $inventory->quantity = $single_inventory;
                            if(!$inventory->save()){
                                $has_error_creation = true;
                                $errors->add_error("Something went wrong. Please Try Again.");
                            }
                        }

                        $uploaded_image_names = Helper::post_val("uploaded-image-names");
                        if($uploaded_image_names){
                            $image_names = explode(",", $uploaded_image_names);
                            if(empty(trim($image_names[count($image_names)-1]))) array_splice($image_names, count($image_names)-1, 1);

                            foreach ($image_names as $item){
                                $item_images = new Item_Image();
                                $resolution = Upload::get_image_resolution($item);
                                $item_images->resolution = $resolution[0] . ":" . $resolution[1];
                                $item_images->image_name = $item;
                                $item_images->admin_id = $admin->id;
                                $item_images->item_id = $id;

                                $product_id = $item_images->save();

                                if(!$product_id) {
                                    $has_error_creation = true;
                                    $errors->add_error($item_images->image_name . " failed to upload.");
                                }
                            }
                        }

                        if(!$has_error_creation) $message->set_message("Product Created Successfully");
                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/products.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/product-form.php");
            }

        }else if(!empty($_POST['id'])){
            $product->id = trim($_POST['id']);
            $product->title = trim($_POST['title']);
            $product->status = (isset($_POST['status'])) ? 1 : 2;
            $product->image_name = trim($_POST['prev_image']);

            $product->purchase_price = trim($_POST['purchase_price']);
            $product->prev_price = trim($_POST['prev_price']);
            $product->current_price = trim($_POST['current_price']);
            $product->category = trim($_POST['category']);

            $single_inventory = trim($_POST['inventory']);

            $product->description = trim($_POST['description']);
            $product->tags = trim($_POST['tags']);
            $product->featured = (isset($_POST['featured'])) ? 1 : 2;

            if(!$product->prev_price) $product->prev_price = -1;

            $inventory = [];

            $has_qty_error = false;

            if(!empty($_POST["prev_attr"])){
                foreach($_POST as $post_item => $qty){
                    if(strpos($post_item, "qty") !== false){
                        $post_item = str_replace("qty", "", $post_item);
                        $post_item = str_replace("_", ",", $post_item);

                        if(empty($qty)) $has_qty_error = true;
                        else $inventory[$post_item] = $qty;
                    }
                }
            }

            $product->validate_except(["image_name", "image_resolution", "sell", "inventory", "prev_price", "group_by"]);
            $errors = $product->get_errors();

            if(count($inventory) > 0){
                $single_inventory = 0;
                if($has_qty_error) $errors->add_error("Quantity can't be empty");
            }else{

                $has_multiple_attr = false;
                foreach($_POST as $post_item => $qty){
                    if(strpos($post_item, "qty") !== false){
                        $has_multiple_attr = true;
                        break;
                    }
                }

                if(!$has_multiple_attr && empty($single_inventory)) $errors->add_error("Inventory can't be empty");
            }

            if($errors->is_empty()){

                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()){
                        $upload->delete($product->image_name);
                        $product->image_name = $upload->get_file_name();
                        $product->image_resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    if($product->where(["id"=>$product->id])->andWhere(["admin_id" => $product->admin_id])->update()){

                        $has_error_updating = false;
                        $removed_image_ids = Helper::post_val("removed-image-ids");

                        if(!empty($inventory)){
                            foreach ($inventory as $key => $value){
                                $inventory = new Inventory();
                                $inventory->attributes = $key;
                                $inventory->product = $product->id;
                                $inventory->quantity = $value;
                                if(!$inventory->save()){
                                    $has_error_creation = true;
                                    $errors->add_error("Something went wrong. Please Try Again.");
                                }
                            }

                        }

                        if(!empty($_POST["prev_attr"])){
                            $inventory_ids = explode(",", $_POST["prev_attr"]);
                            foreach ($inventory_ids as $inventory_id) {
                                $inventory = new Inventory();
                                $inventory->where(["id"=>$inventory_id])->delete();
                            }
                        }


                        if(!empty($single_inventory)){

                            $new_inven = new Inventory();
                            $new_inven->quantity = $single_inventory;

                            $new_invent_from_db = new Inventory();
                            $new_invent_from_db = $new_invent_from_db->where(["product"=>$product->id])->one();

                            if(empty($new_invent_from_db)){
                                $new_inven->product = $product->id;
                                if(!$new_inven->save()){
                                    $has_error_creation = true;
                                    $errors->add_error("Something went wrong. Please Try Again.");
                                }
                            }else{
                                if(!$new_inven->where(["product"=>$product->id])->update()){
                                    $has_error_creation = true;
                                    $errors->add_error("Something went wrong. Please Try Again.");
                                }
                            }
                        }


                        if($removed_image_ids){
                            $removed_id_arr = $image_names = explode(",", $removed_image_ids);
                            if(empty(trim($removed_id_arr[count($removed_id_arr)-1]))) array_splice($removed_id_arr, count($removed_id_arr)-1, 1);

                            foreach ($removed_id_arr as $item){
                                $item_images = new Item_Image();
                                if(!$item_images->where(["id"=>$item])->delete()) {
                                    $has_error_updating = true;
                                    $errors->add_error($item . " failed to delete.");
                                }
                            }
                        }

                        $uploaded_image_names = Helper::post_val("uploaded-image-names");

                        if($uploaded_image_names){
                            $image_names = explode(",", $uploaded_image_names);
                            if(empty(trim($image_names[count($image_names)-1]))) array_splice($image_names, count($image_names)-1, 1);

                            foreach ($image_names as $item){
                                $item_images = new Item_Image();

                                $resolution = Upload::get_image_resolution($item);
                                $item_images->resolution = $resolution[0] . ":" . $resolution[1];

                                $item_images->image_name = $item;
                                $item_images->admin_id = $admin->id;
                                $item_images->item_id = $product->id;

                                if(!$item_images->save()) {
                                    $has_error_updating = true;
                                    $errors->add_error($item_images->image_name . " failed to upload.");
                                }
                            }
                        }

                        if(!$has_error_updating) $message->set_message("Product Updated Successfully");
                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/products.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/product-form.php?id=" . $product->id);
            }
        }
    }else if (Helper::is_get()) {
        $product->id = Helper::get_val('id');
        $product->admin_id = Helper::get_val('admin_id');

        if(!empty($product->admin_id) && !empty($product->id)){
            if($admin->id == $product->admin_id){

                $product_from_db = new Product();
                $product_from_db = $product_from_db->where(["id" => $product->id])->one();
                if(count($product_from_db) > 0){
                    $image = $product_from_db->image_name;

                    if($product->where(["id" => $product->id])->andWhere(["admin_id" => $product->admin_id])->delete()){
                        Upload::delete($image);

                        $image_from_db = new Item_Image();
                        $image_from_db = $image_from_db->where(["item_id"=>$product->id])->all();
                        if(count($image_from_db) > 0){
                            foreach($image_from_db as $item){
                                $item_image_name = $item->image_name;
                                $item_image = new Item_Image();
                                if($item_image->where(["id" => $item->id])->andWhere(["admin_id" => $product->admin_id])->delete()){
                                    Upload::delete($item_image_name);
                                }
                            }
                        }

                        $inventory = new Inventory();
                        $inventory = $inventory->where(["product"=>$product->id])->delete();

                        $message->set_message("Successfully Deleted.");

                    }else  $errors->add_error("Error Occurred While Deleting");
                }else  $errors->add_error("Invalid Product");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Parameters.");

        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/products.php");
    }
}

?>