<?php require_once('../private/init.php'); ?>

<?php
$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

$sort_by_array["created"] = "Date";
$sort_by_array["title"] = "Title";
$sort_by_array["status"] = "Status";

$sort_type_array["DESC"] = "Desc";
$sort_type_array["ASC"] = "Asc";

$sort_by = $sort_type = $search = "";
$this_url = "sliders.php?";

if(!empty($admin)){
    $all_sliders = new Slider();

    $pagination = "";
    $pagination_msg = "";

    if(Helper::is_get()){
        $page = Helper::get_val("page");
        $search = Helper::get_val("search");
        $sort_by = Helper::get_val("sort_by");
        $sort_type = Helper::get_val("sort_type");;

        if($search){
            $slider_count = new Slider();
            $item_count = $slider_count->where(["admin_id" => $admin->id])->like(["title" => $search])->search()->count();

            if($item_count < 1) $pagination_msg = "Nothing Found.";

            $pagination = new Pagination($item_count, BACKEND_PAGINATION, $page, $this_url);
            if($page){
                if(($page > $pagination->get_page_count()) || ($page < 1)) $pagination_msg = "Nothing Found.";
            }else {
                $page = 1;
                $pagination->set_page($page);
            }

            $start = ($page - 1) * BACKEND_PAGINATION;

            if($sort_by && $sort_type){
                $all_sliders = $all_sliders->where(["admin_id" => $admin->id])
                    ->like(["title" => $search])->search()
                    ->orderBy($sort_by)->orderType($sort_type)->limit($start, BACKEND_PAGINATION)->all();

            }else{
                $all_sliders = $all_sliders->where(["admin_id" => $admin->id])
                    ->like(["title" => $search])->search()
                    ->orderBy("created")->orderType("DESC")->limit($start, BACKEND_PAGINATION)->all();
            }
        }else{
            $item_count = $all_sliders->where(["admin_id" => $admin->id])->count();
            if($item_count < 1) $pagination_msg = "Nothing Found.";

            $pagination = new Pagination($item_count, BACKEND_PAGINATION, $page, $this_url);
            if($page) {
                if(($page > $pagination->get_page_count()) || ($page < 1)) $pagination_msg = "Nothing Found.";
            }else {
                $page = 1;
                $pagination->set_page($page);
            }

            $start = ($page - 1) * BACKEND_PAGINATION;

            if($sort_by && $sort_type){
                $all_sliders = $all_sliders->where(["admin_id" => $admin->id])
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();

            }else{
                $all_sliders = $all_sliders->where(["admin_id" => $admin->id])
                    ->orderBy("created")->orderType("DESC")
                    ->limit($start, BACKEND_PAGINATION)->all();
            }
        }
    }

}else Helper::redirect_to("login.php");

?>


<?php require("common/php/php-head.php"); ?>

    <body>

<?php require("common/php/header.php"); ?>

    <div class="main-container">

        <?php require("common/php/sidebar.php"); ?>

        <div class="main-content">

            <h5 class="mb-30 mb-xs-15">Sliders</h5>

            <?php if($message) echo $message->format(); ?>

            <div class="oflow-hidden mb-xs-0">
                <div class="float-l search-wrapper">

                    <form method="get">
                        <input type="text" placeholder="Search Here" name="search" value="<?php echo $search; ?>"/>
                        <button type="submit"><b>Search</b></button>
                    </form>
                </div>
                <h6 class="float-r mt-5"><b><a class="c-btn" href="slider-form.php">+ Add Slider</a></b></h6>
            </div>


            <div class="oflow-hidden sort-wrapper">
                <div class="float-l page-count-text">

                    <?php if(empty($pagination_msg)){ ?>
                        <h6 class="">Showing <?php echo ($start + 1) . " - " . ((($page - 1) * BACKEND_PAGINATION) + count($all_sliders)) . " of " . $item_count; ?> result</h6>
                    <?php }else{
                        echo "<h6 class='ml-10'>" . $pagination_msg . "</h6>";
                    } ?>

                </div>
                <div class="float-r">
                    <form method="get">

                        <?php if($search) { ?> <input type="hidden" name="search" value="<?php echo $search; ?>" /> <?php } ?>

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


            <div class="item-wrapper oflow-visible p-10">

                <table class="order-table min-w-1000x">
                    <thead>
                    <tr>
                        <th>Image</th>
                        <th>Title</th>
                        <th>Detail</th>
                        <th>Tag</th>
                        <th>Created</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>

                    <?php if(count($all_sliders) > 0){
                        foreach ($all_sliders as $item){ ?>
                            <tr>
                                
                                <td><img src="<?php echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR . $item->image_name; ?>" alt="image"/></td>

                                <td><?php echo $item->title; ?></td>
                                <td><?php echo $item->detail; ?></td>
                                <td><?php echo $item->tag; ?></td>

                                <td class="all-status video-status"><?php
                                    if($item->status == 1) echo "<span class='active'>Active</span>";
                                    else echo "<span class='inactive'>Inactive</span>"; ?>
                                </td>

                                <td>
                                    <a href="<?php echo 'slider-form.php?id=' . $item->id; ?>"><i class="ion-compose"></i></a>
                                    <a data-confirm = "Are you sure?" href="<?php echo '../private/controllers/slider.php?id=' . $item->id . '&&admin_id=' . $item->admin_id; ?>">
                                        <i class="ion-trash-a"></i></a>
                                </td><!--item-footer-->

                            </tr>

                        <?php }
                    } ?>


                    </tbody>
                </table>
            </div><!--item-wrapper-->


            <?php echo $pagination->format(["sort_by"=>$sort_by, "sort_type"=>$sort_type, "search"=>$search]); ?>

        </div><!--main-content-->
    </div><!--main-container-->

<?php require("common/php/php-footer.php"); ?>