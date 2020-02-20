<?php require_once('../init.php'); ?>

<?php

$errors = new Errors();
$message = new Message();

if (Helper::is_post()) {

    $site_config = new Site_Config();
    $site_config->firebase_auth = Helper::post_val("firebase_auth");
    $pus_noti = new Push_Notification();
    $pus_noti->title = Helper::post_val("title");
    $pus_noti->message = Helper::post_val("message");

    $pus_noti->validate_with(["message", "title"]);
    $errors = $pus_noti->get_errors();

    $site_config->validate_with(["firebase_auth"]);
    $errors->add_errors($site_config->get_errors());

    $type["type"]= "push_notification";
    Session::set_session($type);

    if($errors->is_empty()){
        $url = 'https://fcm.googleapis.com/fcm/send';

        $mes['title'] = $pus_noti->title;
        $mes['desc'] = $pus_noti->message;

        $fields = array(
            'to' => '/topics/message',
            'data' => array(
                "message" => $mes,
            )
        );
        
        $fields = json_encode($fields);

        $headers = array(
            'Authorization:key =' . $site_config->firebase_auth,
            'Content-Type: application/json',
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);

        $result = curl_exec($ch);

        curl_close($ch);
        $res_data = json_decode($result);

        if(!empty($res_data)) $message->set_message("Notified Successfully.");
        else $errors->add_error("Something Went Wrong.");
    }

    if(!$message->is_empty()) Session::set_session($message);
    else Session::set_session($errors);

    Helper::redirect_to("../../public/push-notifications.php");

}

?>