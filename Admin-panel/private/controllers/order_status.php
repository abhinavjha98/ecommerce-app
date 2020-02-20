<?php require_once('../init.php'); ?>
<?php

$response = new Response();
$admin = Session::get_session(new Admin());
if(!empty($admin)){

    if (Helper::is_get()) {
        $id = Helper::get_val("id");
        $admin_id = Helper::get_val("admin_id");
        $status = Helper::get_val("status");

        if($id && $status && $admin_id){
            if($admin_id == $admin->id){
                $order = new Orders();
                $order->status = $status;
                if($order->where(["id"=>$id])->update()){

                    $response->create(200, "Success.", null);

                }else $response->create(201, "Something Went Wrong please try Again.", null);
            }else $response->create(201, "Please Login First", null);
        }else $response->create(201, "Invalid Parameter", null);
    }else $response->create(201, "Invalid Request Method", null);
}else $response->create(201, "Please Login First", null);

echo $response->print_response();

?>
