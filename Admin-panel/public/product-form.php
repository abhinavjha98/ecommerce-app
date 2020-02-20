<?php require_once('../private/init.php'); ?>

<?php

$errors = Session::get_temp_session(new Errors());
$message = Session::get_temp_session(new Message());
$admin = Session::get_session(new Admin());
$deletable_image_ids = "";
$inventory_status = "active";
$single_inventory = 0;

if(!empty($admin)){
    $product = new Product();
    $all_categories = new Category();
    $all_categories = $all_categories->where(["status"=>1])->all();

    $product->admin_id = $admin->id;
    $product->status = 1;

    $panel_setting = new Setting();
    $panel_setting = $panel_setting->where(["admin_id"=> $admin->id])->one();


    if(Helper::is_get() && isset($_GET["id"])){
        $product->id = $_GET["id"];
        $product = $product->where(["id" => $product->id])->andwhere(["admin_id" => $admin->id])->one();

        if($product->prev_price < 0) $product->prev_price = null;

        $inventories = new Inventory();
        $inventories = $inventories->where(["product" => $product->id])->all();

        if((count($inventories) > 0)){
            if(count($inventories) > 1) $inventory_status = "";
            elseif(count($inventories) == 1){
                if(!empty($inventories[0]->attributes)) $inventory_status = "";
            }
        }

        if((count($inventories) == 1) && empty($inventories[0]->attributes)) $single_inventory = $inventories[0]->quantity;

        if(count($product) > 0){
            $image_from_db = new Item_Image();
            $images_from_db = $image_from_db->where(["item_id"=>$product->id])->all();
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
        <div class="item-wrapper one">

            <div class="item">
                <?php if($message) echo $message->format(); ?>

                <form data-validation="true" action="../private/controllers/product.php" method="post" enctype="multipart/form-data">
                    <div class="item-inner">

                        <div class="item-header">
                            <h5 class="dplay-inl-b">Product</h5>

                            <h5 class="float-r oflow-hidden">
                                <label class="status switch">
                                    <input type="checkbox" name="status"
                                        <?php if($product->status == 1) echo "checked"; ?>/>
                                    <span class="slider round">
                                        <b class="active">Active</b>
                                        <b class="inactive">Inactive</b>
                                    </span>
                                </label>
                                <span class="toggle-title"></span>
                            </h5>
                        </div><!--item-header-->

                        <div class="item-content">
                            <input type="hidden" name="id" value="<?php echo $product->id; ?>">
                            <input type="hidden" name="admin_id" value="<?php echo $product->admin_id; ?>">
                            <input type="hidden" name="prev_image" value="<?php echo $product->image_name; ?>"/>

                            <label class="control-label" for="file">Primary Image(<?php echo "Max Image Size : " . MAX_IMAGE_SIZE . "MB. Required Format : png/jpg/jpeg"; ?>)</label>

                            <div class="image-upload">

                                <img src="<?php if(!empty($product->image_name))
                                    echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR . $product->image_name; ?>" alt="" class="uploaded-image"/>

                                <div class="h-100" class="upload-content">
                                    <div class="dplay-tbl">
                                        <div class="dplay-tbl-cell">
                                            <i class="ion-ios-cloud-upload"></i>
                                            <h5><b>Choose Your Image to Upload</b></h5>
                                            <h6 class="mt-10 mb-70">Or Drop Your Image Here</h6>
                                        </div>
                                    </div>
                                </div><!--upload-content-->
                                <input data-required="image" type="file" name="image_name" class="image-input"
                                       data-traget-resolution="image_resolution" value="<?php echo $product->image_name; ?>"/>
                                <input type="hidden" name="image_resolution" value="<?php echo $product->image_resolution; ?>"/>
                            </div>

                            <label>Title</label>
                            <input type="text" data-required="true" placeholder="Site Title" name="title" value="<?php echo $product->title; ?>"/>

                            <div class="oflow-hidden w-100">
                                <div class="input-6 pr-7-5">

                                    <label>Purchase Price</label>
                                    <div class="price-input">
                                        <span><?php echo  $panel_setting->currency_font; ?></span>
                                        <input type="text" data-required="numeric" placeholder="Purchase Price" name="purchase_price" value="<?php echo $product->purchase_price; ?>"/>
                                    </div>
                                </div>

                                <div class="input-6 pl-7-5">
                                    <label>Previous Price</label>
                                    <div class="price-input">
                                        <span><?php echo  $panel_setting->currency_font; ?></span>
                                        <input type="text" placeholder="Previous Price" name="prev_price" value="<?php echo $product->prev_price; ?>"/>
                                    </div>
                                </div>
                            </div><!--oflow-hidden-->

                            <label>Current Price</label>
                            <div class="price-input">
                                <span><?php echo  $panel_setting->currency_font; ?></span>
                                <input type="text" data-required="numeric" placeholder="Current Price" name="current_price" value="<?php echo $product->current_price; ?>"/>
                            </div>


                            <?php if($all_categories > 0){ ?>
                                <select name="category" data-required="dropdown">
                                    <option selected="true" disabled="disabled">Please select a category</option>
                                    <?php foreach($all_categories as $item){ ?>
                                        <?php if($product->category == $item->id) $selected_cat = "selected";
                                            else $selected_cat = ""; ?>

                                        <option value="<?php echo $item->id; ?>"  <?php echo $selected_cat; ?>><?php echo $item->title; ?></option>
                                    <?php }?>
                                </select>
                            <?php  } ?>

                            
                            <h5 class="mt-10 mb-30 oflow-hidden">
                                <label href="#" class="switch">
                                    <input type="checkbox" name="featured"
                                        <?php if($product->featured == 1) echo "checked"; ?>/>
                                    <span class="slider round"></span>
                                </label>
                                <span class="toggle-title ml-20">Featured</span>
                            </h5>

                            <label>Description</label>
                            <textarea data-required="true" class="desc" type="text" name="description"
                                          placeholder="Description"><?php echo $product->description; ?></textarea>


                            <label>Tags</label>
                            <textarea data-required="true" class="desc" type="text" name="tags"
                                      placeholder="men, men shirts, shirts"><?php echo $product->tags; ?></textarea>


                            <div id="upload-status"></div>
                            <div id="upload-progress"></div>
                            <div id="multiple-images">

                                <?php if(!empty($images_from_db)){

                                    foreach ($images_from_db as $item){ ?>
                                        <div class="img-wrapper">
                                            <img src="<?php echo UPLOADED_FOLDER . DIRECTORY_SEPARATOR . $item->image_name; ?>">
                                            <a class="removable-image" href="api/image/remove.php"
                                               data-image-id="<?php echo $item->id; ?>"
                                               data-file-name="<?php echo $item->image_name; ?>"><i class="ion-close-round"></i></a>
                                        </div>
                                    <?php }  ?>
                                <?php }  ?>

                            </div><!--multiple-images-->


                            <label class="control-label" for="file">Upload More Images(<?php echo "Max File Count : " . MAX_FILE_COUNT . ". Max Image Size : " . MAX_IMAGE_SIZE . "MB. Required Format : png/jpg/jpeg";; ?>)</label>

                            <input type="hidden" name="uploaded-image-names" value="" />
                            <input type="hidden" name="removed-image-ids" value="" />

                            <input data-url="api/image/upload.php"
                                   data-remove-url="api/image/remove.php"
                                   id="file-upload" type="file" class="upload-img" name="images[]" multiple />

                            <h6 class="right-text mb-30"><a data-popup="#attribute-popup" class="link" href="#">+ Add Attribute</a></h6>

                            <div id="combined-attr-wrapper">
                                <?php if(empty($single_inventory) && !empty($inventories)){

                                    foreach ($inventories as $item){ ?>

                                    <div class="combined-attr-inner">

                                        <?php
                                            $attr_id = str_replace(",", "_", $item->attributes);
                                            $attr_arr = explode("_", $attr_id);
                                            $attr_value = "";
                                            foreach ($attr_arr as $attr_item){
                                                $attr_item_id_arr = explode('-', $attr_item);
                                                $attr_item_id = $attr_item_id_arr[1];
                                                $attr_value_obj = new Attribute_Value();
                                                $attr_value_obj = $attr_value_obj->where(["id"=>$attr_item_id])->one();
                                                if(!empty($attr_value_obj)) $attr_value .= $attr_value_obj->title . " + ";
                                                else $attr_value = "Unknown   ";
                                            }
                                        $attr_value = substr($attr_value, 0, -3); ?>

                                        <label for="<?php echo $attr_id; ?>"><?php echo $attr_value; ?></label>

                                        <?php $item->quantity = ($item->quantity == DEFAULT_NEGATIVE) ? 0 : $item->quantity; ?>

                                        <input data-required="true" placeholder="Quantity" name="qty<?php echo $attr_id; ?>" id="<?php echo $attr_id; ?>"
                                               value="<?php echo $item->quantity; ?>" class="qty">

                                    </div><!--combined-attr-inner-->

                                <?php }
                                } ?>
                            </div><!--combined-attr-wrapper-->


                            <input type="hidden" name="prev_attr" />

                            <div class="inventory <?php echo $inventory_status; ?>">
                                <label>Inventory</label>
                                <?php $single_inventory = ($single_inventory == DEFAULT_NEGATIVE) ? 0 : $single_inventory; ?>

                                <input id="inventory" type="text" placeholder="Quantity" name="inventory" value="<?php echo $single_inventory; ?>"/>
                            </div>

                            <div class="btn-wrapper"><button type="submit" class="demo-disable c-btn mb-10"><b>Save</b></button></div>

                            <?php if($errors) echo $errors->format(); ?>

                        </div><!--item-content-->
                    </div><!--item-inner-->

                </form>
            </div><!--item-->

        </div><!--item-wrapper-->
    </div><!--main-content-->


    <div class="item-wrapper one popup-area "  id="attribute-popup">
        <div class="dplay-tbl">
            <div class="dplay-tbl-cell">
                <div class="item">
                    <div class="item-inner">

                        <div class="item-header">
                            <h5 class="dplay-inl-b">Attribute</h5>
                        </div><!--item-header-->

                        <div class="item-content ptb-15">

                            <form>
                                <div class="attribute-wrapper">
                                    <?php

                                    $attributes = new Attribute();
                                    $attributes = $attributes->where(["admin_id"=>$admin->id])->all();
                                    foreach ($attributes as $item){ ?>

                                        <div class="attribute-cb-wrapper">
                                            <span class="attribute-cb">
                                                <input id="atr<?php echo $item->id; ?>" type="checkbox" name="attribute"/>
                                                <label for="atr<?php echo $item->id; ?>"><?php echo $item->title; ?></label>
                                            </span><!--attribute-cb-->

                                            <?php $attribute_values = new Attribute_Value();
                                            $attribute_values = $attribute_values->where(["attribute" => $item->id])->all();

                                            foreach ($attribute_values as $inner_item){ ?>
                                                <span class="attribute-value-cb">
                                                    <input id="<?php echo 'atr' . $item->id . '-val'. $inner_item->id; ?>" type="checkbox" name="attribute-value"/>
                                                    <label for="<?php echo 'atr' . $item->id . '-val'. $inner_item->id; ?>"><?php echo $inner_item->title; ?></label>
                                                </span><!--attribute-value-cb-->
                                            <?php } ?>

                                        </div><!--attribute-cb-->

                                    <?php } ?>
                                </div><!--attribute-wrapper-->
                            </form>

                            <div>
                                <h6 class="mt-15 mb-15">Selected Combination</h6>
                                <form>
                                    <div id="combined-cb"></div>
                                    <button type="submit" class="c-btn attr-btn mb-20 mt-10" id="combined-attr-btn">submit</button>
                                </form>
                            </div>


                        </div><!--item-content-->
                    </div><!--item-inner-->

                </div><!--item-->
            </div><!--dplay-tbl-cell-->
        </div><!--dplay-tbl-->
    </div><!--item-wrapper-->

</div><!--main-container-->


 <?php echo "<script>maxUploadedFile = '" . MAX_IMAGE_SIZE  . "'</script>"; ?>
 <?php echo "<script>maxUploadedFileCount = '" . MAX_FILE_COUNT  . "'</script>"; ?>
 <?php echo "<script>adminId = '" . $admin->id  . "'</script>"; ?>

 <?php require("common/php/php-footer.php"); ?>

<script> attrQty = [];</script>
<script> attrFromDB = [];</script>
<script> attrIdsFromDB = [];</script>

<?php if(!empty($inventories)){
    foreach($inventories as $inv_item){
        $a_id = str_replace(",", "_", $inv_item->attributes);
        echo "<script>attrIdsFromDB.push('" . $inv_item->id . "'); </script>";
        echo "<script>attrFromDB.push('" . $a_id . "'); </script>";
        echo "<script>attrQty['" . $a_id . "'] = '" . $inv_item->quantity  . "'</script>";
    }
}?>

 <script>

     var uploadedFolder = '<?php echo UPLOADED_FOLDER; ?>';
     uploadedFolder = uploadedFolder + '/';

     var uploadAjaxCall,
         refreshDisable = false,
         submitDisable = false;

     var uploadStatus = $('#upload-status'),
         uploadProgress = $('#upload-progress'),
         multipleImages = $('#multiple-images');

     function checkFileType(fileType, acceptedFiles){

         var validFile = false;
         $(acceptedFiles).each(function(key, value){
             if(fileType == value) {
                 validFile = true;
                 return;
             }
         });
         return validFile;
     }

     function checkUpload(input, file){
         var $this = $(input),
             file_data = file,
             noError = true,
             acceptedFiles = ['image/png', 'image/jpeg', 'image/jpg'];

         if(file_data != null){
             if(checkFileType(file_data.type, acceptedFiles)){
                 if(file_data.size / (1024 * 1024) > maxUploadedFile){
                     noError  = false;
                     alert("Too Large file (Max file size : " + maxUploadedFile + "MB)");
                 }
             }else {
                 alert(file_data.name + " is Invalid File Type(Accepted File : png, jpg, jpeg)");
                 noError = false;
             }
         }else noError = false;
         return noError;
     }


     function showUploadedImage(file){
         var _URL = window.URL || window.webkitURL,
             img = new Image(),
             imgElement = $('<img />'),
             imageWrapper = $("<div></div>",{ class: "img-wrapper" });

         img.src = _URL.createObjectURL(file);
         img.onload = function() {

             $(imgElement).attr('src', img.src).appendTo(imageWrapper);
             imageWrapper.appendTo('#multiple-images');
         };
         return imageWrapper;
     }


     function upload(input, form_data){
         var $this = $(input),
             fileCount = $this.get(0).files.length,
             url = $this.data('url'),
             removeUrl = $this.data('remove-url');

         $(uploadProgress).css('width', 0);

         if(maxUploadedFileCount < fileCount) alert('You can upload maximum ' + maxUploadedFileCount + ' files per upload.');
         else{
             for (var i = 0; i < fileCount; ++i) {

                 var _URL = window.URL || window.webkitURL,
                     file = $this[0].files[i],
                     form_data = new FormData();

                 form_data.append('image_name', file);

                 if(file){
                     if(checkUpload(input, file)){
                         var fileType = file["type"],
                             fileName = file["name"],
                             fileSize = file["size"] / (1024 *1024);

                         uploadAjaxCall = $.ajax({
                             url: url,
                             dataType: 'json',
                             cache: false,
                             contentType: false,
                             processData: false,
                             data: form_data,
                             type: 'POST',

                             success: function (res) {
                                 var uploadedObj = JSON.parse(JSON.stringify(res));

                                 console.log(uploadedObj);
                                 if(uploadedObj.status_code == 200){

                                     var imgElement = $('<img />'),
                                         imageWrapper = $("<div></div>",{ class: "img-wrapper" }),
                                         removeImage = $("<a></a>",{ class: "remove-image", href: removeUrl, 'data-file-name': uploadedObj.data.file_name}),
                                         removeIcon = $('<i></i>', {class: 'ion-close-round'});

                                     $(imgElement).attr('src', uploadedFolder + uploadedObj.data.file_name).appendTo(imageWrapper);

                                     $(removeIcon).appendTo(removeImage);
                                     $(removeImage).appendTo(imageWrapper);
                                     $(imageWrapper).appendTo(multipleImages);

                                     var imageNames = $('[name="uploaded-image-names"]').val();
                                     imageNames += uploadedObj.data.file_name + ',';
                                     $('[name="uploaded-image-names"]').val(imageNames)

                                     $(uploadStatus).text('Done');
                                 }else $(uploadStatus).text(uploadedVideoObj.message);

                             },

                             xhr: function(){
                                 var xhr = $.ajaxSettings.xhr();
                                 if (xhr.upload) {
                                     xhr.upload.addEventListener('progress', function(event) {
                                         var percent = 0;
                                         var position = event.loaded || event.position;
                                         var total = event.total;
                                         if (event.lengthComputable) {

                                             percent = Math.ceil(position / total * 100);
                                             $(uploadProgress).css('width', percent + '%');
                                             $(uploadStatus).text('Uploading... ' + percent + ' %');
                                         }
                                     }, true);
                                 }
                                 return xhr;
                             },

                             mimeType:"multipart/form-data"
                         });
                     }
                 }
             }
         }
     }

     function removeFile(removeBtn){
         var fileName = $(removeBtn).attr('data-file-name'),
             url = $(removeBtn).attr('href');

         var values = {
             'image_name': fileName,
             'admin_id': '<?php echo $admin->id; ?>'
         };

         var a = $.ajax({
             url: url,
             dataType: 'json',
             cache: false,
             data: values,
             type: 'POST',
             success: function(res) {
                 var uploadedObj = JSON.parse(JSON.stringify(res));

                 if(uploadedObj.status_code == 201) $(uploadStatus).text(uploadedObj.message);

                 $(removeBtn).closest('.img-wrapper').remove();
                 $(uploadStatus).text('Successfully Deleted');

                 var imageNames = $('[name="uploaded-image-names"]').val();
                 var updatedNames = '';
                 var nameArray = imageNames.split(',');
                 if(nameArray[nameArray.length-1].trim() == '') nameArray.splice(nameArray.length-1, 1);

                 $(nameArray).each(function(key, value){
                     if(fileName == value)  nameArray.splice(key, 1);
                     else updatedNames += value + ','

                 });

                 $('[name="uploaded-image-names"]').val(updatedNames);

                 $(uploadProgress).css('width', '0px');
             }
         });
     }


     function removeExistingFile(removeBtn){
         var fileName = $(removeBtn).attr('data-file-name'),
             url = $(removeBtn).attr('href');

         var values = {
             'image_name': fileName,
             'admin_id': '<?php echo $admin->id; ?>'
         };

         var a = $.ajax({
             url: url,
             dataType: 'json',
             cache: false,
             data: values,
             type: 'POST',
             success: function(res) {
                 var uploadedObj = JSON.parse(JSON.stringify(res));

                 if(uploadedObj.status_code == 201) $(uploadStatus).text(uploadedObj.message);

                 $(removeBtn).closest('.img-wrapper').remove();
                 $(uploadStatus).text('Successfully Deleted');

                 var imageNames = $('[name="removed-image-ids"]').val();
                 imageNames += $(removeBtn).data('image-id') + ',';

                 $('[name="removed-image-ids"]').val(imageNames);

                 $(uploadProgress).css('width', '0px');
             }
         });
     }


     function renderNewCB(combinedCB, newCbId, newCbText){
         var cbWrapper = $('<div>'),
             cbInput = $('<input>', {
                 type: 'checkbox', id: newCbId, name: 'combined-attr', value: newCbId, 'checked': 'checked'
             }).appendTo(cbWrapper),
             cbLabel = $('<label>', { text: newCbText, for: newCbId });

         cbLabel.appendTo(cbWrapper);
         cbWrapper.appendTo(combinedCB);
     }


     function renderCombinedAttr(combinedCB, newCbId, newCbText, attrQty){

         var newId = newCbId.replace(/atr/g, '');
         newId = newId.replace(/val/g, '');

         var cbWrapper = $('<div>', { class: 'combined-attr-inner' }),
             cbLabel = $('<label>', { text: newCbText, for: newId }).appendTo(cbWrapper),
             qtyInput = $('<input>', { placeholder: 'Quantity', name: 'qty' + newId, id: newId, class: 'qty'}).appendTo(cbWrapper);

         if(attrQty != null){
             if(attrQty[newId] != 'undefined') qtyInput.val(attrQty[newId]);
         }

         cbWrapper.appendTo(combinedCB);
     }


     function attrMappingUpdate(checkedAttr, attrQty){
         var combinedCB = $('#combined-cb');
         combinedCB.html('');

        $(checkedAttr).each(function(key, value){
            var newCbId = '';
            var newCbText = '';
            $(value).each(function(key, value){

                newCbId += value + '_';
                newCbText += $('label[for="' + value + '"]').text() + ' + ';
            });

            newCbId = newCbId.slice(0, -1);
            newCbText = newCbText.slice(0, -3);

            renderNewCB(combinedCB, newCbId, newCbText);

        });

     }


     function attrMappingAdd(checkedAttr, checkedAssociativeAttr){
         var combinedCB = $('#combined-cb');
         combinedCB.html('');


         if(checkedAttr.length == 1){
             /*=========ONE=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 var cbId = checkedAssociativeAttr[0][j],
                     cbText = $('label[for="' + cbId + '"]').text(),
                     newCbText = cbText;
                 newCbId = cbId;

                 renderNewCB(combinedCB, newCbId, newCbText);
             }

         }else if(checkedAttr.length == 2){
             /*=========TWO=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){

                     var cbId1 = checkedAssociativeAttr[0][j],
                         cbId2 = checkedAssociativeAttr[1][k],
                         cbText1 = $('label[for="' + cbId1 + '"]').text(),
                         cbText2 = $('label[for="' + cbId2 + '"]').text(),
                         newCbText = cbText1 + ' + ' + cbText2,
                         newCbId = cbId1 + '_' + cbId2;

                     renderNewCB(combinedCB, newCbId, newCbText);
                 }
             }

         }else if(checkedAttr.length == 3){
             /*=========THREE=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){

                         var cbId1 = checkedAssociativeAttr[0][j],
                             cbId2 = checkedAssociativeAttr[1][k],
                             cbId3 = checkedAssociativeAttr[2][l],
                             cbText1 = $('label[for="' + cbId1 + '"]').text(),
                             cbText2 = $('label[for="' + cbId2 + '"]').text(),
                             cbText3 = $('label[for="' + cbId3 + '"]').text(),
                             newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3,
                             newCbId = cbId1 + '_' + cbId2 + '_' + cbId3;

                         renderNewCB(combinedCB, newCbId, newCbText);
                     }
                 }
             }

         }else if(checkedAttr.length == 4){
             /*=========FOUR=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){

                             var cbId1 = checkedAssociativeAttr[0][j],
                                 cbId2 = checkedAssociativeAttr[1][k],
                                 cbId3 = checkedAssociativeAttr[2][l],
                                 cbId4 = checkedAssociativeAttr[3][m],
                                 cbText1 = $('label[for="' + cbId1 + '"]').text(),
                                 cbText2 = $('label[for="' + cbId2 + '"]').text(),
                                 cbText3 = $('label[for="' + cbId3 + '"]').text(),
                                 cbText4 = $('label[for="' + cbId4 + '"]').text(),
                                 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4,
                                 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4;

                             renderNewCB(combinedCB, newCbId, newCbText);
                         }
                     }
                 }
             }

         }else if(checkedAttr.length == 5){
             /*=========FIVE=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){

                                 var cbId1 = checkedAssociativeAttr[0][j],
                                     cbId2 = checkedAssociativeAttr[1][k],
                                     cbId3 = checkedAssociativeAttr[2][l],
                                     cbId4 = checkedAssociativeAttr[3][m],
                                     cbId5 = checkedAssociativeAttr[4][n],
                                     cbText1 = $('label[for="' + cbId1 + '"]').text(),
                                     cbText2 = $('label[for="' + cbId2 + '"]').text(),
                                     cbText3 = $('label[for="' + cbId3 + '"]').text(),
                                     cbText4 = $('label[for="' + cbId4 + '"]').text(),
                                     cbText5 = $('label[for="' + cbId5 + '"]').text(),
                                     newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5,
                                     newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5;

                                 renderNewCB(combinedCB, newCbId, newCbText);
                             }
                         }
                     }
                 }
             }
         }else if(checkedAttr.length == 6){
             /*=========SIX=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){
								 for(var o = 0; o < checkedAssociativeAttr[5].length; o++){

									 var cbId1 = checkedAssociativeAttr[0][j],
										 cbId2 = checkedAssociativeAttr[1][k],
										 cbId3 = checkedAssociativeAttr[2][l],
										 cbId4 = checkedAssociativeAttr[3][m],
										 cbId5 = checkedAssociativeAttr[4][n],
										 cbId6 = checkedAssociativeAttr[5][o],
										 cbText1 = $('label[for="' + cbId1 + '"]').text(),
										 cbText2 = $('label[for="' + cbId2 + '"]').text(),
										 cbText3 = $('label[for="' + cbId3 + '"]').text(),
										 cbText4 = $('label[for="' + cbId4 + '"]').text(),
										 cbText5 = $('label[for="' + cbId5 + '"]').text(),
										 cbText6 = $('label[for="' + cbId6 + '"]').text(),
										 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5+ ' + ' + cbText6,
										 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5+ '_' + cbId6;

									 renderNewCB(combinedCB, newCbId, newCbText);
								 }
                             }
                         }
                     }
                 }
             }
         }else if(checkedAttr.length == 7){
             /*=========SEVEN=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){
								 for(var o = 0; o < checkedAssociativeAttr[5].length; o++){
									 for(var p = 0; p < checkedAssociativeAttr[6].length; p++){

										 var cbId1 = checkedAssociativeAttr[0][j],
											 cbId2 = checkedAssociativeAttr[1][k],
											 cbId3 = checkedAssociativeAttr[2][l],
											 cbId4 = checkedAssociativeAttr[3][m],
											 cbId5 = checkedAssociativeAttr[4][n],
											 cbId6 = checkedAssociativeAttr[5][o],
											 cbId7 = checkedAssociativeAttr[6][p],
											 cbText1 = $('label[for="' + cbId1 + '"]').text(),
											 cbText2 = $('label[for="' + cbId2 + '"]').text(),
											 cbText3 = $('label[for="' + cbId3 + '"]').text(),
											 cbText4 = $('label[for="' + cbId4 + '"]').text(),
											 cbText5 = $('label[for="' + cbId5 + '"]').text(),
											 cbText6 = $('label[for="' + cbId6 + '"]').text(),
											 cbText7 = $('label[for="' + cbId7 + '"]').text(),
											 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5 + ' + ' + cbText6 + ' + ' + cbText7,
											 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5 + '_' + cbId6 + '_' + cbId7;

										 renderNewCB(combinedCB, newCbId, newCbText);
									 }
								 }
                             }
                         }
                     }
                 }
             }
         }else if(checkedAttr.length == 8){
             /*=========EIGHT=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){
								 for(var o = 0; o < checkedAssociativeAttr[5].length; o++){
									 for(var p = 0; p < checkedAssociativeAttr[6].length; p++){
										 for(var q = 0; q < checkedAssociativeAttr[7].length; q++){

											 var cbId1 = checkedAssociativeAttr[0][j],
												 cbId2 = checkedAssociativeAttr[1][k],
												 cbId3 = checkedAssociativeAttr[2][l],
												 cbId4 = checkedAssociativeAttr[3][m],
												 cbId5 = checkedAssociativeAttr[4][n],
												 cbId6 = checkedAssociativeAttr[5][o],
												 cbId7 = checkedAssociativeAttr[6][p],
												 cbId8 = checkedAssociativeAttr[7][q],
												 cbText1 = $('label[for="' + cbId1 + '"]').text(),
												 cbText2 = $('label[for="' + cbId2 + '"]').text(),
												 cbText3 = $('label[for="' + cbId3 + '"]').text(),
												 cbText4 = $('label[for="' + cbId4 + '"]').text(),
												 cbText5 = $('label[for="' + cbId5 + '"]').text(),
												 cbText6 = $('label[for="' + cbId6 + '"]').text(),
												 cbText7 = $('label[for="' + cbId7 + '"]').text(),
												 cbText8 = $('label[for="' + cbId8 + '"]').text(),
												 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5 + ' + ' + cbText6 + ' + ' + cbText7 + ' + ' + cbText8,
												 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5 + '_' + cbId6 + '_' + cbId7 + '_' + cbId8;

											 renderNewCB(combinedCB, newCbId, newCbText);
										 }
									 }
								 }
                             }
                         }
                     }
                 }
             }
         }else if(checkedAttr.length == 9){
             /*=========NINE=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){
								 for(var o = 0; o < checkedAssociativeAttr[5].length; o++){
									 for(var p = 0; p < checkedAssociativeAttr[6].length; p++){
										 for(var q = 0; q < checkedAssociativeAttr[7].length; q++){
											 for(var r = 0; r < checkedAssociativeAttr[8].length; r++){

												 var cbId1 = checkedAssociativeAttr[0][j],
													 cbId2 = checkedAssociativeAttr[1][k],
													 cbId3 = checkedAssociativeAttr[2][l],
													 cbId4 = checkedAssociativeAttr[3][m],
													 cbId5 = checkedAssociativeAttr[4][n],
													 cbId6 = checkedAssociativeAttr[5][o],
													 cbId7 = checkedAssociativeAttr[6][p],
													 cbId8 = checkedAssociativeAttr[7][q],
													 cbId9 = checkedAssociativeAttr[8][r],
													 cbText1 = $('label[for="' + cbId1 + '"]').text(),
													 cbText2 = $('label[for="' + cbId2 + '"]').text(),
													 cbText3 = $('label[for="' + cbId3 + '"]').text(),
													 cbText4 = $('label[for="' + cbId4 + '"]').text(),
													 cbText5 = $('label[for="' + cbId5 + '"]').text(),
													 cbText6 = $('label[for="' + cbId6 + '"]').text(),
													 cbText7 = $('label[for="' + cbId7 + '"]').text(),
													 cbText8 = $('label[for="' + cbId8 + '"]').text(),
													 cbText9 = $('label[for="' + cbId9 + '"]').text(),
													 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5 + ' + ' + cbText6 + ' + ' + cbText7 + ' + ' + cbText8 + ' + ' + cbText9,
													 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5 + '_' + cbId6 + '_' + cbId7 + '_' + cbId8 + '_' + cbId9;

												 renderNewCB(combinedCB, newCbId, newCbText);
											 }
										 }
									 }
								 }
                             }
                         }
                     }
                 }
             }
         }else if(checkedAttr.length == 10){
             /*=========NINE=========*/
             for(var j = 0; j < checkedAssociativeAttr[0].length; j++){
                 for(var k = 0; k < checkedAssociativeAttr[1].length; k++){
                     for(var l = 0; l < checkedAssociativeAttr[2].length; l++){
                         for(var m = 0; m < checkedAssociativeAttr[3].length; m++){
                             for(var n = 0; n < checkedAssociativeAttr[4].length; n++){
								 for(var o = 0; o < checkedAssociativeAttr[5].length; o++){
									 for(var p = 0; p < checkedAssociativeAttr[6].length; p++){
										 for(var q = 0; q < checkedAssociativeAttr[7].length; q++){
											 for(var r = 0; r < checkedAssociativeAttr[8].length; r++){
												 for(var s = 0; s < checkedAssociativeAttr[9].length; s++){

													 var cbId1 = checkedAssociativeAttr[0][j],
														 cbId2 = checkedAssociativeAttr[1][k],
														 cbId3 = checkedAssociativeAttr[2][l],
														 cbId4 = checkedAssociativeAttr[3][m],
														 cbId5 = checkedAssociativeAttr[4][n],
														 cbId6 = checkedAssociativeAttr[5][o],
														 cbId7 = checkedAssociativeAttr[6][p],
														 cbId8 = checkedAssociativeAttr[7][q],
														 cbId9 = checkedAssociativeAttr[8][r],
														 cbId10 = checkedAssociativeAttr[9][s],
														 cbText1 = $('label[for="' + cbId1 + '"]').text(),
														 cbText2 = $('label[for="' + cbId2 + '"]').text(),
														 cbText3 = $('label[for="' + cbId3 + '"]').text(),
														 cbText4 = $('label[for="' + cbId4 + '"]').text(),
														 cbText5 = $('label[for="' + cbId5 + '"]').text(),
														 cbText6 = $('label[for="' + cbId6 + '"]').text(),
														 cbText7 = $('label[for="' + cbId7 + '"]').text(),
														 cbText8 = $('label[for="' + cbId8 + '"]').text(),
														 cbText9 = $('label[for="' + cbId9 + '"]').text(),
														 cbText10 = $('label[for="' + cbId10 + '"]').text(),
														 newCbText = cbText1 + ' + ' + cbText2 + ' + ' + cbText3 + ' + ' + cbText4 + ' + ' + cbText5 + ' + ' + cbText6 + ' + ' + cbText7 + ' + ' + cbText8 + ' + ' + cbText9 + ' + ' + cbText10,
														 newCbId = cbId1 + '_' + cbId2 + '_' + cbId3 + '_' + cbId4 + '_' + cbId5 + '_' + cbId6 + '_' + cbId7 + '_' + cbId8 + '_' + cbId9 + '_' + cbId10;

													 renderNewCB(combinedCB, newCbId, newCbText);
												 }
											 }
										 }
									 }
								 }
                             }
                         }
                     }
                 }
             }
         }
     }


     /*MAIN SCRIPTS*/
     (function ($) {
         "use strict";

         $("input.qty").change(function(){
             $('input[name="prev_attr"]').val(attrIdsFromDB.join(','));
         });

         $('[data-popup]').on('click', function(e){
             e.stopPropagation();
             e.preventDefault();

             var $this = $(this),
                 targetDiv = $this.data('popup');

             var checkedAttr = [];
             $($('#combined-attr-wrapper').find('label')).each(function(){

                 var attrValues = $(this).attr('for');
                 var attrValueArr = attrValues.split('_');
                 var checkedAssociativeAttr = [];

                 for(var  i = 0; i < attrValueArr.length; i++){
                     var attrValueIdArr = attrValueArr[i].split('-');
                     var first = 'atr' + attrValueIdArr[0];
                     var last = 'val' + attrValueIdArr[1];
                     var attrValueId = first + '-' + last;

                     $('#' + attrValueId).prop('checked', true);
                     $('#' + attrValueId).closest('.attribute-cb-wrapper').find('.attribute-cb').find('input').prop('checked', true);

                     checkedAssociativeAttr.push(attrValueId);
                 }

                 checkedAttr.push(checkedAssociativeAttr);
             });

             attrMappingUpdate(checkedAttr, attrQty);
             $(targetDiv).addClass('active');

         });


         $('#combined-attr-btn').on('click', function(e){
             var selectedAttr = [];
             e.preventDefault();
             e.stopPropagation();
             var combinedAttrWrapper = $('#combined-attr-wrapper'),
                 $this = $(this);

             combinedAttrWrapper.html('');

             $($this.closest('form').find('input[type="checkbox"]:checked')).each(function(event){
                 var attr = $(this).val();
                 attr = attr.replace(/atr|val/g, '');
                 selectedAttr.push(attr);

                 renderCombinedAttr(combinedAttrWrapper,  $(this).val(), $('label[for="' + $(this).val() + '"]').html(), attrQty);
             });

             if(selectedAttr.length > 0)  $(".inventory").removeClass('active');
             else $(".inventory").addClass('active');


             selectedAttr.sort();
             attrFromDB.sort();

             var isSame = (selectedAttr.length == attrFromDB.length) && selectedAttr.every(function(element, index){
                     return element == attrFromDB[index];
                 });

             if(!isSame){
                 if(attrIdsFromDB.length < 1) attrIdsFromDB.push('empty');
                 $('input[name="prev_attr"]').val(attrIdsFromDB.join(','));
             }

             $this.closest('.item-wrapper').removeClass('active');

         });


         $('input[type="checkbox"]').change(function() {
             var $this = $(this);

             if($this.attr('name') == 'attribute'){
                 if(this.checked) $this.closest('.attribute-cb-wrapper').find('.attribute-value-cb').find('input').prop('checked', true);
                 else $this.closest('.attribute-cb-wrapper').find('.attribute-value-cb').find('input').prop('checked', false);

             }else if($this.attr('name') == 'attribute-value'){
                 if(!this.checked) {
                     var haveChecked = false;
                     $($this.closest('.attribute-cb-wrapper').find('.attribute-value-cb').find('input')).each(function(){
                         if(this.checked) haveChecked = true;
                     });

                     if(!haveChecked) $this.closest('.attribute-cb-wrapper').find('.attribute-cb').find('input').prop('checked', false);
                 }else $this.closest('.attribute-cb-wrapper').find('.attribute-cb').find('input').prop('checked', true);
             }

             var checkedAttr = [];
             var checkedAssociativeAttr = [];
             $($(this).closest('.attribute-wrapper').find('.attribute-cb').find('input:checked')).each(function(key, value){
                 checkedAttr.push($(this).attr('id'));
                 checkedAssociativeAttr[key] = [];
             });

             var combinedCB = $('#combined-cb');
             combinedCB.html('');


             if(checkedAttr.length > 0){
                 var combinedCBHeading = $('<h6>', { text: 'Selected Combination', class: 'mt-15 mb-15' }).appendTo(combinedCB);
             }

             $($(this).closest('.attribute-wrapper').find('.attribute-value-cb').find('input:checked')).each(function(){
                 var attrValueId = $(this).attr('id');

                 $(checkedAttr).each(function(key, value){
                     if(attrValueId.indexOf(value) >= 0) {
                         checkedAssociativeAttr[key].push(attrValueId);
                         return;
                     }
                 });
             });

             attrMappingAdd(checkedAttr, checkedAssociativeAttr, attrQty);
         });


         $('[data-validation="true"]').on('submit', function(e){
             var hasError = false;
             if($('#combined-attr-wrapper').find('input').length < 1){
                 var inv = $('#inventory');

                 if(isEmpty(inv.val()) || inv.val() < 1) {
                     inv.addClass('has-error');
                     inv.after('<h6 class="err-msg">Inventory is required' + '</h6>');
                     hasError = true;
                 }
             }else{
                 $($(this).find('.combined-attr-inner input')).each(function(e){
                     var $this = $(this);

                     if(isEmpty($this.val()) || inv.val() < 1) {
                         hasError = true;
                         $this.addClass('has-error');
                         $this.after('<h6 class="err-msg">required' + '</h6>');
                     }
                 });
             }

             if(hasError){
                 e.stopPropagation();
                 e.preventDefault();
                 return false;
             }
         });

         $(document).on('click touch', function(e) {
             $('.popup-area').removeClass('active');
         });

         $('.popup-area .item').on('click touch', function(e) {
             e.stopPropagation();
         });


         window.onbeforeunload = function() {
             if(refreshDisable){
                 return "Are you sure you want to leave?";
             }
         }


         $("#file-upload").closest('form').on('submit', function(){
             if(submitDisable) {
                 alert("Upload is in Progress");
                 return false;
             }
         });

         $("#file-upload").change(function (){
             upload($(this));
         });


         $(document).on('click', '.remove-image', function(e) {
             e.preventDefault();
             e.stopPropagation();
             if(confirm("Are You Sure?")){
                 var $this = $(this);
                 removeFile($this)
             }
             return false;
         });

     })(jQuery);
 </script>

</body>