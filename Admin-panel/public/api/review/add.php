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
            $review->item_id = Helper::post_val("item_id");
            $review->user_id = Helper::post_val("user_id");
            $review->admin_id = $setting->admin_id;
            $review->rating = Helper::post_val("rating");
            $review->review = Helper::post_val("review");
            $review->id = Helper::post_val("id");

            $review_images = [];
			
			
			$error = "Something Went Wrong";

            if($review->item_id && $review->user_id && $review->admin_id && $review->rating){
				
                if($review->id){
                    if($review->where(["id" => $review->id])->update()) $response->create(200, "Success.", $review->to_valid_array());
                    else $response->create(201, "Something Went Wrong. Please try Again.", null);


                }else{
                    
					$success = true;

					

                    if(isset($_FILES["images"]["name"])){
						
                            for($i = 0; $i < count($_FILES["images"]["name"]); $i++){
                                $img["name"] = $_FILES["images"]["name"][$i];
                                $img["type"] = $_FILES["images"]["type"][$i];
                                $img["tmp_name"] = $_FILES["images"]["tmp_name"][$i];
                                $img["error"] = $_FILES["images"]["error"][$i];
                                $img["size"] = $_FILES["images"]["size"][$i];

                                $upload = new Upload($img);
								
								$review_image = new Review_Image();
								
								if($upload->upload()) {
									$review_image->image_name = $upload->get_file_name();
									$review_image->resolution = $upload->get_resolution();
								}									
	
								$errors = $upload->get_errors();
								
								if($errors->is_empty()) array_push($review_images, $review_image);
								else {
									
									$success = false;
									$error = $upload->get_errors()->to_sting();
								}
                            }	
                    }
					
					
					if($success){
						$review->id = $review->save();
						$inserted_review_images=  [];
						foreach($review_images as $item){
							
							$new_review_image = new Review_Image();
							$new_review_image->item_id = $review->item_id;
							$new_review_image->review_id = $review->id;
							$new_review_image->admin_id = $setting->admin_id;
							$new_review_image->image_name = $item->image_name;
							$new_review_image->resolution = $item->resolution;
							
							$new_review_image->id = $new_review_image->save();
							
							array_push($inserted_review_images, $new_review_image);
						}
						
						$review->create_property("review_images", $inserted_review_images);
						
						if($review->id) $response->create(200, "Success.", $review->to_valid_array());
						else $response->create(201, $error, null);
					
					} else $response->create(201, $error, null);

                }

            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);

echo $response->print_response();

?>