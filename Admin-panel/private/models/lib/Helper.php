
<?php

class Helper{
    
    public static function arrayToObject(array $array, $className) {
        return unserialize(sprintf(
            'O:%d:"%s"%s',
            strlen($className),
            $className,
            strstr(serialize($array), ':')
        ));
    }
    
    
    public static function get_distance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 6371000){
        // convert from degrees to radians
        $latFrom = deg2rad($latitudeFrom);
        $lonFrom = deg2rad($longitudeFrom);
        $latTo = deg2rad($latitudeTo);
        $lonTo = deg2rad($longitudeTo);

        $latDelta = $latTo - $latFrom;
        $lonDelta = $lonTo - $lonFrom;

        $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
                cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
        return $angle * $earthRadius;
    }
    
    public static function objectToObject($instance, $className) {
        return unserialize(sprintf(
            'O:%d:"%s"%s',
            strlen($className),
            $className,
            strstr(strstr(serialize($instance), '"'), ':')
        ));
    }


    public static function redirect_to($url){
        header('Location: ' . $url);
    }

    public static function is_post(){
        return $_SERVER['REQUEST_METHOD'] === 'POST';
    }

    public static function is_get(){
        return $_SERVER['REQUEST_METHOD'] === 'GET';
    }

    public static function post_val($val){
        return (isset($_POST[$val]) && (!empty($_POST[$val]))) ? trim($_POST[$val]) : null;
    }

    public static function get_val($val){
        return (isset($_GET[$val]) && (!empty($_GET[$val]))) ? trim($_GET[$val]) : null;
    }

    public static function validateEmail($email) {
        if(!filter_var($email, FILTER_VALIDATE_EMAIL)) return "Invalid Email";
        else return null;
    }

    public static function invalid_length($key, $value, $length){
        if(strlen($value) < $length)  return ucfirst($key) . ' must be at least ' . $length . ' char long.';
        else return null;
    }

    public static function unique_code($limit){
        return substr(base_convert(sha1(uniqid(mt_rand())), 16, 36), 0, $limit);
    }

    public static function unique_numeric_code($limit){
        return substr(uniqid(mt_rand()), 0, $limit);
    }

    public static function format_address($address){
        $formatted_address = "";
        if(empty($address)) $formatted_address = "Unknown";
        else{
            $address->id = null;
            $address->user = null;
            foreach ($address as $key => $value){
                if(!empty($value)) {
                    $formatted_address .=  $value . ", ";
                }
            }
        }
        return rtrim($formatted_address, ", ");
    }

    public static function days_ago($date){
        $date1 = new DateTime($date);
        $date2 = new DateTime();
        $interval = $date1->diff($date2);

        if($interval->i <= 0) return "Just Now";
        else if($interval->h <= 0) return $interval->i . " min";
        else if($interval->d <= 0) return $interval->h . " hour";
        else if($interval->m <= 0) return $interval->d . " day";
        else if($interval->y <= 0) return $interval->m . " month";
        else return $interval->y . " year";
    }

    public static function format_text($text, $char_count = 20){
        if(strlen($text) >$char_count) $text = substr($text, 0, $char_count) . "...";
        return $text;
    }

    public static function format_date($time){
        $date = date_create($time);
        return date_format($date, 'Y-n-j');
    }

    public static function format_time($time){
        $date = date_create($time);
        return date_format($date, 'g:ia, M j, Y');
    }

	public static function curl_mail_sender($current_page, $user_id, $api_token){
        $url  = (isset($_SERVER['HTTPS'])) ? "https://" : "http://";
        $url .= $_SERVER['HTTP_HOST'];
        $url .= str_replace($current_page,"send-mail.php", $_SERVER['REQUEST_URI']);
        $url .= "?id=" . $user_id . "&&api_token=" . $api_token;

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($ch, CURLOPT_TIMEOUT_MS, 200);
        curl_setopt($ch, CURLOPT_NOSIGNAL, 1);
        curl_exec($ch);
        curl_close($ch);
    }
	
}