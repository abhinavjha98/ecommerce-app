package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentConfirmationModel {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("method")
    @Expose
    public String paymentMethod;

    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("user")
    @Expose
    public String UserId;

    @SerializedName("address")
    @Expose
    public String addressId;

    @SerializedName("txn_id")
    @Expose
    public String transactionId;
}
