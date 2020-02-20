<?php require_once('../init.php'); ?>

<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{
    $type["type"] = "push_notification";
    Session::set_session($type);
    
    $errors = new Errors();
    $message = new Message();
    $push_notification = new Push_Notification();

    if (Helper::is_post()) {

        $push_notification->id = $_POST['id'];
        $push_notification->admin_id = $_POST['admin_id'];
        $push_notification->title = $_POST['title'];
        $push_notification->message = $_POST['message'];

        if(!empty($push_notification->id)){

            $push_notification->validate();
            $errors = $push_notification->get_errors();
            if($errors->is_empty()){
                $new_push_notification = clone $push_notification;
                if($push_notification->where(["id" => $push_notification->id])->update()){
                    $message->set_message("Successfully Added.");
                }
            }
        }else{
            $push_notification->validate_except(["id", "group_by"]);
            $errors = $push_notification->get_errors();

            if($errors->is_empty()){
                if($push_notification->save()){
                    $message->set_message("Successfully Added.");
                }
            }
        }

        if(!$message->is_empty()){
            Session::set_session($message);
            Helper::redirect_to("../../public/push-notifications.php");
        }else{
            Session::set_session($errors);
            Helper::redirect_to("../../public/push-notification-form.php");
        }

    }else if(Helper::is_get()){

        $push_notification->id = Helper::get_val('id');
        $push_notification->admin_id = Helper::get_val('admin_id');
        if(!empty($push_notification->admin_id) && !empty($push_notification->id)){
            if($admin->id == $push_notification->admin_id){
                if($push_notification->where(["id" => $push_notification->id])->andWhere(["admin_id" => $push_notification->admin_id])->delete()){

                    $message->set_message("Successfully Deleted.");

                }else  $errors->add_error("Error Occurred While Deleting");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else  $errors->add_error("Invalid Notification.");
        
        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/push-notifications.php");
    }
}
