package com.w3engineers.ecommerce.bootic.ui.checkout;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.encryption.MCrypt;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.models.EncryptionModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryServerModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InvoiceModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.ActivityCheckOutBinding;
import com.w3engineers.ecommerce.bootic.ui.addcart.CartActivity;
import com.w3engineers.ecommerce.bootic.ui.invoice.InvoiceActivity;
import com.w3engineers.ecommerce.bootic.ui.ordercomplete.OrderCompleteActivity;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;
import com.w3engineers.ecommerce.bootic.ui.shippingaddress.ShippingAddressActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends BaseActivity<CheckOutMvpView, CheckOutPresenter> implements CheckOutMvpView {
    private ActivityCheckOutBinding mBinding;
    private Toolbar toolbar;
    private int paymentMethod;
    private String paymentResponse;
    private static final int BRAINTREE_REQUEST_CODE = 4949;
    private float totalAmount;
    private Loader mLoader;
    private List<CustomProductInventory> inventoryList;
    private String paymentMethods = "";
    private List<InventoryServerModel> serverModels = new ArrayList<>();
    private String address1 = "";
    private String addressID;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_out;
    }

    @Override
    protected void startUI() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mLoader = new Loader(this);
        checkNetConnection();
        presenter.getSettingCredential(this);
        initToolbar();
        getDataFromIntent();
        setValueToView();
        setClickListener(mBinding.layoutIncludeNoNet.buttonRetry);
    }

    /**
     * check internet
     */
    private void checkNetConnection() {
        if (!NetworkHelper.hasNetworkAccess(this)) {
            //no net
            mLoader.stopLoader();
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
            mBinding.scrollview.setVisibility(View.GONE);
        } else {
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
            mBinding.scrollview.setVisibility(View.VISIBLE);
        }
    }



    private void setValueToView() {
        String address = SharedPref.getSharedPref(this).read(Constants.Preferences.USER_ADDRESS);
        AddressModel addressModel = UtilityClass.stringToAddressModel(address);
        if (addressModel != null) {
            addressID = "" + addressModel.id;
            address1 = addressModel.addressLine1 + addressModel.addressLine2
                    + getResources().getString(R.string.city_text) + addressModel.city +
                    getResources().getString(R.string.zip_code) + addressModel.zipCode
                    + getResources().getString(R.string.state_text) + addressModel.state +
                    getResources().getString(R.string.country_text) + addressModel.country;
            mBinding.textViewAddress1.setText(address1);
        }
        if (paymentMethod != 0) {
            if (paymentMethod == 1) {
                mBinding.textViewPayment.setText(getString(R.string.payment_paypal));
            }else if (paymentMethod == 2){
                mBinding.textViewPayment.setText(getString(R.string.creditordebit));
            }
            else {
                mBinding.textViewPayment.setText(getString(R.string.cash_on_delivery));
            }
        }

        String currency = CustomSharedPrefs.getCurrency(this);
        float amount = SharedPref.getSharedPref(this).readFloat(Constants.IntentKey.TOTAL_AMOUNT);
        String tax = SharedPref.getSharedPref(this).read(Constants.Preferences.TAX);
        float taxPrice = (Float.parseFloat(tax) * amount) / 100;


        SharedPref.getSharedPref(this).write(Constants.Preferences.INVOICE_SUB_TOTAL_AMOUNT,UtilityClass.getCurrencySymbolAndAmount(this,amount));
        mBinding.textViewSubtotalAmount.setText( "" + UtilityClass.getCurrencySymbolAndAmount(this,amount));
        mBinding.textViewDiscount.setText(getResources().getString(R.string.tax_text) + tax + "%)");

        SharedPref.getSharedPref(this).write(Constants.Preferences.INVOICE_TAX,UtilityClass.getCurrencySymbolAndAmount(this,taxPrice));
        mBinding.textViewDiscountAmount.setText("" + UtilityClass.getCurrencySymbolAndAmount(this,taxPrice));


        totalAmount = amount + taxPrice;
        SharedPref.getSharedPref(this).write(Constants.Preferences.INVOICE_TOTAL_AMOUNT,UtilityClass.getCurrencySymbolAndAmount(this,totalAmount));
        mBinding.textViewTotalAmount.setText("" + UtilityClass.getCurrencySymbolAndAmount(this,totalAmount));
        mBinding.textTotalCostTitle.setText("" +UtilityClass.getCurrencySymbolAndAmount(this,totalAmount));



        mBinding.buttonContinue.setOnClickListener(this);

    }

    private void getDataFromIntent() {

        //if null then 0
        paymentMethod = getIntent() == null ? 0 : getIntent().getIntExtra(Constants.IntentKey.PAYMENT_METHOD, 0);

        //if null then ""
        paymentResponse = getIntent() == null ? "" : getIntent().getStringExtra(Constants.IntentKey.PAYMENT_RESPONSE);

    }

    /**
     * init toolbar with title
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(getString(R.string.check_out));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void stopUI() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_continue) {
            if (paymentMethod==3){//cash on delivery
                if (NetworkHelper.hasNetworkAccess(this)){
                    getDataFromDatabaseAndDoPayment(this.getResources().getString(R.string.cash_on_delivery));
                }else {
                    Toast.makeText(this,getResources().getString(R.string.check_net_connection), Toast.LENGTH_SHORT).show();
                }

            }else {
                if (NetworkHelper.hasNetworkAccess(this)){
                    onBraintreeSubmit();
                }else {
                    Toast.makeText(this,getResources().getString(R.string.check_net_connection), Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if (view.getId() == R.id.button_retry){
            presenter.getSettingCredential(this);
        }
    }

    @Override
    protected CheckOutPresenter initPresenter() {
        return new CheckOutPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BRAINTREE_REQUEST_CODE) {
            if (RESULT_OK == resultCode) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                String paymentNonce = result.getPaymentMethodNonce().getNonce();

                String payAmount = String.valueOf(totalAmount);

                mLoader.show();
                presenter.sendPaymentNonceToServer(this, paymentNonce, payAmount);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                    //action cancel
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    /**
     * this api is for initializing {@link DropInRequest} to get callback in {@link #onActivityResult}
     */
    private void onBraintreeSubmit() {
        if (paymentResponse != null) {
            DropInRequest dropInRequest = new DropInRequest().clientToken(paymentResponse);
            // dropInRequest.vaultManager(true);
            if (paymentMethod != 1)
                dropInRequest.disablePayPal();
            startActivityForResult(dropInRequest.getIntent(this), BRAINTREE_REQUEST_CODE);
        }
    }

    @Override
    public void onPaymentNonceSuccess(String response) {
        Log.d("noncePay", response);
        if (response != null) {
            if (!response.toLowerCase().contains("error")){
                String[] separated = response.split("_");
                if (separated.length == 2)
                    paymentMethods = UtilityClass.capFirstLetter(separated[0]) + " " + UtilityClass.capFirstLetter(separated[1]);
                else paymentMethods = response;
                getDataFromDatabaseAndDoPayment(paymentMethods);
            }else {
                Toast.makeText(this, this.getResources().getString(R.string.error_transaction), Toast.LENGTH_SHORT).show();
            }

        }
        mLoader.stopLoader();

    }


    /**
     * Get data from database and do payment
     */
    private void getDataFromDatabaseAndDoPayment(String paymentMethod){

        AsyncTask.execute(() -> {
            inventoryList = DatabaseUtil.on().getAllCodes();
            for (CustomProductInventory inventory : inventoryList) {
                InventoryServerModel model = new InventoryServerModel();
                model.inventory = "" + inventory.inventory_id;
                model.price = "" + inventory.price;
                model.product = "" + inventory.product_id;
                model.quantity = "" + inventory.currentQuantity;
                serverModels.add(model);
            }
            String inventoryLists = UtilityClass.objectToStrings(serverModels);


            EncryptionModel encryptionModel=new EncryptionModel();
            encryptionModel.api_token=Constants.ServerUrl.API_TOKEN;
            encryptionModel.method=paymentMethod;
            encryptionModel.address=addressID;
            encryptionModel.ordered_products=inventoryLists;
            encryptionModel.user= CustomSharedPrefs.getLoggedInUserId(CheckOutActivity.this);
            encryptionModel.amount=String.valueOf(totalAmount);


            String json=UtilityClass.objectToStringsEncryptionModel(encryptionModel);

            String encryptedJson= MCrypt.encrypt(Constants.EncryptionKey.ENCRYPTION_KEY,Constants.EncryptionKey.ENCRYPTION_IV, json);
            String decryptJson= MCrypt.decrypt(Constants.EncryptionKey.ENCRYPTION_KEY,Constants.EncryptionKey.ENCRYPTION_IV, encryptedJson);


            Log.d("encryptedJson",encryptedJson);
            Log.d("decryptJson",decryptJson);

            presenter.doPayment(CheckOutActivity.this, encryptedJson);
        });
    }

    @Override
    public void onPaymentNonceError(String errorMessage) {
        mLoader.stopLoader();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void allPaymentSuccess(PaymentResponse response) {
        mLoader.stopLoader();
        Log.d("response",response.toString());
        if (response != null && response.statusCode == HttpURLConnection.HTTP_OK) {
            SharedPref.getSharedPref(this).write(Constants.Preferences.TRANSACTION_ID,response.model.transactionId);
            SharedPref.getSharedPref(this).write(Constants.Preferences.CURRENT_DATE,
                    UtilityClass.getTodayDate());
            AsyncTask.execute(() -> {
                setProductListForInvoice();
                DatabaseUtil.on().deleteAll();
                runOnUiThread(() -> {
                    mLoader.stopLoader();
                    startActivity(new Intent(CheckOutActivity.this, InvoiceActivity.class));
                    if (ProductDetailsActivity.productDetailsActivity != null)
                        ProductDetailsActivity.productDetailsActivity.finish();
                    if (CartActivity.cartActivity != null)
                        CartActivity.cartActivity.finish();
                    if (ShippingAddressActivity.shippingAddressActivity != null)
                        ShippingAddressActivity.shippingAddressActivity.finish();
                    finish();
                });
            });
        }

    }

    private void setProductListForInvoice(){
        InvoiceModel invoiceModel=new InvoiceModel();
        invoiceModel.inventoryListForInvoice=inventoryList;
        String productList= UtilityClass.inventoryListToJson(invoiceModel);
        SharedPref.getSharedPref(this).write(Constants.Preferences.INVOICE_PRODUCT_LIST,productList);
    }

    private String giveProductList(){
        InvoiceModel invoiceModel=new InvoiceModel();
        invoiceModel.inventoryListForInvoice=inventoryList;
        return UtilityClass.inventoryListToJson(invoiceModel);
    }

    @Override
    public void allPaymentError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mLoader.stopLoader();
    }

    @Override
    public void onSettingCredentialSuccess(SettingsResponse settingsResponse) {
        CustomSharedPrefs.setCurrency(this, settingsResponse.settingsModel.currencyFont);
        SharedPref.getSharedPref(this).write(Constants.Preferences.TAX, settingsResponse.settingsModel.tax);
        SharedPref.getSharedPref(this).write(Constants.Preferences.MERCHANT_ID, settingsResponse.settingsModel.paymentModel.merchantId);
        SharedPref.getSharedPref(this).write(Constants.Preferences.ENVIRONMENT, settingsResponse.settingsModel.paymentModel.environment);
        SharedPref.getSharedPref(this).write(Constants.Preferences.PUBLIC_KEY, settingsResponse.settingsModel.paymentModel.publicKey);
        SharedPref.getSharedPref(this).write(Constants.Preferences.PRIVATE_KEY, settingsResponse.settingsModel.paymentModel.privateKey);

        mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
        mBinding.scrollview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSettingCredentialError(String error) {
        Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
    }

}
