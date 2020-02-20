<?php require_once('../private/init.php'); ?>

<?php
$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

$sort_by_array["created"] = "Date";
$sort_by_array["title"] = "Title";
$sort_by_array["current_price"] = "Selling Price";
$sort_by_array["purchase_price"] = "Purchase Price";
$sort_by_array["category"] = "Category";
$sort_by_array["featured"] = "Featured";
$sort_by_array["status"] = "Status";

$sort_type_array["DESC"] = "Desc";
$sort_type_array["ASC"] = "Asc";

$sort_by = $sort_type = $search = "";
$url_current = "products.php?";

if(!empty($admin)){
    $all_products = new Product();
    $pagination = "";
    $pagination_msg = "";

    if(Helper::is_get()){
        $page = Helper::get_val("page");
        $search = Helper::get_val("search");
        $sort_by = Helper::get_val("sort_by");
        $sort_type = Helper::get_val("sort_type");;
        $category = Helper::get_val("category");
        
        if($search){
            if($category){
                $url_for_pagination = $url_current . "category=" . $category . "&&";
                $item_count = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])
                    ->like(["title" => $search])->search()->count();
            }else{
                $url_for_pagination = $url_current;
                $item_count = $all_products->where(["admin_id" => $admin->id])->like(["title" => $search])->search()->count();
            }

            if($item_count < 1) $pagination_msg = "Nothing Found.";

            $pagination = new Pagination($item_count, BACKEND_PAGINATION, $page, $url_for_pagination);
            if($page){
                if(($page > $pagination->get_page_count()) || ($page < 1)) $pagination_msg = "Nothing Found.";
            }else {
                $page = 1;
                $pagination->set_page($page);
            }

            $start = ($page - 1) * BACKEND_PAGINATION;

            if($category){
                if($sort_by && $sort_type){
                    $all_products = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])
                        ->like(["title" => $search])->like(["tags" => $search])->search()
                        ->orderBy($sort_by)->orderType($sort_type)
                        ->limit($start, BACKEND_PAGINATION)->all();
                }else{
                    $all_products = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])
                        ->like(["title" => $search])->like(["tags" => $search])->search()
                        ->orderBy("created")->orderType("DESC")
                        ->limit($start, BACKEND_PAGINATION)->all();
                }
            }else{
                if($sort_by && $sort_type){
                    $all_products = $all_products->where(["admin_id" => $admin->id])
                        ->like(["title" => $search])->like(["tags" => $search])->search()
                        ->orderBy($sort_by)->orderType($sort_type)
                        ->limit($start, BACKEND_PAGINATION)->all();
                }else{
                    $all_products = $all_products->where(["admin_id" => $admin->id])
                        ->like(["title" => $search])->like(["tags" => $search])->search()
                        ->orderBy("created")->orderType("DESC")
                        ->limit($start, BACKEND_PAGINATION)->all();
                }
            }

        }else{
            if($category){
                $url_for_pagination = $url_current . "category=" . $category . "&&";
                $item_count = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])->count();
            }else{
                $url_for_pagination = $url_current;
                $item_count = $all_products->where(["admin_id" => $admin->id])->count();
            }
            
            $item_count = $all_products->where(["admin_id" => $admin->id])->count();
            if($item_count < 1) $pagination_msg = "Nothing Found.";
            
            $pagination = new Pagination($item_count, BACKEND_PAGINATION, $page, $url_for_pagination);
            if($page) {
                if(($page > $pagination->get_page_count()) || ($page < 1)) $pagination_msg = "Nothing Found.";
            }else {
                $page = 1;
                $pagination->set_page($page);
            }


            $start = ($page - 1) * BACKEND_PAGINATION;

            if($category){
                if($sort_by && $sort_type){
                    $all_products = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])
                        ->orderBy($sort_by)->orderType($sort_type)
                        ->limit($start, BACKEND_PAGINATION)->all();
                }else{
                    $all_products = $all_products->where(["admin_id" => $admin->id])->andWhere(["category" => $category])
                        ->orderBy("created")->orderType("DESC")
                        ->limit($start, BACKEND_PAGINATION)->all();
                }
            }else{
                if($sort_by && $sort_type){
                    $all_products = $all_products->where(["admin_id" => $admin->id])
                        ->orderBy($sort_by)->orderType($sort_type)
                        ->limit($start, BACKEND_PAGINATION)->all();
                }else{
                    $all_products = $all_products->where(["admin_id" => $admin->id])
                        ->orderBy("created")->orderType("DESC")
                        ->limit($start, BACKEND_PAGINATION)->all();
                }
            }
        }
    }

    $panel_setting = new Setting();
    $panel_setting = $panel_setting->where(["admin_id"=> $admin->id])->one();

    $all_categories = new Category();
    $all_categories = $all_categories->where(["admin_id" => $admin->id])->all();
    $categories_assoc = [];
    foreach ($all_categories as $item){
        $categories_assoc[$item->id] = $item->title;
    }

}else Helper::redirect_to("login.php");

?>


<?php require("common/php/php-head.php"); ?>

    <body>

<?php require("common/php/header.php"); ?>

    <div class="main-container">

        <?php require("common/php/sidebar.php"); ?>

        <div class="main-content">


            <?php if($message) echo $message->format(); ?>


            <div class="oflow-hidden mb-xs-0">
                <div class="float-l search-wrapper">

                    <form method="get">
                        <?php if($category) { ?> <input type="hidden" name="category" value="<?php echo $category; ?>" /> <?php } ?>
                        <input type="text" placeholder="Search Here" name="search" value="<?php echo $search; ?>"/>
                        <button type="submit"><b>Search</b></button>
                    </form>
                </div>
                <h6 class="float-r mt-5"><b><a class="c-btn" href="product-form.php">+ Add Product</a></b></h6>
            </div>


            <div class="oflow-hidden sort-wrapper">
                <div class="float-l page-count-text">

                    <?php if(empty($pagination_msg)){ ?>
                        <h6 class="">Showing <?php echo ($start + 1) . " - " . ((($page - 1) * BACKEND_PAGINATION) + count($all_products)) . " of " . $item_count; ?> result</h6>
                    <?php }else{
                        echo "<h6 class='ml-10'>" . $pagination_msg . "</h6>";
                    } ?>

                </div>
                <div class="float-r">
                    <form method="get">

                        <?php if($search) { ?> <input type="hidden" name="search" value="<?php echo $search; ?>" /> <?php } ?>
                        <?php if($category) { ?> <input type="hidden" name="category" value="<?php echo $category; ?>" /> <?php } ?>

                        <div class="dplay-inl-b">
                            <label>Sort by</label>
                            <select name="sort_by">
                                <?php foreach($sort_by_array as $key => $value){
                                    if(!empty($sort_by) && $key == $sort_by) $selected_sort_by = "selected";
                                    else $selected_sort_by = ""; ?>

                                    <option <?php echo $selected_sort_by; ?> value="<?php echo $key; ?>"><?php echo $value; ?></option>
                                <?php } ?>
                            </select>
                        </div>

                        <div class="dplay-inl-b">
                            <select name="sort_type">
                                <?php foreach($sort_type_array as $key => $value){
                                    if(!empty($sort_by) && $key == $sort_type) $selected_sort_type = "selected";
                                    else $selected_sort_type = ""; ?>
                                    <option <?php echo $selected_sort_type; ?> value="<?php echo $key; ?>"><?php echo $value; ?></option>
                                <?php } ?>
                            </select>
                        </div>

                        <div class="dplay-inl-b">
                            <button class="" type="submit">Submit</button>
                        </div>

                    </form>
                </div><!--float-r-->
            </div><!--oflow-hidden-->


            <div class="item-wrapper">
                <div class="masonry-grid four">

                    <?php if(count($all_products) > 0){
                        foreach ($all_products as $item){ ?>

                            <div class="masonry-item">
                                <div class="item">

                                    <div class="item-inner">

                                        <div class="video-header oflow-hidden">
                                            <h6 class="all-status video-status float-l"><?php
                                                if($item->status == 1) echo "<span class='active'>Active</span>";
                                                else echo "<span class='inactive'>Inactive</span>"; ?>
                                            </h6>

                                            <h6 class="all-status video-status float-r"><?php
                                                if($item->featured == 1) echo "<span class='active'>Featured</span>"; ?>
                                            </h6>
                                        </div>

                                        <div class="p-25">

                                            <img class="p-15" src="<?php echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR . $item->image_name; ?>" alt="image" />

                                            <h5 class="mtb-10"><a href="product-form.php?id=<?php echo $item->id; ?>"><?php echo $item->title; ?></a></h5>

                                            <?php if(!empty($categories_assoc[$item->category])){

                                                $cat_param = $url_current . "category=" . $item->category;
                                                $current_category = "<a class='ml-5 link' href='" . $cat_param . "'>" . $categories_assoc[$item->category] . "</a>";
                                            }else $current_category = "Unknown"; ?>

                                            <p class="mtb-10">Category : <?php echo $current_category; ?></p>

                                            <?php
                                                $purchase_price = $panel_setting->currency_font . $item->purchase_price;
                                                $prev_price = $panel_setting->currency_font . $item->prev_price;
                                                $current_price = $panel_setting->currency_font . $item->current_price;

                                                if($panel_setting->currency_type == CURRENCY_APPEND){

                                                    $purchase_price = $item->purchase_price . ' ' . $panel_setting->currency_font;
                                                    $prev_price = $item->prev_price  . ' ' . $panel_setting->currency_font;
                                                    $current_price = $item->current_price . ' ' . $panel_setting->currency_font;

                                                }
                                            ?>

                                            <p class="mtb-10">Purchased : <b><?php echo $purchase_price ?></b></p>
                                            <p class="mtb-10">Selling :
                                                <?php if($item->prev_price > 0){ ?>
                                                    <span class="prev-price"><?php echo $prev_price; ?></span>
                                                <?php } ?>
                                                <span class="current-price"><b><?php echo $current_price; ?></b></span></p>
                                            <p class="">Tags : <?php echo $item->tags; ?></p>

                                        </div><!--item-inner-->

                                        <div class="item-footer two">
                                            <a href="<?php echo 'product-form.php?id=' . $item->id; ?>"><i class="ion-compose"></i></a>
                                            <a data-confirm = "Are you sure?" href="<?php echo '../private/controllers/product.php?id=' . $item->id . '&&admin_id=' . $item->admin_id; ?>">
                                                <i class="ion-trash-a"></i></a>
                                        </div><!--item-footer-->

                                    </div><!--item-inner-->
                                </div><!--item-->
                            </div><!--masonry-item-->

                        <?php }
                    } ?>

                </div><!--masonry-grid-->
            </div><!--item-wrapper-->

            <?php echo $pagination->format(["sort_by"=>$sort_by, "sort_type"=>$sort_type, "search"=>$search]); ?>

        </div><!--main-content-->
    </div><!--main-container-->

<?php require("common/php/php-footer.php"); ?>