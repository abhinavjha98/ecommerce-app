package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    @SerializedName("slider")
    @Expose
    public ArrayList<SliderMain> mSliderMains;

    @SerializedName("category")
    @Expose
    public ArrayList<CategoryModel> categoryModel;

    @SerializedName("featured")
    @Expose
    public ArrayList<ProductModel> featureProduct;

    @SerializedName("popular")
    @Expose
    public ArrayList<ProductModel> popularProduct;

    @SerializedName("recent")
    @Expose
    public ArrayList<ProductModel> recentProduct;

    @SerializedName("existing")
    @Expose
    public List<String> existing;
}
