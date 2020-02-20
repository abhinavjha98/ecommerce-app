<?php

class Admin extends Util{

    public $id;
    public $username;
    public $email;
    public $password;

    public function save(){
        $this->password = password_hash($this->password, PASSWORD_BCRYPT);
        return parent::save();
    }

    public function update(){
        $this->password = password_hash($this->password, PASSWORD_BCRYPT);
        return parent::update();
    }

    public function verify_login(){
        $admin_frm_db = $this->where(["username" => $this->username])->one();

        if(!empty($admin_frm_db)) {
            if(password_verify($this->password, $admin_frm_db->password)) {
                return $admin_frm_db;
            }
        }
        return null;
    }


    public function validate(){
        $errors = parent::validate();
        foreach ($this as $key => $value){
            if($key == "email"){
                $message = Helper::validateEmail($value);
                if(!empty($message)) array_push($errors, $message);
            }else if($key == "password"){
                $message = Helper::invalid_length($key, $value, 6);
                if(!empty($message)) array_push($errors, $message);
            }
        }
        return $errors;
    }
    

}