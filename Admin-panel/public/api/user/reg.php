<?php 


require_once('../../../private/init.php'); ?>
<?php


	
	
$response = new Response();
$send_mail = false;


					
					
if(Helper::is_post()){
	
	
					
    $api_token = Helper::post_val("api_token");
	
	
					
					
    if($api_token){
        $setting = new Setting();
        $setting = $setting->where(["api_token" => $api_token])->one();



					
					
        if(!empty($setting)){
			
			
			
					
					
            if(isset($_POST["email"]) && isset($_POST["password"]) && isset($_POST["type"])
                && isset($_POST["social_id"]) && isset($_POST["username"])){
				
					
					
					
	
				
                $user = new User();
                $user->email = Helper::post_val("email");
                $user->password = Helper::post_val("password");
                $user->type = Helper::post_val("type");
                $user->social_id = Helper::post_val("social_id");
                $user->username = Helper::post_val("username");
                $user->mp_status = Helper::post_val("mp_status");
                $user->admin_id = $setting->admin_id;


				
					
					
                if($user->type == EMAIL_USER || $user->type == MP_USER){
		
					
					
                    /*EMAIL LOGIN*/
                    $user->validate_with(["email", "password", "username"]);
                    $errors = $user->get_errors();
                    if($errors->is_empty()){

                        $user_from_db = $user->where(["email" => $user->email])->one();
                        if(empty($user_from_db)){


							
                            if(isset($_FILES["image"])) {
                                $uploaded_image = $_FILES["image"];
                                $upload = new Upload($uploaded_image);
                                $upload->set_max_size(MAX_IMAGE_SIZE);
                                if ($upload->upload()) {
                                    $user->image_name = $upload->get_file_name();
                                    $user->image_resolution = $upload->resolution;
                                }
                            }else{
                                $user->image_name = PROFILE_DEFAULT;
                                $user->image_resolution = DEFAULT_RESOLUTION;
                            }
		
                            $user->id = $user->save();
                            if(!empty($user->id)){

                                $response->create(200, "Success", $user->response()->to_valid_array());
                                $send_mail = true;
								$mailer = new Mailer($user);
                                if(!$mailer->send()) $response->create(201, "Something Went Wrong", null);

                            }else $response->create(201, "Something Went Wrong", null);
                        }else if($user_from_db->status != 1){
							
							
							
							
							
							
                            if($user->where(["id"=>$user_from_db->id])->update()){
                                $user->id = $user_from_db->id;


								

                                $response->create(200, "Success", $user_from_db->response()->to_valid_array());
                                $send_mail = true;
								$mailer = new Mailer($user);
								
								
                                if(!$mailer->send()) $response->create(201, "Something Went Wrong", null);

                            }else $response->create(201, "Something Went Wrong", null);

                        }else if($user_from_db->status == 1) $response->create(201, "You Already Have an Account", null);
                    }else $response->create(201, $errors->get_error_str(), null);
                }else if(($user->type == FACEBOOK_USER) || ($user->type == GOOGLE_USER)){
                    
					
					
					
					
                    /*SOCIAL LOGIN*/
                    $user->validate_with(["social_id"]);
                    $errors = $user->get_errors();
                    
                    if($errors->is_empty()){
						
						
					
                        $existing_user = $user->where(["type" => $user->type])->andWhere(["social_id" => $user->social_id])->one();
						
						
						
					
					
                        if(!empty($existing_user)){
	
						
                            $existing_user->verification_token = "";
                            $response->create(200, "Success", $existing_user->to_valid_array());
                        }else{

					$res["email"] = $_POST["email"];
					$res["password"] = $_POST["password"];
					$res["type"] = $_POST["type"];
					$res["social_id"] = $_POST["social_id"];
					$res["username"] = $_POST["username"];
					
						
						
                            
							
							echo json_encode($user->id);	
							die();
							
							$user->id = $user->save();
								
                            if(!empty($user->id)){
								
								
						
						
                                $response->create(200, "Success", $user->to_valid_array());
                            }else $response->create(201, "Something Went Wrong", null);
                        }
                    }else $response->create(201, $errors, null);
                }else $response->create(201, "Invalid User Type", null);
            }else $response->create(201, "Invalid Parameter", null);
        }else $response->create(201, "Invalid Api Token", null);
    }else $response->create(201, "No Api Token Found", null);
}else $response->create(201, "Invalid Request Method", null);


// if($send_mail){
    // Helper::curl_mail_sender("register.php", $user->id, $setting->api_token);
// }

echo $response->print_response();

?>
