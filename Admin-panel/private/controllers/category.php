<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{

    $errors = new Errors();
    $message = new Message();
    $category = new Category();

    if (Helper::is_post()) {
        $category->admin_id = $_POST['admin_id'];

        if(empty($_POST['id'])){
            $category->title = $_POST['title'];
            $category->status = (isset($_POST['status'])) ? 1 : 2;

            $category->image_name = $_FILES["image_name"]["name"];

            $category->validate_except(["id", "image_resolution", "group_by"]);
            $errors = $category->get_errors();

            if($errors->is_empty()){
                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()) {
                        $category->image_name = $upload->get_file_name();
                        $category->image_resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    $id = $category->save();
                    $has_error_creation = false;
                    if($id){

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

                                if(!$item_images->save()) {
                                    $has_error_creation = true;
                                    $errors->add_error($item_images->image_name . " failed to upload.");
                                }
                            }
                        }

                        if(!$has_error_creation) $message->set_message("Category Created Successfully");

                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/categories.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/category-form.php");
            }

        }else if(!empty($_POST['id'])){
            $category->id = $_POST['id'];
            $category->title = $_POST['title'];
            $category->status = (isset($_POST['status'])) ? 1 : 2;
            $category->image_name = $_POST['prev_image'];


            $category->validate_except(["image_name", "image_resolution", "group_by"]);
            $errors = $category->get_errors();

            if($errors->is_empty()){

                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()){
                        $upload->delete($category->image_name);
                        $category->image_name = $upload->get_file_name();
                        $category->image_resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    if($category->where(["id"=>$category->id])->andWhere(["admin_id" => $category->admin_id])->update()){

                        $has_error_updating = false;
                        $removed_image_ids = Helper::post_val("removed-image-ids");

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
                                $item_images->resolution = Upload::get_image_resolution($item);
                                $item_images->image_name = $item;
                                $item_images->admin_id = $admin->id;
                                $item_images->item_id = $category->id;

                                if(!$item_images->save()) {
                                    $has_error_updating = true;
                                    $errors->add_error($item_images->image_name . " failed to upload.");
                                }
                            }
                        }

                        if(!$has_error_updating) $message->set_message("Category Updated Successfully");
                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/categories.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/category-form.php?id=" . $category->id);
            }
        }
    }else if (Helper::is_get()) {
        $category->id = Helper::get_val('id');
        $category->admin_id = Helper::get_val('admin_id');

        if(!empty($category->admin_id) && !empty($category->id)){
            if($admin->id == $category->admin_id){

                $category_from_db = new Category();
                $category_from_db = $category_from_db->where(["id" => $category->id])->one();
                if(count($category_from_db) > 0){
                    $image = $category_from_db->image_name;

                    if($category->where(["id" => $category->id])->andWhere(["admin_id" => $category->admin_id])->delete()){

                        
                        Upload::delete($image);

                        $products_of_cat = new Product();
                        $products_of_cat = $products_of_cat->where(["category"=>$category->id])->all();
                        
                        foreach ($products_of_cat as $p_item){

                            $item_images = new Item_Image();
                            $item_images = $item_images->where(["item_id" => $p_item->id])->all();

                            foreach ($item_images as $p_image_item){
                                Upload::delete($p_image_item->image_name);
                            }

                            $new_item_image = new Item_Image();
                            $new_item_image->where(["item_id" => $p_item->id])->delete();
                            
                            $inventory = new Inventory();
                            $inventory = $inventory->where(["product"=>$p_item->id])->delete();
                        }

                        $new_products_of_cat = new Product();
                        $new_products_of_cat->where(["category"=>$category->id])->delete();

                        $message->set_message("Successfully Deleted.");

                    }else  $errors->add_error("Error Occurred While Deleting");
                }else  $errors->add_error("Invalid Category");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Parameters.");

        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/categories.php");
    }
}

?>