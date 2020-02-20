<?php require_once('../init.php'); ?>

<?php

if (Helper::is_post()) {
    $errors = new Errors();
    $message = new Message();
    $payment = new Payment();

    $payment->id = $_POST['id'];
    $payment->admin_id = $_POST['admin_id'];


    $type = [];
    if(isset($_POST['environment'])){
        $type["type"]= "braintree";
        Session::set_session($type);

        $payment->environment = $_POST['environment'];
        $payment->merchant_id = $_POST['merchant_id'];
        $payment->public_key = $_POST['public_key'];
        $payment->private_key = $_POST['private_key'];



        $payment->validate_except(["group_by"]);
        $errors = $payment->get_errors();

        if($errors->is_empty()){

            $new_payment = clone $payment;
            $new_payment->id = null;
            if($new_payment->where(["id"  => $payment->id])->update()){
                $message->set_message("Braintree Updated");
            }
        }

        if(!$message->is_empty()) Session::set_session($message);
        else Session::set_session($errors);

        Helper::redirect_to("../../public/payment.php");

    }
}

?>