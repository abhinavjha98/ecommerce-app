<?php require_once('../../../private/init.php'); ?>

<?php

$response = new Response();
$errors = new Errors();

if(Helper::is_post()){
    $api_token = Helper::post_val("api_token");
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();

        if(!empty($setting)){
            $review = new Review();
            $review->user_id = Helper::post_val("user_id");
            $review->item_id = Helper::post_val("item_id");

            if($review->item_id && $review->user_id){
                $db_review = new Review();
                $db_review = $db_review->where(["user_id" => $review->user_id])->andWhere(["item_id" => $review->item_id])->one();

                if(!empty($db_review->id)){
                    if($review->where(["user_id" => $review->user_id])->andWhere(["item_id" => $review->item_id])->delete()){

                        $review_image = new Review_Image();
                        $review_image->where(["review_id" => $db_review->id])->andWhere(["item_id" => $review->item_id])->delete();

                        $response->create(200, "Success.", $review->to_valid_array());
                    }else $response->create(201, "Something Wnt Wrong. Please try Again.", null);
                }else $response->create(201, "Invalid Review.", null);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>