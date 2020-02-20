<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{
    $errors = new Errors();
    $message = new Message();
    $response = new Response();
    $attribute_value = new Attribute_Value();

    if (Helper::is_post()) {
        $attribute_value->admin_id = $_POST['admin_id'];

        if(empty($_POST['id'])){
            $attribute_value->title = $_POST['title'];
            $attribute_value->attribute = $_POST['attribute_id'];
            

            $attribute_value->validate_except(["id", "group_by"]);
            $errors = $attribute_value->get_errors();

            if($errors->is_empty()){
                $attribute_value->id = $attribute_value->save();
                if( $attribute_value->id > 0) $message->set_message("Attribute Value Created Successfully");
            }

            $ajax_request = isset($_POST["ajax_request"]) ? true : false;

            if($ajax_request){
                if(!$message->is_empty()) $response->create(200, "Success", $attribute_value->to_valid_array());
                else if(!$errors->is_empty())  $response->create(201, $errors->format(), null);

                echo $response->print_response();
            }else{
                if(!$message->is_empty()){
                    Session::set_session($message);
                    Helper::redirect_to("../../public/attributes.php");
                }else if(!$errors->is_empty()){
                    Session::set_session($errors);
                    Helper::redirect_to("../../public/attribute-form.php");
                }
            }
        }else if(!empty($_POST['id'])){

            $attribute_value->id = $_POST['id'];
            $attribute_value->title = $_POST['title'];
            $attribute_value->attribute = $_POST['attribute_id'];

            $attribute_value->validate_with(["id", "title"]);
            $errors = $attribute_value->get_errors();


            if($errors->is_empty()){
                $updated_attribute_value = new Attribute_Value();
                $updated_attribute_value->title = $attribute_value->title;
                if($updated_attribute_value->where(["id"=>$attribute_value->id])->update()){
                    $message->set_message("Attribute Value Updated Successfully");
                }
            }

            $ajax_request = isset($_POST["ajax_request"]) ? true : false;

            if($ajax_request){
                if(!$message->is_empty()) $response->create(200, "Success", $attribute_value->to_valid_array());
                else if(!$errors->is_empty())  $response->create(201, $errors->format(), null);

                echo $response->print_response();
            }else{
                if(!$message->is_empty()){
                    Session::set_session($message);
                    Helper::redirect_to("../../public/attributes.php");
                }else if(!$errors->is_empty()){
                    Session::set_session($errors);
                    Helper::redirect_to("../../public/attribute-form.php?id=" . $attribute->id);
                }

            }
        }
    }else if (Helper::is_get()){
        $attribute_value->id = Helper::get_val('id');
        $attribute_value->admin_id = Helper::get_val('admin_id');

        if(!empty($attribute_value->admin_id) && !empty($attribute_value->id)){
            if($admin->id == $attribute_value->admin_id){

                $attribute_value_from_db = new Attribute_Value();
                $attribute_value_from_db = $attribute_value_from_db->where(["id" => $attribute_value->id])->one();

                if(count($attribute_value_from_db) > 0){
                    if($attribute_value->where(["id" => $attribute_value->id])->andWhere(["admin_id" => $attribute_value->admin_id])->delete()){

                        $message->set_message("Successfully Deleted.");

                    }else  $errors->add_error("Error Occurred While Deleting");
                }else  $errors->add_error("Invalid Attribute Value");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Parameters.");

        $request_via_ajax = (isset($_GET['request_via_ajax'])) ? true : false;

        if($request_via_ajax){
            if(!$message->is_empty()) $response->create(200, "Success", $attribute_value->to_valid_array());
            else if(!$errors->is_empty())  $response->create(201, $errors->format(), null);

            echo $response->print_response();
        }else{
            if(!$message->is_empty()) Session::set_session($message);
            else Session::set_session($errors);

            Helper::redirect_to("../../public/attributes.php");
        }
    }
}

?>