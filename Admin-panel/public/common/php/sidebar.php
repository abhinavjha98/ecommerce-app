<?php

$current = basename($_SERVER["SCRIPT_FILENAME"]);
$index = $users = $setting = $admob = $site_config = $push_notifications = $products = $categories = $sliders = $payment = $attributes = $orders = "";


if($current == "index.php") $index = "active";
else if(($current == "categories.php") ||($current == "category-form.php")) $categories = "active";
else if(($current == "products.php") ||($current == "product-form.php")) $products = "active";
else if(($current == "attributes.php") ||($current == "attribute-form.php")) $attributes = "active";
else if(($current == "orders.php") || ($current == "order-detail.php") || ($current == "generate-invoice.php")) $orders = "active";
else if($current == "payment.php") $payment = "active";
else if($current == "users.php") $users = "active";
else if(($current == "sliders.php") ||($current == "slider-form.php")) $sliders = "active";
else if($current == "setting.php") $setting = "active";
else if($current == "admob.php") $admob = "active";
else if($current == "site-config.php") $site_config = "active";
else if(($current == "push-notifications.php") || $current == "push-notification-form.php") $push_notifications = "active";

?>

<div class="sidebar">
    <ul class="sidebar-list">
        <li class="<?php echo $index; ?>"><a href="index.php"><i class="ion-ios-pie"></i><span>Dashboard</span></a></li>
        <li class="<?php echo $categories; ?>"><a href="categories.php"><i class="ion-social-buffer"></i><span>Category</span></a></li>
        <li class="<?php echo $attributes; ?>"><a href="attributes.php"><i class="ion-pricetags"></i><span>Attributes</span></a></li>
        <li class="<?php echo $products; ?>"><a href="products.php"><i class="ion-android-apps"></i><span>Product</span></a></li>
        <li class="<?php echo $orders; ?>"><a href="orders.php"><i class="ion-android-cart"></i><span>Orders</span></a></li>
        <li class="<?php echo $payment; ?>"><a href="payment.php"><i class="ion-cash"></i><span>Payment</span></a></li>
        <li class="<?php echo $users; ?>"><a href="users.php"><i class="ion-person"></i><span>Register Users</span></a></li>
        <li class="<?php echo $admob; ?>"><a href="admob.php"><i class="ion-closed-captioning"></i><span>Admob</span></a></li>
        <li class="<?php echo $push_notifications; ?>"><a href="push-notifications.php"><i class="ion-ios-bell"></i><span>Push Notification</span></a></li>
        <li class="<?php echo $sliders; ?>"><a href="sliders.php"><i class="ion-android-image"></i><span>Sliders</span></a></li>
        <li class="<?php echo $site_config; ?>"><a href="site-config.php"><i class="ion-settings"></i><span>Configuration</span></a></li>
        <li class="<?php echo $setting; ?>"><a href="setting.php"><i class="ion-android-settings"></i><span>Setting</span></a></li>
    </ul>
</div><!--sidebar-->