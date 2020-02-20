<?php


class 

Util extends Database{
    
    private $errors;

    public function create_property($name, $value){
        $this->{$name} = $value;
    }
    
    function __construct(){
        parent::__construct();
        $this->errors = new Errors();
    }

    public function to_valid_array(){
        $columns = [];
        foreach ($this as $key => $value) {
            if(($key == "errors") || ($key == "admin_id") || ($key == "password")) continue;
            if(!empty($value)) $columns[$key] = $value;
        }
        return $columns;
    }

    public function getVariables(){
        $columns = [];
        foreach ($this as $key => $value) {
            if($key == "errors") continue;
            $columns[$key] = $value;
        }
        return $columns;
    }

    public function get_errors(){
        return $this->errors;
    }

    public function validate(){
        foreach ($this as $key => $value){
            if(empty($value)) $this->errors->add_error(ucfirst($key) . " can't be empty.");
        }
    }

    public function validate_with(array $with){
        if (count($with) > 1) {
            $with_str = "";
            foreach ($with as $item) {
                $with_str .= ucfirst($item) . " | ";
            }
        } else if (count($with) == 1) $with_str = ucfirst($with[0]);

        foreach ($this as $key => $value) {
            if ((strpos($with_str, ucfirst($key)) === false) || ($key == "errors")) continue;
            if (empty(trim($value))) {
                $this->errors->add_error(ucfirst($key) . " can't be empty.");
            }
        }
    }

    public function validate_except($except){
        if(count($except) > 1) {
            $except_str = "";
            foreach ($except as $item){
                $except_str .= ucfirst($item) . " | ";
            }
        }else if(count($except) == 1) $except_str = ucfirst($except[0]);

        foreach ($this as $key => $value){
            if((strpos($except_str, ucfirst($key)) !== false) || ($key == "errors")) continue;
            if(empty(trim($value))) $this->errors->add_error(ucfirst($key) . " can't be empty.");
        }
    }

}