<?php require_once('../init.php'); ?>

<?php

    if(Helper::is_post()){
        $admin = new Admin();
        $admin->id = $_POST['id'];
        $admin->username = $_POST['username'];
        $admin->email = $_POST['email'];
        $admin->password = $_POST['password'];

        $new_pass = $_POST['new_pass'];
        $confirm_pass = $_POST['confirm_pass'];

        $errors = new Errors();
        $admin->validate_except(["group_by"]);
        $errors = $admin->get_errors();

        if(empty($new_pass) || empty($confirm_pass)) {
            $errors->add_error("New password can't be empty.");
        }else if($new_pass != $confirm_pass){
            $errors->add_error("New passwords didn't match.");
        }else{
            $err_msg = Helper::invalid_length("New Password", $new_pass, 6);
            if(!empty($err_msg)){
                $errors->add_error($err_msg);
            }else{
                $err_msg = Helper::invalid_length("New Password", $confirm_pass, 6);
                if(!empty($err_msg)){
                    $errors->add_error($err_msg);
                }
            }
        }

        $message = new Message();

        if($errors->is_empty()){
            $db_admin = $admin->where(["id" => $admin->id])->one();

            if(password_verify($admin->password, $db_admin->password)){

                $new_admin = clone $admin;
                $new_admin->id = null;
                $new_admin->password = $new_pass;
                
                if($new_admin->where(["id" => $admin->id])->update()) $message->set_message("Profile Successfully Updated");
                else $errors->add_error("Something Went Wrong. Please Try Again.");
            }else $errors->add_error("Wrong Password.");
        }

        $type["type"] = "admin_credentials";
        Session::set_session($type);
        
        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/profile.php");
    }
?>