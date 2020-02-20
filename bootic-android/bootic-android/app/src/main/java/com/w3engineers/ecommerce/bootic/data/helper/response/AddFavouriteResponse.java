package com.w3engineers.ecommerce.bootic.data.helper.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.w3engineers.ecommerce.bootic.data.helper.models.FavouriteModel;

public class AddFavouriteResponse {
    @SerializedName("status_code")
    @Expose
    public int statusCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public FavouriteModel favouriteModel;

    @Override
    public String toString() {
        return "AddFavouriteResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", favouriteModel=" + favouriteModel +
                '}';
    }
}
