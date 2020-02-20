<?php


class Message{

    public $message;

    public function set_message($message){
        $this->message = $message;
    }

    public function is_empty(){
        if(empty($this->message)) return true;
        else return false;
    }

    public function format(){
       return "<h5 class='message'>" . $this->message . "</h5>";
    }
}