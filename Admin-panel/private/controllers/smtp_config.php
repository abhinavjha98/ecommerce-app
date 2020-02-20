<?php require_once('../init.php'); ?>

<?php

if(Helper::is_post()){
    $errors = new Errors();
    $message = new Message();
    $smtp_config = new Smtp_Config();
    $smtp_config->id = $_POST["id"];
    $smtp_config->admin_id = $_POST["admin_id"];
    $smtp_config->host = $_POST["host"];
    $smtp_config->sender_email = $_POST["sender_email"];
    $smtp_config->username = $_POST["username"];
    $smtp_config->password = $_POST["password"];
    $smtp_config->port = $_POST["port"];
    $smtp_config->encryption = $_POST["encryption"];

    $smtp_config->validate_except(["group_by"]);
    $errors = $smtp_config->get_errors();

    $type["type"] = "smtp_config";
    Session::set_session($type);

    if($errors->is_empty()){
        if($smtp_config->where(["id" => $smtp_config->id])->andWhere(["admin_id" => $smtp_config->admin_id])->update()){
            $message->set_message("SMTP Configuration Successfully Updated");
        }
    }

    if(!$message->is_empty()) Session::set_session($message);
    else if(!$errors->is_empty()) Session::set_session($errors);
    
    Helper::redirect_to("../../public/site-config.php");
}

?>