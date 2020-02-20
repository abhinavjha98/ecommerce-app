package com.w3engineers.ecommerce.bootic.data.helper.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.w3engineers.ecommerce.bootic.data.helper.models.CartModel;

import java.util.List;

public class CartModelResponse {

    @SerializedName("status_code")
    @Expose
    public int statusCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<CartModel> mCartModelList;
}
