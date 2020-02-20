<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{

    $errors = new Errors();
    $message = new Message();
    $attribute = new Attribute();

    if (Helper::is_post()) {
        $attribute->admin_id = trim($_POST['admin_id']);

        if(empty($_POST['id'] && !empty($attribute->admin_id))){
            $attribute->title = trim($_POST['title']);

            $attribute->validate_except(["id", "group_by"]);
            $errors = $attribute->get_errors();

            $attribute_value = trim($_POST['attribute_values']);
            if(empty($attribute_value)) $errors->add_error("Attributes value can't be empty.");
            else{
                $attribute_values = array_map("trim", explode(",", $attribute_value));
                $attribute_values = array_filter($attribute_values, function($value) { return $value !== ''; });


                if(empty($attribute_values)) $errors->add_error("Attributes value can't be empty.");
            }

            if($errors->is_empty()){
                $attr_id = $attribute->save();
                if(!empty($attr_id) && ($attr_id > 0)){

                    foreach ($attribute_values as $av){
                        if(!empty($av)){
                            $attr_val = new Attribute_Value();
                            $attr_val->attribute = $attr_id;
                            $attr_val->admin_id = $attribute->admin_id;
                            $attr_val->title = trim($av);
                            $attr_val->save();
                        }
                    }
                    $message->set_message("Attribute Created Successfully");
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/attributes.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/attribute-form.php");
            }

        }else if(!empty($_POST['id']) && !empty($attribute->admin_id)) {
            $attribute->id = trim($_POST['id']);
            $attribute->title = trim($_POST['title']);
            $errors = $attribute->get_errors();

            $attribute_value = trim($_POST['attribute_values']);
            if(empty($attribute_value)) $errors->add_error("Attributes value can't be empty.");
            else{
                $attr_val_arr = array_map("trim", explode(",", $attribute_value));
                $attr_val_arr = array_filter($attr_val_arr, function($value) { return $value !== ''; });
                if(empty($attr_val_arr)) $errors->add_error("Attributes value can't be empty.");
            }

            if($errors->is_empty()){
                if($attribute->where(["id"=>$attribute->id])->andWhere(["admin_id" => $attribute->admin_id])->update()){

                    $attr_val_db = new Attribute_Value();
                    $attr_val_db = $attr_val_db->where(["attribute"=>$attribute->id])->all();
                    $attr_val_db_arr = [];
                    foreach ($attr_val_db as $attr_val_db_item) {
                        array_push($attr_val_db_arr, $attr_val_db_item->title);
                    }

                    sort($attr_val_db_arr);
                    sort($attr_val_arr);

                    if($attr_val_db_arr != $attr_val_arr){
                        $updated_attr_val = new Attribute_Value();
                        $updated_attr_val->where(["attribute"=>$attribute->id])->delete();

                        foreach ($attr_val_arr as $item) {
                            $new_attr_val = new Attribute_Value();
                            $new_attr_val->admin_id = $attribute->admin_id;
                            $new_attr_val->attribute = $attribute->id;
                            $new_attr_val->title = trim($item);
                            $new_attr_val->save();
                        }
                    }

                    $message->set_message("Attribute Updated Successfully");
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/attributes.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/attribute-form.php?id=" . $attribute->id);
            }
        }
    }else if (Helper::is_get()) {
        $attribute->id = Helper::get_val('id');
        $attribute->admin_id = Helper::get_val('admin_id');

        if(!empty($attribute->admin_id) && !empty($attribute->id)){
            if($admin->id == $attribute->admin_id){

                $attribute_from_db = new Attribute();
                $attribute_from_db = $attribute_from_db->where(["id" => $attribute->id])->one();
                if(count($attribute_from_db) > 0){

                    if($attribute->where(["id" => $attribute->id])->andWhere(["admin_id" => $attribute->admin_id])->delete()){

                        $attr_val = new Attribute_Value();
                        $attr_val->where(["attribute" => $attribute->id])->delete();
                        $message->set_message("Successfully Deleted.");

                    }else  $errors->add_error("Error Occurred While Deleting");
                }else  $errors->add_error("Invalid Attribute");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Parameters.");

        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/attributes.php");
    }
}

?>