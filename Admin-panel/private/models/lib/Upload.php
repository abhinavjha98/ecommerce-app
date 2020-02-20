<?php

class Upload{

    private $basename;
    public $size;
    public $file_name;
    private $extension;
    public $type = "img";
    private $temp_file;
    private $errors;
    private $upload_errors;
    private $max_size;
    public $resolution;
    public $thumb = true;

    function __construct($input){
        $this->max_size =  $this->parse_size(ini_get('upload_max_filesize'));
        $this->extension = pathinfo($input['name'], PATHINFO_EXTENSION);
        $this->basename = str_replace(("." . $this->extension), "", $input['name']);
        $this->basename = str_replace(" ", "_", $this->basename);
        $this->size = $input['size'];
        $this->temp_file = $input['tmp_name'];
        $this->upload_errors = $input['error'];
        $this->errors = new Errors();
        $this->file_name = $this->basename . "." . $this->extension;
    }

    function parse_size($size) {
        $unit = preg_replace('/[^bkmgtpezy]/i', '', $size); // Remove the non-unit characters from the size.
        $size = preg_replace('/[^0-9\.]/', '', $size); // Remove the non-numeric characters from the size.
        if ($unit) {
            // Find the position of the unit in the ordered string which is the power of magnitude to multiply a kilobyte by.
            return round($size * pow(1024, stripos('bkmgtpezy', $unit[0])));
        }
        else return round($size);
    }



    public function get_errors(){
        return $this->errors;
    }

    public function set_type($type){
        $this->type = strtolower($type);
    }

    public function set_resolution(){
        $res_arr = getimagesize(UPLOAD_FOLDER . $this->get_file_name());
        $this->resolution = $res_arr[0] . ":" . $res_arr[1];
    }

    public function get_resolution(){
        $res_arr = getimagesize(UPLOAD_FOLDER . $this->get_file_name());
        return $res_arr[0] . ":" . $res_arr[1];
    }

    public static function get_image_resolution($file_name){
        return getimagesize(UPLOAD_FOLDER . $file_name);
    }

    public function set_max_size($max_size){
        $this->max_size = $max_size;
    }

    private function validate_size(){
        if ($this->size > ($this->max_size * 1024 * 1024)) {
            return false;
        }
        return true;
    }

    public function get_file_name(){
        return $this->basename . "." . $this->extension;
    }

    private function validate_extension(){
        $img_ext = strtolower($this->extension);
        if($this->type == "img"){
            if($img_ext != "jpg" && $img_ext != "png" && $img_ext != "jpeg" && $img_ext != "gif") {
                return false;
            }else return true;
        }else if($this->type == "video"){
            if($img_ext != "mp4") return false;
            else return true;
        }
    }

    private function validate_extension_header(){
        $finfo = finfo_open(FILEINFO_MIME_TYPE);
        $mime = finfo_file($finfo, $this->temp_file);
        if($this->type = "img"){
            if($mime == "image/png" || $mime == "image/jpg" || $mime == "image/jpeg") return true;
            else return false;
        }elseif ($this->type = "video"){
            if($mime == "video/mp4") return true;
            else return false;
        }
    }

    
    public function upload(){
        if(!$this->validate_extension() && !$this->validate_extension_header()){
            $this->errors->add_error("Invalid File");
        }else if(!$this->validate_size()){
            $this->errors->add_error("File can't be over " . $this->max_size . " MB");
        }

        if(empty($this->errors->errors)) {
            if (file_exists(UPLOAD_FOLDER . $this->basename . "." . $this->extension)) {
                $this->basename = Helper::unique_code(16);
                $this->file_name = $this->basename . "." . $this->extension;
            }
            $target_file = UPLOAD_FOLDER . $this->basename . "." . $this->extension;

            if(move_uploaded_file($this->temp_file, $target_file)){

                if($this->thumb && $this->type == "img") {
                    $res_arr = getimagesize($target_file);
                    $this->resolution = $res_arr[0] . ":" . $res_arr[1];

                    if($this->thumbnail(300, 300)) return true;
                    else $this->errors->add_error("Something Went Wrong Generating Thumb Image.");
                }else return true;

            }else{
                $this->error_message();
                return false;
            }
        }
    }


    private function thumbnail($maxw, $maxh) {
        $jpg = UPLOAD_FOLDER . $this->file_name;
        $result = 0;
        if( $jpg ){
            list( $width, $height  ) = getimagesize( $jpg ); //$type will return the type of the image

            $img_ext = strtolower($this->extension);
            if(($img_ext == "jpg") || ($img_ext == "jpeg")) $source = imagecreatefromjpeg( $jpg );
            else if($img_ext == "png") $source = imagecreatefrompng( $jpg );
            else if($img_ext == "gif") $source = imagecreatefromgif( $jpg );
            else $source = imagecreatefromjpeg( $jpg );

            if( $maxw >= $width && $maxh >= $height )  $ratio = 1;
            elseif( $width > $height ) $ratio = $maxw / $width;
            else $ratio = $maxh / $height;

            $thumb_width = round( $width * $ratio ); //get the smaller value from cal # floor()
            $thumb_height = round( $height * $ratio );

            $thumb = imagecreatetruecolor( $thumb_width, $thumb_height );

            if(($img_ext == "png") ||($img_ext == "gif")){
                imagealphablending($thumb, false);
                imagesavealpha($thumb,true);
            }

            imagecopyresampled( $thumb, $source, 0, 0, 0, 0, $thumb_width, $thumb_height, $width, $height );
            $path = UPLOAD_FOLDER_THUMB . $this->file_name;

            if(($img_ext == "jpg") || ($img_ext == "jpeg")) $result = imagejpeg( $thumb, $path, 90);
            else if($img_ext == "png") $result = imagepng( $thumb, $path, 9);
            else if($img_ext == "gif") $result = imagegif( $thumb, $path, 9);
            else $result = imagejpeg( $thumb, $path, 90);

        }
        imagedestroy($thumb);
        imagedestroy($source);
        return $result;
    }


    private function error_message(){
        if($this->upload_errors == 1)
            $this->errors->add_error("The uploaded file exceeds the upload_max_filesize directive in php.ini.");
        else if($this->upload_errors == 2)
            $this->errors->add_error("The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form.");
        else if($this->upload_errors == 3)
            $this->errors->add_error("The uploaded file was only partially uploaded.");
        else if($this->upload_errors == 4)
            $this->errors->add_error("No file was uploaded.");
        else if($this->upload_errors == 6)
            $this->errors->add_error("Missing a temporary folder.");
        else if($this->upload_errors == 7)
            $this->errors->add_error("Failed to write file to disk.");
        else if($this->upload_errors == 8)
            $this->errors->add_error("A PHP extension stopped the file upload. PHP does not provide a way to ascertain which extension 
            caused the file upload to stop; 
            examining the list of loaded extensions with phpinfo() may help.");
        else
            $this->errors->add_error("Something went wrong while uploading. Please try again");

    }

    public static function delete($image_name){
        $success = false;
        $target_file = UPLOAD_FOLDER . $image_name;
        if (file_exists($target_file)) {
            if(unlink($target_file)){
                if (file_exists(UPLOAD_FOLDER_THUMB . $image_name)) {

                    unlink(UPLOAD_FOLDER_THUMB . $image_name);
                    $success = true;

                }
            }
        }
        return $success;
    }

    

}