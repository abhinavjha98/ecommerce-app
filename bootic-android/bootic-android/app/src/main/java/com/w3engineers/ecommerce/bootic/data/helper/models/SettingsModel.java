package com.w3engineers.ecommerce.bootic.data.helper.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingsModel {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("api_token")
    @Expose
    public String api_token;

    @SerializedName("currency_name")
    @Expose
    public String currencyName;


    @SerializedName("currency_type")
    @Expose
    public String currencyPosition;

    @SerializedName("currency_font")
    @Expose
    public String currencyFont;

    @SerializedName("tax")
    @Expose
    public String tax;

    @SerializedName("payment")
    @Expose
    public PaymentCredentialModel paymentModel;

    @SerializedName("admin_address")
    @Expose
    public AddressModel addressModel;


}
