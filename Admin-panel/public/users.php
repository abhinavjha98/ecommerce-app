<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

$sort_by_array["created"] = "Date";
$sort_by_array["username"] = "Name";
$sort_by_array["email"] = "Email";
$sort_by_array["type"] = "Type";
$sort_by_array["status"] = "Verified";

$sort_type_array["DESC"] = "Desc";
$sort_type_array["ASC"] = "Asc";

$sort_by = $sort_type = $search = "";
$this_url = "news.php?";

if(!empty($admin)){
    $all_users = new User();
    $pagination = "";
    $pagination_msg = "";

    if(Helper::is_get()){
        $page = Helper::get_val("page");
        $search = Helper::get_val("search");
        $sort_by = Helper::get_val("sort_by");
        $sort_type = Helper::get_val("sort_type");

        if($search){
            $searched_mps = new User();
            $item_count = $all_users->where(["admin_id" => $admin->id])
                ->like(["username" => $search])->like(["email" => $search])->search()->count();

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
                $all_users = $all_users->where(["admin_id" => $admin->id])
                    ->like(["username" => $search])->like(["email" => $search])->search()
                    ->orderBy($sort_by)->orderType($sort_type)->limit($start, BACKEND_PAGINATION)->all();
            }else{
                $all_users = $all_users->where(["admin_id" => $admin->id])
                    ->like(["username" => $search])->like(["email" => $search])->search()
                    ->orderBy("created")->orderType("DESC")->limit($start, BACKEND_PAGINATION)->all();
            }

        }else{
            $item_count = $all_users->where(["admin_id" => $admin->id])->count();
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
                $all_users = $all_users->where(["admin_id" => $admin->id])
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();;
            }else{
                $all_users = $all_users->where(["admin_id" => $admin->id])
                    ->orderBy("created")->orderType("DESC")->limit($start, BACKEND_PAGINATION)->all();
            }
        }
    }

}else  Helper::redirect_to("login.php");

?>

<?php require("common/php/php-head.php"); ?>

<body>

<?php require("common/php/header.php"); ?>

<div class="main-container">

	<?php require("common/php/sidebar.php"); ?>

	<div class="main-content">

        <h5 class="mb-30 mb-xs-15">Registered users</h5>

        <?php if($message) echo $message->format(); ?>

        <div class="oflow-hidden mb-xs-0">
            <div class="search-wrapper w-100">

                <form method="get">
                    <input type="text" placeholder="Search Here" name="search" value="<?php echo $search; ?>"/>
                    <button type="submit"><b>Search</b></button>
                </form>
            </div>
        </div>

        <div class="oflow-hidden sort-wrapper">
            <div class="float-l page-count-text">

                <?php if(empty($pagination_msg)){ ?>
                    <h6 class="">Showing <?php echo ($start + 1) . " - " . ((($page - 1) * BACKEND_PAGINATION) + count($all_users)) . " of " . $item_count; ?> result</h6>
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


		<div class="item-wrapper">
            <div class="masonry-grid four">

                <?php if(count($all_users) > 0){
                    foreach ($all_users as $u){ ?>
                    <div class="masonry-item">
                        <div class="item">
                            <div class="item-inner">
                                <div class="item-content">

                                    <?php
										$user_type = $u->type;
										
                                        if($u->type == 1){
                                            if($u->status == 1) $u->type = "Email<sapan class='verified'>(Verified)</sapan>";
                                            else $u->type = "Email<sapan class='unverified'>(UnVerified)</sapan>";
                                        }else if($u->type == 2) $u->type = "Facebook";
                                        else if($u->type == 3) $u->type = "Gmail";
                                        else if($u->type == NUMBER_USER) $u->type = "Phone";
                                        else $u->type = "Unknown";
                                    ?>
                                    <h6 class="color-semi-dark">Signed in with</h6>
                                    <h6><?php echo $u->type; ?></h6>
                                    <h6 class="mt-15 color-semi-dark">Username</h6>
                                    <h5><?php echo $u->username; ?></h5>
									
									<?php if($user_type == NUMBER_USER) { ?>
										<h6 class="mt-15 color-semi-dark">Number</h6>
									<?php }else{ ?>
										<h6 class="mt-15 color-semi-dark">Email</h6>
									<?php }?>
                                    
                                    <h5><?php echo $u->email; ?></h5>
                                </div><!--item-content-->

                                <div class="item-footer">
                                    <a ata-confirm = "Are you sure?" href="<?php echo '../private/controllers/users.php?id=' .
                                        $u->id . '&&admin_id=' . $u->admin_id; ?>"
                                       data-confirm="Are you sure you want to delete this item?">Delete</a>

                                  </div><!--item-footer-->

                            </div><!--item-inner-->
                        </div><!--item-->
                    </div><!--masonry-grid-->
                    <?php }
                }?>

            </div><!--masonry-grid-->
		</div><!--item-wrapper-->

        <?php echo $pagination->format(["sort_by"=>$sort_by, "sort_type"=>$sort_type, "search"=>$search]); ?>
        
	</div><!--main-content-->
</div><!--main-container-->

<?php require("common/php/php-footer.php"); ?>