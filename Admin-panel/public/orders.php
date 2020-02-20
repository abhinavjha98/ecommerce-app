<?php require_once('../private/init.php'); ?>

<?php
$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());
$order_heading = "";

$sort_by_array["created"] = "Date";
$sort_by_array["amount"] = "Amount";
$sort_by_array["method"] = "Payment Method";
$sort_by_array["Status"] = "Status";

$sort_type_array["DESC"] = "Desc";
$sort_type_array["ASC"] = "Asc";

$sort_by = $sort_type = $search = "";
$this_url = "orders.php?";

if(!empty($admin)){
    $all_orders = new Orders();

    $pagination = "";
    $pagination_msg = "";

    $panel_setting = new Setting();
    $panel_setting = $panel_setting->where(["admin_id"=> $admin->id])->one();

    if(Helper::is_get()){
        $days = Helper::get_val("days");
        $page = Helper::get_val("page");
        $search = Helper::get_val("search");
        $sort_by = Helper::get_val("sort_by");
        $sort_type = Helper::get_val("sort_type");

        if($search){
            $searched_mps = new User();
            $item_count = $all_orders->where(["admin_id" => $admin->id])
                ->like(["method" => $search])->search()->count();

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
                $all_orders = $all_orders->where(["admin_id" => $admin->id])
                    ->like(["method" => $search])->search()
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();
            }else{
                $all_orders = $all_orders->where(["admin_id" => $admin->id])
                    ->like(["method" => $search])->search()
                    ->orderBy("created")->orderType("DESC")
                    ->limit($start, BACKEND_PAGINATION)->all();
            }

        }else{
            $item_count = $all_orders->where(["admin_id" => $admin->id])->count();
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
                $all_orders = $all_orders->where(["admin_id" => $admin->id])
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();
            }else{
                $all_orders = $all_orders->where(["admin_id" => $admin->id])
                    ->orderBy("created")->orderType("DESC")
                    ->limit($start, BACKEND_PAGINATION)->all();
            }
        }
    }



   /* if($days){
        $order_heading = "<span class='sub-heading'> ( Showing Orders of last " . $days ." days. )</span>";
        $all_orders = $all_orders->by_date(30)->andWhere(["admin_id" => $admin->id])->orderBy("id")->desc()->limit($start, BACKEND_PAGINATION)->all();
    }else{
        $all_orders = $all_orders->where(["admin_id" => $admin->id])->orderBy("id")->desc()->limit($start, BACKEND_PAGINATION)->all();
    }*/

    $order_status = [];
    $order_status[PENDING] = "Pending";
    $order_status[CLEAR] = "Clear";

}else Helper::redirect_to("login.php");



?>


<?php require("common/php/php-head.php"); ?>

    <body>

<?php require("common/php/header.php"); ?>

    <div class="main-container">

        <?php require("common/php/sidebar.php"); ?>

        <div class="main-content">

            <h5 class="mb-30 mb-xs-15">Orders</h5>

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
                        <h6 class="">Showing <?php echo ($start + 1) . " - " . ((($page - 1) * BACKEND_PAGINATION) + count($all_orders)) . " of " . $item_count; ?> result</h6>
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
                            <th>Transaction ID</th>
                            <th>Method</th>
                            <th>Amount<span class="font-8">(<?php echo $panel_setting->currency_name; ?>)</span></th>
                            <th>User</th>
                            <th>Created</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>

                        <?php if(count($all_orders) > 0){
                            foreach ($all_orders as $item){ ?>

                                <tr>
                                    <?php $date = date_create($item->created);
                                    $order_no = date_format($date, 'mjY');
                                    $order_no = $order_no . $item->id;
                                    ?>
                                    <td><?php echo $order_no; ?></td>
                                    <td><?php echo $item->method; ?></td>

                                    <?php
                                    $amount = $panel_setting->currency_font . " " . $item->amount;
                                    if($panel_setting->currency_type == CURRENCY_APPEND){
                                        $amount = $item->amount . ' ' . $panel_setting->currency_font;
                                    }
                                    ?>


                                    <td><?php echo $amount; ?></td>
                                    <td><?php
                                        $user = new User();
                                        $user = $user->where(["id"=>$item->user])->one();
                                        if(!empty($user)) echo $user->username;
                                        else echo "Unknown"; ?>
                                    </td>
                                    <td><?php echo Helper::format_time($item->created); ?></td>
                                    <td>
                                        <div class="status-wrapper">
                                            <select data-action="../private/controllers/order_status.php?id=<?php echo $item->id . "&&admin_id=" . $admin->id; ?>" class="order-status">
                                                <?php foreach ($order_status as $key => $value){ ?>
                                                    <?php if($key == $item->status) $status_selected = "Selected";
                                                    else $status_selected = ""; ?>
                                                    <option value="<?php echo $key; ?>"  <?php echo $status_selected; ?>><?php echo $value; ?></option>
                                                <?php } ?>
                                            </select>
                                            <div class="d-down-loader"></div>
                                        </div>
                                    </td>

                                    <td><div class="action-wrapper"><a class="action-btn" href="#"><i class="ion-more"></i></a>
                                            <ul class="d-down">
                                                <li><a href="order-detail.php?id=<?php echo $item->id; ?>">View</a></li>
                                                <li><a data-confirm = "Are you sure?" href="../private/controllers/order.php?id=<?php echo $item->id . "&&admin_id=" . $admin->id; ?>">Delete</a></li>
                                            </ul>
                                        </div>
                                    </td>
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

<script>
    /*MAIN SCRIPTS*/
    (function ($) {
        "use strict";

        $('.order-status').on('change', function(){
            var $this = $(this),
                url = $this.closest('select').data('action') + "&&status=" + $this.val(),
                dDownLoader = $this.closest('.status-wrapper').find('.d-down-loader');

            $(dDownLoader).addClass('active');

            var a = $.ajax({
                url: url,
                dataType: 'text',
                cache: false,
                type: 'GET',
                success: function(res) {

                    $(dDownLoader).removeClass('active');
                }
            });
        });

    })(jQuery);
</script>
