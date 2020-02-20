<?php

class Session{

    const PROJECT = "bootic";

    public static function set_session_arr($arr){
        $_SESSION[self::PROJECT . key($arr)] = $arr[key($arr)];
    }

    public static function get_session_by_key($key){
        if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
            return $_SESSION[self::PROJECT . $key];
        }
    }

    public static function unset_session_by_key($key){
        if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
            unset($_SESSION[self::PROJECT . $key]);
        }
    }

    public static function get_temp_session_by_key($key){
        if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
            $val = $_SESSION[self::PROJECT . $key];
            unset($_SESSION[self::PROJECT . $key]);
            return $val;
        }
    }

    public static function set_session($obj){
        foreach ($obj as $key => $value){
            if(!empty($value)){
                $_SESSION[self::PROJECT . $key] = $value;
            }
        }
    }

    public static function unset_session($obj){
        foreach ($obj as $key => $value){
            if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
                unset($_SESSION[self::PROJECT . $key]);
            }
        }
    }

    public static function get_session($obj){
        $class_name = get_class($obj);
        $session_obj = new $class_name;
        $session_ok = false;
        foreach ($obj as $key => $value){
            if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
                $session_obj->$key = $_SESSION[self::PROJECT . $key];
                $session_ok = true;
            }
        }
        if($session_ok) return $session_obj;
        else return null;
    }

    public static function get_temp_session($obj){
        $class_name = get_class($obj);
        $session_obj = new $class_name;
        $session_ok = false;
        foreach ($obj as $key => $value){
            if ((isset($_SESSION[self::PROJECT . $key])) && (!empty($_SESSION[self::PROJECT . $key]))) {
                $session_obj->$key = $_SESSION[self::PROJECT . $key];
                unset($_SESSION[self::PROJECT . $key]);
                $session_ok = true;
            }
        }
        if($session_ok) return $session_obj;
        else return null;
    }

}