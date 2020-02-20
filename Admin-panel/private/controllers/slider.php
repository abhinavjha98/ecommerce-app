<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{

    $errors = new Errors();
    $message = new Message();
    $slider = new Slider();

    if (Helper::is_post()) {
        $slider->admin_id = $_POST['admin_id'];

        if(empty($_POST['id'])){
            $slider->title = $_POST['title'];
            $slider->detail = $_POST['detail'];
            $slider->tag = $_POST['tag'];
            $slider->status = (isset($_POST['status'])) ? 1 : 2;

            $slider->image_name = $_FILES["image_name"]["name"];

            $slider->validate_except(["id", "resolution", "created"]);
            $errors = $slider->get_errors();

            if($errors->is_empty()){
                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()) {
                        $slider->image_name = $upload->get_file_name();
                        $slider->resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    $id = $slider->save();

                    if($id){

                        $message->set_message("Slider Created Successfully");

                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/sliders.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/slider-form.php");
            }

        }else if(!empty($_POST['id'])){
            $slider->id = $_POST['id'];
            $slider->title = $_POST['title'];
            $slider->detail = $_POST['detail'];
            $slider->tag = $_POST['tag'];
            $slider->status = (isset($_POST['status'])) ? 1 : 2;
            $slider->image_name = $_POST['prev_image'];


            $slider->validate_except(["image_name", "resolution", "created"]);
            $errors = $slider->get_errors();

            if($errors->is_empty()){

                if(!empty($_FILES["image_name"]["name"])){
                    $upload = new Upload($_FILES["image_name"]);
                    $upload->set_max_size(MAX_IMAGE_SIZE);
                    if($upload->upload()){
                        $upload->delete($slider->image_name);
                        $slider->image_name = $upload->get_file_name();
                        $slider->resolution = $upload->resolution;
                    }
                    $errors = $upload->get_errors();
                }

                if($errors->is_empty()){
                    if($slider->where(["id"=>$slider->id])->andWhere(["admin_id" => $slider->admin_id])->update()){

                        $message->set_message("Slider Updated Successfully");
                    }
                }
            }

            if(!$message->is_empty()){
                Session::set_session($message);
                Helper::redirect_to("../../public/sliders.php");
            }else if(!$errors->is_empty()){
                Session::set_session($errors);
                Helper::redirect_to("../../public/slider-form.php?id=" . $slider->id);
            }
        }
    }else if (Helper::is_get()) {
        $slider->id = Helper::get_val('id');
        $slider->admin_id = Helper::get_val('admin_id');

        if(!empty($slider->admin_id) && !empty($slider->id)){
            if($admin->id == $slider->admin_id){

                $slider_from_db = new Slider();
                $slider_from_db = $slider_from_db->where(["id" => $slider->id])->one();
                if(count($slider_from_db) > 0){
                    $image = $slider_from_db->image_name;

                    if($slider->where(["id" => $slider->id])->andWhere(["admin_id" => $slider->admin_id])->delete()){


                        $message->set_message("Successfully Deleted.");

                    }else  $errors->add_error("Error Occurred While Deleting");
                }else  $errors->add_error("Invalid Slider");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Parameters.");

        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/sliders.php");
    }
}

?>