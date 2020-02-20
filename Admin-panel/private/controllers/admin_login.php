<?php require_once('../init.php'); ?>

<?php

if (Helper::is_post()) {
    $admin = new Admin();
    $admin->username = $_POST['username'];
    $admin->password = $_POST['password'];

    $errors = new Errors();
    if(empty($admin->username)){
        $errors->add_error("Username can't be blank");
    }
    if(empty($admin->password)){
        $errors->add_error("Password can't be blank");
    }

    $success = false;
    if($errors->is_empty()){
        $admin = $admin->verify_login();

        if($admin != null){
            $admin->password = null;
            $admin->id = (int) $admin->id;
            Session::set_session($admin);
            $success = true;
        }else{
            $errors->add_error("Invalid Username/Password");
        }
    }

    if($success)  Helper::redirect_to("../../public/index.php");
    else{
        Session::set_session($errors);
        Helper::redirect_to("../../public/login.php");
    }
}else Helper::redirect_to("../../public/login.php");

?>