<?php require_once('../init.php'); ?>

<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{
    if(Helper::is_get()){

        $errors = new Errors();
        $message = new Message();
        $user = new User();
        
        $user->id = $_GET['id'];
        $user->admin_id = $_GET['admin_id'];
        if(!empty($user->admin_id) && !empty($user->id)){
            if($admin->id == $user->admin_id){
                if($user->where(["id" => $user->id])->andWhere(["admin_id" => $user->admin_id])->delete()){

                    $message->set_message("Successfully Deleted.");

                }else $errors->add_error("Error Occurred While Deleting");
            }else $errors->add_error("You re only allowed to delete your own data.");
        }else $errors->add_error("Invalid Notification.");

        if(!$message->is_empty())  Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/users.php");
    }
}

?>