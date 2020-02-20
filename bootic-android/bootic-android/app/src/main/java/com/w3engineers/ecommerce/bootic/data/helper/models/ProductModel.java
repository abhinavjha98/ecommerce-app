package com.w3engineers.ecommerce.bootic.data.helper.models;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductModel {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("image_name")
    @Expose
    public String imageUri;

    @SerializedName("image_resolution")
    @Expose
    public String imageResolution;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("prev_price")
    @Expose
    public float previousPrice;

    @SerializedName("current_price")
    @Expose
    public float currentPrice;

    @SerializedName("favourited")
    @Expose
    public int isFavourite;

    public String userID;

}
