
<?php

class Pagination{
    private $page_count;
    private $current_page;
    private $url;

    function __construct($total_item, $page_item, $current_page, $url){
        $this->current_page = $current_page;
        $this->url = $url;
        $this->page_count = floor($total_item / $page_item);
        if($total_item % $page_item != 0) $this->page_count ++;
    }

    public function set_page($current_page){
        $this->current_page = $current_page;
    }

    public function get_page_count(){
        return $this->page_count;
    }

    public function format($params = null){
        $output  = '';

        $params_str = "";
        if($params != null){
            foreach ($params as $key => $value){
                if(!empty($value)) $params_str .= "&&" . $key . '=' . $value;
            }
        }

        if($this->page_count > 1){
            $output .= '<ul class="pagination">';
            if($this->current_page > 1) {

                $anchor = '<a href="' . $this->url . 'page=' . ($this->current_page - 1) . $params_str . '">';
                $output .= '<li>' . $anchor . '<i class="ion-ios-arrow-left"></i><i class="ion-ios-arrow-left"></i></a></li>';
            } else {
                $output .= '<li class="disable"><i class="ion-ios-arrow-left"></i><i class="ion-ios-arrow-left"></i></li>';
            }

            for($i = 1; $i <= $this->page_count; $i++){
                $anchor = '<a href="' . $this->url . 'page=' . $i . $params_str . '">';
                $current_class = "";
                if($this->current_page == $i) {
                    $current_class = "current";
                    $output .= '<li class="' . $current_class . '">' . $i . '</li>';
                }else $output .= '<li class="' . $current_class . '">' . $anchor . $i . '</a></li>';
            }

            if($this->current_page < $this->page_count) {

                $anchor = '<a href="' . $this->url . 'page=' . ($this->current_page + 1) . $params_str . '">';
                $output .= '<li>' . $anchor . '<i class="ion-ios-arrow-right"></i><i class="ion-ios-arrow-right"></i></a></li>';
            } else {
                $output .= '<li class="disable"><i class="ion-ios-arrow-right"></i><i class="ion-ios-arrow-right"></i></li>';
            }
            $output .= '</ul>';
        }
        return $output;
    }
}