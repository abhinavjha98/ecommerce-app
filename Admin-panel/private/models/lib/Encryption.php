<?php


class Encryption{

    private static $OPENSSL_CIPHER_NAME = "aes-128-cbc"; //Name of OpenSSL Cipher 
    private static $CIPHER_KEY_LEN = 16; //128 bits


    static function encrypt($key, $iv, $data) {
        if (strlen($key) < Encryption::$CIPHER_KEY_LEN) {
            $key = str_pad("$key", Encryption::$CIPHER_KEY_LEN, "0"); //0 pad to len 16
        } else if (strlen($key) > Encryption::$CIPHER_KEY_LEN) {
            $key = substr($key, 0, Encryption::$CIPHER_KEY_LEN); //truncate to 16 bytes
        }

        $encodedEncryptedData = base64_encode(openssl_encrypt($data, Encryption::$OPENSSL_CIPHER_NAME, $key, OPENSSL_RAW_DATA, $iv));
        $encodedIV = base64_encode($iv);
        $encryptedPayload = $encodedEncryptedData;

        return $encryptedPayload;
    }


    static function decrypt($key, $iv, $data) {
        
       
         
        if (strlen($key) < Encryption::$CIPHER_KEY_LEN) {
            $key = str_pad("$key", Encryption::$CIPHER_KEY_LEN, "0"); //0 pad to len 16
        } else if (strlen($key) > Encryption::$CIPHER_KEY_LEN) {
            $key = substr($key, 0, Encryption::$CIPHER_KEY_LEN); //truncate to 16 bytes
        }

        //$parts = explode(':', $data); //Separate Encrypted data from iv.
        
        
        $decryptedData = openssl_decrypt(base64_decode($data), Encryption::$OPENSSL_CIPHER_NAME, $key, OPENSSL_RAW_DATA, $iv);
        //$decryptedData = openssl_decrypt(base64_decode($parts[0]), Encryption::$OPENSSL_CIPHER_NAME, $key, OPENSSL_RAW_DATA, base64_decode($parts[1]));

  
        return $decryptedData;
    }
    
    
    
    function encrypt_decrypt($action, $string) {
        $output = false;
        $encrypt_method = "AES-256-CBC";
        $secret_key = $this->key;
        $secret_iv = $this->iv;
        
        // hash
        $key = hash('sha256', $secret_key);
        
        // iv - encrypt method AES-256-CBC expects 16 bytes - else you will get a warning
        $iv = substr(hash('sha256', $secret_iv), 0, 16);
        if ( $action == 'encrypt' ) {
            $output = openssl_encrypt($string, $encrypt_method, $key, 0, $iv);
            $output = base64_encode($output);
        } else if( $action == 'decrypt' ) {
            $output = openssl_decrypt(base64_decode($string), $encrypt_method, $key, 0, $iv);
        }
        return $output;
    }



}