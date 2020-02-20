<?php require_once('../init.php'); ?>

<?php

    if(Helper::is_post()){
        $errors = new Errors();
        $message = new Message();

        $admobs = new Admob();
        $admobs->id = $_POST["id"];
        $admobs->admin_id = $_POST["admin_id"];

        $type = [];

        if(isset($_POST["banner_id"])){

            $admobs->banner_status = (isset($_POST['banner_status'])) ? 1 : 2;
            $admobs->banner_id = $_POST["banner_id"];
            $admobs->banner_unit_id = $_POST["banner_unit_id"];

            $admobs->validate_with(["id", "banner_id", "banner_unit_id"]);
            $errors = $admobs->get_errors();

            $type["type"]= "banner_ad";

            if($errors->is_empty()){
                if($admobs->where(["id" => $admobs->id])->andWhere(["admin_id" => $admobs->admin_id])->update()){
                    $message->set_message("Banner admob Successfully Updated");
                }
            }

        }else if (isset($_POST["interstitial_id"])){

            $admobs->interstitial_status = (isset($_POST['interstitial_status'])) ? 1 : 2;
            $admobs->interstitial_id = $_POST["interstitial_id"];
            $admobs->interstitial_unit_id = $_POST["interstitial_unit_id"];

            $admobs->validate_with(["id", "interstitial_id", "interstitial_unit_id"]);
            $errors = $admobs->get_errors();

            $type["type"]= "interstitial_ad";

            if($errors->is_empty()){
                if($admobs->where(["id" => $admobs->id])->andWhere(["admin_id" => $admobs->admin_id])->update()){
                    $message->set_message("Interstitial admob Successfully Updated");
                }
            }

        }else if(isset($_POST["video_id"])){

            $admobs->video_status = (isset($_POST['video_status'])) ? 1 : 2;
            $admobs->video_id = $_POST["video_id"];
            $admobs->video_unit_id = $_POST["video_unit_id"];

            $admobs->validate_with(["id", "video_id", "video_unit_id"]);
            $errors = $admobs->get_errors();

            $type["type"]= "video_ad";

            if($errors->is_empty()){
                if($admobs->where(["id" => $admobs->id])->andWhere(["admin_id" => $admobs->admin_id])->update()){
                    $message->set_message("Video admob Successfully Updated");
                }
            }
        }

        Session::set_session($type);

    }

    if(!$message->is_empty()) Session::set_session($message);
    else Session::set_session($errors);
    
    Helper::redirect_to("../../public/admob.php");


?>