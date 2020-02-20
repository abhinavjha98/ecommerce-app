
function isEmpty(value){
	return (value.length < 1);
}

function validEmail(v) {
    var r = new RegExp("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    return (v.match(r) == null) ? false : true;
}

function lengthChecker(value){
	return (value.length < 8);
}

function isExists(elem){
	if ($(elem).length > 0) {
		return true;
	}
	return false;
}

function changeUploadedImage(e) {
	var reader = new FileReader();
    var _URL = window.URL || window.webkitURL;

	reader.onload = function (e) {
		$('#uploaded-image').attr('src', e.target.result);
		$('#uploaded-image').addClass('active').fadeIn(2000);
		$('#upload-content').hide();
	};
	reader.readAsDataURL(e.target.files[0]);
}

function validateImage(input, e){
	var imageName = $(input).val(),
		extension = imageName.substring(imageName.lastIndexOf('.') + 1).toLowerCase();

	if (extension == 'jpg' || extension == 'png' || extension == 'jpeg' || extension == 'gif') {
		changeUploadedImage(e);
	} else {
		$(input).val("");
		alert("Invalid Image file.");
	}
}

function ucFirst(str) {
    str = str.toLowerCase().replace(/\b[a-z]/g, function(letter) {
        return letter.toUpperCase();
    });
    str = str.replace('_', ' ');
    return str;
}

function videoType(input){
    var videoField = $(input).data('video');
    $('.video-field').removeClass('active');
    $('.video-field').find('input').removeAttr('data-required');
    $(videoField).addClass('active');
    $(videoField).find('input').attr('data-required', true);
}


(function ($) {
    "use strict";
    
    videoType('input:radio[name="type"]:checked');

    $('input:radio[name="type"]').change(function () {
        videoType($(this));
    });

    
    $(window).bind("load", function () {
        $("#preloader").fadeOut();
    });


    $(document).on('click', '.removable-image', function(e) {
        e.preventDefault();
        e.stopPropagation();
        if(confirm("Are You Sure?")){
            var $this = $(this);
            removeExistingFile($this)
        }
        return false;
    });


    $('[data-validation]').on('submit', function(e){
        $('.image-upload').removeClass('has-error');
        $('input').removeClass('has-error');
        $('select').removeClass('has-error');
        $('textarea').removeClass('has-error');
        $('.err-msg').remove();
        var hasError = false;

        $($(this).find('[data-required]')).each(function(){
            var $this = $(this);

            if(($this.attr('type') != 'hidden') && ($this.data('required') != false)){

                if($this.data('required') == 'dropdown'){
                    if($(this).prop('selectedIndex') < 1){
                        hasError = true;
                        $this.addClass('has-error');
                        $this.after('<h6 class="err-msg">' + ucFirst($this.attr('name')) + ' is required.' + '</h6>');
                    }
                }else if($this.data('required') == 'image'){

                    if(isEmpty($this.val())){
                        if(isEmpty($this.attr('value'))){
                            hasError = true;
                            var imageUpload = $this.closest('.image-upload');
                            imageUpload.addClass('has-error');
                            imageUpload.after('<h6 class="err-msg">' + ucFirst($this.attr('name')) + ' is required.' + '</h6>');
                        }
                    }

                }else if($this.data('required') == true){
                    if($this.attr('type') == 'file') var val = $this.attr('value');
                    else var val = $.trim($this.val());

                    if(isEmpty(val)){
                        hasError = true;
                        $this.addClass('has-error');
                        $this.after('<h6 class="err-msg">' + ucFirst($this.attr('name')) + ' is required.' + '</h6>');
                    }
                }else if($this.data('required') == 'numeric'){
                    if(isEmpty($this.val())){
                        hasError = true;
                        $this.addClass('has-error');
                        $this.after('<h6 class="err-msg">' + ucFirst($this.attr('name')) + ' is required.' + '</h6>');

                    }else if(!$.isNumeric($this.val()) || $this.val() == 0){
                        hasError = true;
                        $this.addClass('has-error');
                        $this.after('<h6 class="err-msg">' + ucFirst($this.attr('name')) + ' must be valid numeric.' + '</h6>');
                    }
                }
            }
        });

        if(hasError) {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }
	});


    $(document).on('click', '[data-confirm]', function(e){
        if (!confirm($(this).data('confirm'))) {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }
    });

	if(isExists('.uploaded-image')){
		if(!isEmpty($('.uploaded-image').attr('src'))){
			$(this).find('.upload-content').hide();
		}
	}

    $('.multiple-image-input').on('change', function (e) {
        var $this = $(this),
            fileCount = $this.get(0).files.length,
            uploadedImages = '<div id="uploaded-images"></div>';

        if(maxUploadedFileCount < fileCount){
            alert('You can upload maximum ' + maxUploadedFileCount + ' files per upload.');
        }else{

            for (var i = 0; i < fileCount; ++i) {
                var _URL = window.URL || window.webkitURL,
                    file = $this[0].files[i],
                    img = new Image();

                if(file){
                    var fileType = file["type"],
                        fileName = file["name"],
                        fileSize = file["size"] / (1024 *1024),
                        validImageTypes = ["image/jpeg", "image/png"];

                    if ($.inArray(fileType, validImageTypes) < 0) {
                        $(this).val('');
                        alert( fileName + ' is Invalid');
                    }else if(fileSize > maxUploadedFile){
                        $(this).val('');
                        alert(fileName + ' : ' + fileSize.toFixed(2) + 'MB (Maximum file size : ' + maxUploadedFile + 'MB)');
                    }else{
                        img.src = _URL.createObjectURL(file);
                        img.onload = function() {
                            
                            var imgElement = $('<img />');
                            $(imgElement).attr('src', img.src);
                            var masonryGrid = $("<div></div>",{class: "multiple-img-wrapper"});

                            $(imgElement).appendTo(masonryGrid);
                            masonryGrid.appendTo('#multiple-images');
                        };
                    }
                }
            }
        }
    });


	$('.image-input').on('change', function (e) {
        var uploadedImage = $(this).closest('.image-upload').find('.uploaded-image'),
            uploadContent = $(this).closest('.image-upload').find('.upload-content');
        $(uploadedImage).attr('src', '');
        $(uploadContent).show();

        var _URL = window.URL || window.webkitURL,
            file = $(this)[0].files[0],
            img = new Image(),
            targetResolution = $(this).data("traget-resolution");

        if(file){
            var fileType = file["type"],
                fileSize = file["size"] / (1024 *1024),
                validImageTypes = ["image/jpeg", "image/png"];

            if ($.inArray(fileType, validImageTypes) < 0) {
                $(this).val('');
                alert("Invalid FileType");
            }else if(fileSize > maxUploadedFile){
                $(this).val('');
                alert('Uploaded Image : ' + fileSize.toFixed(2) + 'MB (Maximum file size : ' + maxUploadedFile + 'MB)');
            }else{
                img.src = _URL.createObjectURL(file);
                img.onload = function() {
                    var imgwidth = this.width,
                        imgheight = this.height;

                    if(targetResolution) $('input[name=' + targetResolution + ']').attr('value', imgwidth + ':' + imgheight);

                    $(uploadedImage).attr('src', img.src);
                    $(uploadedImage).addClass('active').fadeIn(2000);
                    $(uploadContent).hide();
                };
            }
        }
	});

	$(window).bind("load", function() {
		if(isExists('.masonry-grid')){
			$('.masonry-grid').masonry({
				itemSelector: '.masonry-item',
				percentPosition: true,
			});
		}
	});

})(jQuery);
