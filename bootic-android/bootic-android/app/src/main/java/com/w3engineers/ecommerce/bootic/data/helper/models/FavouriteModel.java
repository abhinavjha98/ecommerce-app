package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavouriteModel {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("item_id")
    @Expose
    public int itemID;

    @SerializedName("user_id")
    @Expose
    public String UserID;
}
