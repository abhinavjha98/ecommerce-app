<?php require_once('../private/init.php'); ?>

<?php
$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());

$sort_by_array["created"] = "Date";
$sort_by_array["title"] = "Title";

$sort_type_array["DESC"] = "Desc";
$sort_type_array["ASC"] = "Asc";

$sort_by = $sort_type = $search = "";
$this_url = "attributes.php?";

if(!empty($admin)){
    $all_attributes = new Attribute();

    $pagination = "";
    $pagination_msg = "";

    if(Helper::is_get()){
        $page = Helper::get_val("page");
        $search = Helper::get_val("search");
        $sort_by = Helper::get_val("sort_by");
        $sort_type = Helper::get_val("sort_type");;

        if($search){
            $item_count = $all_attributes->where(["admin_id" => $admin->id])->like(["title" => $search])->search()->count();

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
                $all_attributes = $all_attributes->where(["admin_id" => $admin->id])
                    ->like(["title" => $search])->search()
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();

            }else{
                $all_attributes = $all_attributes->where(["admin_id" => $admin->id])
                    ->like(["title" => $search])->search()
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();
            }
        }else{
            $item_count = $all_attributes->where(["admin_id" => $admin->id])->count();
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
                $all_attributes = $all_attributes->where(["admin_id" => $admin->id])
                    ->orderBy($sort_by)->orderType($sort_type)
                    ->limit($start, BACKEND_PAGINATION)->all();
            }else{
                $all_attributes = $all_attributes->where(["admin_id" => $admin->id])
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

            <h5 class="mb-30 mb-xs-15">Product Attribute</h5>

            <?php if($message) echo $message->format(); ?>


            <div class="oflow-hidden mb-xs-0">
                <div class="float-l search-wrapper">

                    <form method="get">
                        <input type="text" placeholder="Search Here" name="search" value="<?php echo $search; ?>"/>
                        <button type="submit"><b>Search</b></button>
                    </form>
                </div>
                <h6 class="float-r mt-5"><b><a class="c-btn" href="attribute-form.php">+ Add Attribute</a></b></h6>
            </div>


            <div class="oflow-hidden sort-wrapper">
                <div class="float-l page-count-text">

                    <?php if(empty($pagination_msg)){ ?>
                        <h6 class="">Showing <?php echo ($start + 1) . " - " . ((($page - 1) * BACKEND_PAGINATION) + count($all_attributes)) . " of " . $item_count; ?> result</h6>
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

                    <?php if(count($all_attributes) > 0){
                        foreach ($all_attributes as $item){ ?>

                            <div class="masonry-item">
                                <div class="item">
                                    <div class="item-inner">

                                        <div class="video-header">
                                            <h6 class="">Attribute</h6>
                                        </div>

                                        <div class="attribute-content p-25">
                                            <div class="attr-title-wrapper">
                                                <p class="float-l"><a href="attribute-form.php?id=<?php echo $item->id; ?>"><?php echo $item->title; ?></a></p>
                                                <a data-attribute-id="<?php echo $item->id; ?>" class="demo-disable float-r popup-attribute link" href="#">Add Value</a>
                                            </div>

                                            <div class="attribute-value-wrapper">

                                            <?php
                                                $attribute_values = new Attribute_Value();
                                                $attribute_values = $attribute_values->where(["attribute"=>$item->id])->all();

                                                foreach($attribute_values as $inner_item){ ?>
                                                    <div class="attribute-value">

                                                        <h6 class="attribute-value-title"><?php echo $inner_item->title; ?></h6>
                                                        <div class="attribute-value-btn">
                                                            <a data-attribute-id="<?php echo $item->id; ?>"
                                                               data-attribute-value-id="<?php echo $inner_item->id; ?>"
                                                               data-attribute-value-title="<?php echo $inner_item->title; ?>"
                                                               class="popup-attribute" href="#">Update</a>

                                                            <?php if(count($attribute_values) > 1){ ?>
                                                                <a data-confirm = "Are you sure?"
                                                                href="<?php echo '../private/controllers/attribute_value.php?id=' . $inner_item->id . '&&admin_id=' . $admin->id; ?>">Delete</a>
                                                            <?php } ?>

                                                        </div><!--attribute-value-btn-->
                                                    </div><!-- attribute-value -->
                                                <?php } ?>
                                            </div><!--attribute-value-wrapper-->
                                        </div><!-- attribute-content -->

                                        <div class="item-footer two">
                                            <a href="<?php echo 'attribute-form.php?id=' . $item->id; ?>"><i class="ion-compose"></i></a>
                                            <a data-confirm = "Are you sure?" href="<?php echo '../private/controllers/attribute.php?id=' . $item->id . '&&admin_id=' . $item->admin_id; ?>">
                                                <i class="ion-trash-a"></i></a>
                                        </div><!-- item-footer -->

                                    </div><!--item-inner-->
                                </div><!--item-->
                            </div><!--masonry-item-->

                        <?php }
                    } ?>

                </div><!--masonry-grid-->
            </div><!--item-wrapper-->

            <div class="item-wrapper one" id="attribute-value">
                <div class="dplay-tbl">
                    <div class="dplay-tbl-cell">
                        <div class="item">

                            <form data-validation="true" data-ajax-form="true" action="../private/controllers/attribute_value.php" method="post">
                                <div class="item-inner">

                                    <div class="item-header">
                                        <h5 class="dplay-inl-b">Attribute Value</h5>
                                    </div><!--item-header-->

                                    <div class="item-content">
                                        <input type="hidden" name="id" value="">
                                        <input type="hidden" name="attribute_id" value="">
                                        <input type="hidden" class="absolute-val" name="admin_id" value="<?php echo $admin->id; ?>">
                                        <input type="hidden" class="absolute-val" name="ajax_request" value="1">

                                        <label>Title</label>
                                        <input type="text" data-required="true" placeholder="Site Title" name="title" value=""/>

                                        <div class="btn-wrapper"><button type="submit" class="ajax-btn c-btn mb-10"><b>Save</b>

                                                <div class="ajax-progress">
                                                    <div id="floatingBarsG">
                                                        <div class="blockG" id="rotateG_01"></div>
                                                        <div class="blockG" id="rotateG_02"></div>
                                                        <div class="blockG" id="rotateG_03"></div>
                                                        <div class="blockG" id="rotateG_04"></div>
                                                        <div class="blockG" id="rotateG_05"></div>
                                                        <div class="blockG" id="rotateG_06"></div>
                                                        <div class="blockG" id="rotateG_07"></div>
                                                        <div class="blockG" id="rotateG_08"></div>
                                                    </div>
                                                </div>

                                            </button></div>

                                        <?php if($errors) echo $errors->format(); ?>

                                    </div><!--item-content-->
                                </div><!--item-inner-->

                            </form>
                        </div><!-- item -->
                    </div><!-- dplay-tbl-cell -->
                </div><!-- dplay-tbl -->
            </div><!-- item-wrapper -->

            <?php echo $pagination->format(["sort_by"=>$sort_by, "sort_type"=>$sort_type, "search"=>$search]); ?>

        </div><!--main-content-->
    </div><!--main-container-->

<?php require("common/php/php-footer.php"); ?>

<?php echo "<script>adminId = '" . $admin->id  . "'</script>"; ?>

<script>
    (function ($) {
        "use strict";

        $(document).bind("ajaxStart.mine", function() {
            $('.ajax-btn').prop('disabled', true);
            $('.ajax-progress').addClass('active');
        });

        $(document).bind("ajaxStop.mine", function() {
            $('.ajax-btn').prop('disabled', false);
            $('.ajax-progress').removeClass('active');
        });
        
        $(document).on('click', '.popup-attribute', function(e){
            e.stopPropagation();
            e.preventDefault();

            var $this = $(this),
                attributeId = $this.data('attribute-id'),
                attributeValueId = $this.data('attribute-value-id'),
                attributeValueTitle = $this.data('attribute-value-title');


            $('#attribute-value').addClass('active');

            if(typeof attributeValueId == 'undefined'){
                $($('[data-ajax-form="true"]').find('input')).each(function(){

                    if(!$(this).hasClass('absolute-val')) $(this).val('');
                });
                
            }else{
                $('[name="id"]').val(attributeValueId);
                $('[name="title"]').val(attributeValueTitle);
            }

            $('[name="attribute_id"]').val(attributeId);
            
            return false;
        });


        $(document).on('submit', '[data-ajax-form="true"]', function(e){
            e.preventDefault();
            e.stopPropagation();

            var $this = $(this),
                url = $this.attr('action'),
                attributeValueId = $this.find('input[name="id"]').val(),
                isUpdate = ( attributeValueId != '') && ($.isNumeric(attributeValueId)) ? true : false;

            var a = $.ajax({
                url: url,
                dataType: 'json',
                data: $(this).serialize(),
                type: 'POST',
                success: function(res) {

                    console.log(res);
                    var uploadedObj = JSON.parse(JSON.stringify(res));
                    if(uploadedObj.status_code == 200){

                        var attributeContent = $('[data-attribute-value-id="' + uploadedObj.data.id + '"]').closest('.attribute-value');

                        if(isUpdate){
                            $(attributeContent).find('.attribute-value-title').text(uploadedObj.data.title);
                            $(attributeContent).find('a[data-attribute-value-title]').data('attribute-value-title', uploadedObj.data.title);

                        }else{

                            var attributeValue = $('<div>', { class: 'attribute-value'}),
                                attributeValueWrapper = $('<div>', { class: 'attribute-value-btn'}),
                                attributeValueTitle = $('<h6>', { text: uploadedObj.data.title, class: 'attribute-value-title'});

                            var attributeValueUpdate  = $('<a>', { href: '#',
                                'data-attribute-id' : uploadedObj.data.attribute,
                                'data-attribute-value-id' : uploadedObj.data.id,
                                'data-attribute-value-title' : uploadedObj.data.title,
                                text: 'Update', class: 'popup-attribute attribute-value-update'}),
                                attributeValueDelete = $('<a>', { href: url + '?id=' + uploadedObj.data.id + '&&admin_id=' + adminId,
                                    'data-confirm' : 'Are you sure?', text: 'Delete', class: 'attribute-value-delete'});

                            $(attributeValueWrapper).append(attributeValueUpdate);
                            $(attributeValueWrapper).append(attributeValueDelete);
                            $(attributeValue).append(attributeValueTitle);
                            $(attributeValue).append(attributeValueWrapper);
                            $(attributeContent).append(attributeValue);

                            $('.attribute-value-wrapper').append(attributeValue);

                        }

                        $('#attribute-value').removeClass('active');

                        $('.masonry-grid').masonry({
                            itemSelector: '.masonry-item',
                            percentPosition: true,
                        });

                    }else console.log(uploadedObj.message);
                }
            });

            return false;
        });


        $(document).on('click touch', function(e) {
            $('#attribute-value').removeClass('active');
        });

        $('#attribute-value .item').on('click touch', function(e) {
            e.stopPropagation();
        });


    })(jQuery);


</script>
