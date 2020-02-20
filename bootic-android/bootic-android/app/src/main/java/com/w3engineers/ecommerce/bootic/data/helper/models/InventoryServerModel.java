package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InventoryServerModel {
    @SerializedName("product")
    @Expose
    public String product;

    @SerializedName("price")
    @Expose
    public String price;

    @SerializedName("inventory")
    @Expose
    public String inventory;

    @SerializedName("quantity")
    @Expose
    public String quantity;
}
