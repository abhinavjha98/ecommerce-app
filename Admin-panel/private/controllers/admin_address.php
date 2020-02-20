<?php require_once('../init.php'); ?>
<?php

$admin = Session::get_session(new Admin());
if(empty($admin)){
    Helper::redirect_to("admin_login.php");
}else{

    $errors = new Errors();
    $message = new Message();
    $address = new Admin_Address();

    if (Helper::is_post()) {
        $address->admin_id = $_POST['admin_id'];

        if($address->admin_id != $admin->id) $errors->add_error("Please Login First.");
        else{
            if(!empty($_POST['id'])){
                $address->id = $_POST['id'];
                $address->company_name = $_POST['company_name'];
                $address->line_1 = $_POST['line_1'];
                $address->line_2 = $_POST['line_2'];
                $address->city = $_POST['city'];
                $address->zip = $_POST['zip'];
                $address->state = $_POST['state'];
                $address->country= $_POST['country'];

                $address->validate_except(["line_2", "group_by"]);
                $errors = $address->get_errors();

                if(empty($address->line_2)) $address->line_2 = "null";

                if($errors->is_empty()){
                    if($address->where(["id"=>$address->id])->andWhere(["admin_id" => $address->admin_id])->update()){
                        $message->set_message("Address Updated Successfully");
                    }
                }
            }
        }

        $type["type"] = "admin_address";
        Session::set_session($type);

        if(!$message->is_empty()) Session::set_session($message);
        else if(!$errors->is_empty()) Session::set_session($errors);

        Helper::redirect_to("../../public/profile.php");
    }
}

?>