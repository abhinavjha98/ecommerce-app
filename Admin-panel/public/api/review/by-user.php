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

            if($review->user_id){
                $page = Helper::post_val("page");
                if($page){
                    $start = ($page - 1) * API_PAGINATION;
                    $reviews = $review->where(["user_id" => $review->user_id])
                        ->orderBy("id")->desc()->limit($start, API_PAGINATION)->all();
                }else $reviews = $review->where(["user_id" => $review->user_id])->orderBy("id")->desc()->all();

                if (count($reviews) > 0) {
                    foreach ($reviews as $item){
                        $user = new User();
                        $user = $user->where(["id"=>$item->user_id])->one();
                        if(!empty($user)){
                            $item->create_property("user_name" , $user->username);
                            $item->create_property("image_name", $user->image_name);
                        }else{
                            $item->create_property("user_name", "Unknown");
                            $item->create_property("image_name", PROFILE_DEFAULT);
                        }
                        $review_images = new Review_Image();
                        $review_images = $review_images->where(["review_id"=>$item->id])->all();
                        $item->create_property("review_images", $review_images);
						
						if($item->review == null) $item->review = "";
						
                    }

                    $response->create(200, "Success.", $reviews);
                }else $response->create(200, "No Item Found.", []);

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>