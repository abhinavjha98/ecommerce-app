
<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;



class Mailer{

    private $user;
    private $smtp_config;

    public function __construct($user){
		
		
		
		$this->user = $user;
        $smtp_config = new Smtp_Config();
        $this->smtp_config = $smtp_config->where(["admin_id" => $user->admin_id])->one();
    }

    function send(){

        $mail = new PHPMailer(true);                              // Passing `true` enables exceptions
        try {

            $mail->ClearAllRecipients( );

            //Server settings
            // $mail->SMTPDebug = 2;                                 // Enable verbose debug output
           $mail->isSMTP();                                      // Set mailer to use SMTP
            //$mail->Host = $this->smtp_config->host;  // Specify main and backup SMTP servers
            $mail->SMTPAuth = true;                              
            //$mail->Username = $this->smtp_config->username;                
            //$mail->Password = $this->smtp_config->password;                          
			//$mail->SMTPSecure = $this->smtp_config->encryption;                            
            //$mail->Port = $this->smtp_config->port;                                    
			//$mail->addAddress('ellen@example.com');              
            //$mail->addReplyTo('info@example.com', 'Information');
			//$mail->setFrom($this->smtp_config->sender_email, 'Mailer');

            //Recipients
            
            $mail->addAddress($this->user->email);     // Add a recipient
            
            // $mail->addCC('cc@example.com');
            // $mail->addBCC('bcc@example.com');

            //Attachments
            // $mail->addAttachment('/var/tmp/file.tar.gz');         // Add attachments
            // $mail->addAttachment('/tmp/image.jpg', 'new.jpg');    // Optional name
            
            //Content

            $mail->isHTML(true);                                  // Set email format to HTML
            $mail->Subject = 'Verify your account';
            $mail_body = "<h2>Success!!!!</h2>";
            $mail_body .= "<h4>Thanks for registering.</h4>";
            $mail_body .= "<p>Your verification code : <b>" . $this->user->verification_token . "</b></p>";
            $mail_body .= "<p>Do not share this code with anyone.</p>";

            //$mail->AltBody = 'This is the body in plain text for non-HTML mail clients';
            $mail->Body = $mail_body;
            $mail->SMTPOptions = array(
                'ssl' => array(
                    'verify_peer' => false,
                    'verify_peer_name' => false,
                    'allow_self_signed' => true
                )
            );
		
		
		
		$mail->Host = $this->smtp_config->host;;
        $mail->Username = $this->smtp_config->username;            
        $mail->Password = $this->smtp_config->password;     
        $mail->SetFrom($this->smtp_config->sender_email, 'Verification Email');
        $mail->Port =  $this->smtp_config->port;  
		$mail->AddReplyTo($this->user->email);
        $mail->SMTPSecure = $this->smtp_config->encryption; 
		
		



            $mail->send();
			
			

            return true;
            // echo 'Message has been sent';
        } catch (Exception $e) {
            echo 'Message could not be sent. Mailer Error: ', $mail->ErrorInfo;
            return false;
        }
    }

}