package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {

    @SerializedName("method")
    @Expose
    public String method;

    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("created")
    @Expose
    public String DateTime;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("tax")
    @Expose
    public String tax;

    @SerializedName("txn_id")
    @Expose
    public String transactionId;

    @SerializedName("ordered_products")
    @Expose
    public List<UserOrderList> userOrderLists;

}
