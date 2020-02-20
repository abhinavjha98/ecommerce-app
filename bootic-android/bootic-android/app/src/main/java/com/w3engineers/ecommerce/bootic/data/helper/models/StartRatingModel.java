package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StartRatingModel {
    @SerializedName("1")
    @Expose
    public  String ratingOne;

    @SerializedName("2")
    @Expose
    public String ratingTwo;

    @SerializedName("3")
    @Expose
    public String ratingThree;

    @SerializedName("4")
    @Expose
    public String ratingFour;

    @SerializedName("5")
    @Expose
    public String ratingFive;

}
