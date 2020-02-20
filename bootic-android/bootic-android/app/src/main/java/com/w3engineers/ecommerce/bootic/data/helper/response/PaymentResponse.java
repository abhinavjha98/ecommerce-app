package com.w3engineers.ecommerce.bootic.data.helper.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.w3engineers.ecommerce.bootic.data.helper.models.PaymentConfirmationModel;

public class PaymentResponse {

    @SerializedName("status_code")
    @Expose
    public int statusCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public PaymentConfirmationModel model;

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", model=" + model +
                '}';
    }
}
