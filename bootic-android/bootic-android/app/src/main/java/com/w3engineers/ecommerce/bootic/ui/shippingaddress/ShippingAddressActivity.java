package com.w3engineers.ecommerce.bootic.ui.shippingaddress;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryServerModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AvailableInventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserMultipleAddressResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.ActivityShippingAddressBinding;
import com.w3engineers.ecommerce.bootic.ui.addcart.CartActivity;
import com.w3engineers.ecommerce.bootic.ui.checkout.CheckOutActivity;
import com.w3engineers.ecommerce.bootic.ui.ordercomplete.OrderCompleteActivity;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;


import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressActivity extends BaseActivity<ShippingAddressMvpView, ShippingAddressPresenter> implements ShippingAddressMvpView, ItemClickListener<AddressModel> {

    private Toolbar toolbar;
    private ActivityShippingAddressBinding mBinding;
    private EditText etAddress1, etAddress2, etCity, etZip, etState, etCountry;
    private String address1 = "", address2 = "", city = "", zip = "", state = "", country = "";

    boolean isFieldEmpty;
    private Button btnCompleteAddress;
    private Loader mLoader;
    private Dialog dialog;
    private float totalPrice;
    StringBuilder inventoryIds = new StringBuilder(100);
    private List<CustomProductInventory> inventoryList;
    private List<InventoryModel> availableList;
    private boolean isAvailable;
    public String clientToken;
    public boolean isPaymentClicked, isRadioCash, isInventroyCame;
    public int paymentMethod, addressId;
    public int clickedRadio = 0;
    private ShippingAddressAdapter mAdapter;
    private UserAddressResponse addressResponses;
    public static ShippingAddressActivity shippingAddressActivity;
    private List<InventoryServerModel> serverModels = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_shipping_address;
    }

    @Override
    protected void startUI() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        initToolbar();
        mLoader = new Loader(this);
        checkNetConnection();
        getListFromDataBase();
        totalPrice = SharedPref.getSharedPref(this).readFloat(Constants.IntentKey.TOTAL_AMOUNT);
        mBinding.textTotalCostTitle.setText("" + UtilityClass.getCurrencySymbolAndAmount(this,totalPrice));
        setClickListener(mBinding.layoutAddress.radioCurrentAddress2, mBinding.buttonContinue,
                mBinding.layoutPayment.radioPaypal, mBinding.layoutPayment.radioCredit,
                mBinding.layoutPayment.textViewCredit, mBinding.layoutPayment.textViewPaypal
                , mBinding.layoutPayment.radioCash, mBinding.layoutPayment.textViewCash,
                mBinding.layoutAddress.textViewAddress2,mBinding.layoutIncludeNoNet.buttonRetry);
        isPaymentClicked = false;
        shippingAddressActivity = this;
        mAdapter = new ShippingAddressAdapter(new ArrayList<>(), this);
        mAdapter.setItemClickListener(this);
        mBinding.layoutAddress.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.layoutAddress.recyclerViewAddress.setAdapter(mAdapter);

        mLoader.show();
        presenter.getAllAddress(this, CustomSharedPrefs.getLoggedInUserId(this));
    }

    /**
     * check internet
     */
    private void checkNetConnection(){
        if (!NetworkHelper.hasNetworkAccess(this)){
            //no net
            mLoader.stopLoader();
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
            mBinding.scrollview.setVisibility(View.GONE);
        }else {
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
            mBinding.scrollview.setVisibility(View.VISIBLE);
        }
    }

    /**
     * getting list from database
     */
    private void getListFromDataBase() {
        AsyncTask.execute(() -> {
            inventoryList = DatabaseUtil.on().getAllCodes();
            runOnUiThread(() -> {
                if (inventoryList != null && inventoryList.size() > 0) {
                    for (CustomProductInventory inventory : inventoryList) {
                        inventoryIds.append(inventory.inventory_id + ",");
                    }
                    mLoader.show();
                    presenter.getAvailableInventory(ShippingAddressActivity.this, String.valueOf(inventoryIds));
                }
            });
        });
    }


    /**
     * init toolbar
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.title_shipping_add));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void stopUI() {
        if (shippingAddressActivity != null)
            shippingAddressActivity = null;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.text_view_address_2:
            case R.id.radio_current_address_2:
                mBinding.layoutAddress.radioCurrentAddress2.setChecked(false);
                openAddressPopUp();
                break;

            case R.id.btn_complete_address:
                validateAddress();
                if (!isFieldEmpty) {
                    mLoader.show();
                    if (address1.equals("")) {
                        address1 = "";
                    } else if (address2.equals("")) {
                        address2 = "";
                    }
                    presenter.updateAddress(this, address1, address2, city, zip, state, country);
                }
                break;

            case R.id.text_view_paypal:
            case R.id.radio_paypal:
                isPaymentClicked = true;
                paymentMethod = 1;
                isRadioCash = false;
                mBinding.layoutPayment.radioPaypal.setChecked(true);
                mBinding.layoutPayment.radioCredit.setChecked(false);
                mBinding.layoutPayment.radioCash.setChecked(false);
                break;

            case R.id.text_view_credit:
            case R.id.radio_credit:
                isPaymentClicked = true;
                paymentMethod = 2;
                isRadioCash = false;
                mBinding.layoutPayment.radioCredit.setChecked(true);
                mBinding.layoutPayment.radioPaypal.setChecked(false);
                mBinding.layoutPayment.radioCash.setChecked(false);
                break;


            case R.id.text_view_cash:
                mBinding.layoutPayment.radioCash.setChecked(true);
            case R.id.radio_cash:
                mBinding.layoutPayment.radioCredit.setChecked(false);
                mBinding.layoutPayment.radioPaypal.setChecked(false);
                isRadioCash = true;
                paymentMethod=3;
                //doCashPayment();
                break;
            case R.id.button_continue:
                if (isInventroyCame) {
                    if (isRadioCash) {
                        doCashPayment();
                    } else {
                        checkAvailability();
                        doPaymentDropIN();
                        isRadioCash = false;
                    }
                } else {
                    Toast.makeText(this, getString(R.string.check_availability), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_retry:
                getListFromDataBase();
                mLoader.show();
                presenter.getAllAddress(this, CustomSharedPrefs.getLoggedInUserId(this));
                break;
        }
    }

    /**
     * this method is for braintree payment
     */
    private void doPaymentDropIN() {
        if (isAvailable) {
            if (isPaymentClicked) {
                if (clickedRadio != 0) {
                    mLoader.show();
                   /* if (clickedRadio == 2) {
                        presenter.saveData(addressResponses, this);
                    }*/
                    presenter.getClientTokenFromServer(this);
                } else {
                    Toast.makeText(this, getString(R.string.add_addresss), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.choose_pay_method), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this api is used to do cash on delivery
     */
    private void doCashPayment() {
        if (clickedRadio != 0) {
            checkAvailability();
            if (isAvailable) {
                hitServerForCashPayment();
            } else {
                Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.add_addresss), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Hit server for cash payment
     */
    private void hitServerForCashPayment(){
/*
        for (CustomProductInventory inventory : inventoryList) {
            InventoryServerModel model = new InventoryServerModel();
            model.inventory = "" + inventory.inventory_id;
            model.price = "" + inventory.price;
            model.product = "" + inventory.product_id;
            model.quantity = "" + inventory.currentQuantity;
            serverModels.add(model);
        }
        String inventoryLists = UtilityClass.objectToStrings(serverModels);
        mLoader.show();
        String  address = SharedPref.getSharedPref(ShippingAddressActivity.this).read(Constants.Preferences.USER_ADDRESS);
        AddressModel addressModel = UtilityClass.stringToAddress(address);
        addressId = addressModel.id;
*/



        Intent intent = new Intent(this, CheckOutActivity.class);
        intent.putExtra(Constants.IntentKey.PAYMENT_RESPONSE, clientToken);
        intent.putExtra(Constants.IntentKey.PAYMENT_METHOD, paymentMethod);
        startActivity(intent);

/*
        String tax = SharedPref.getSharedPref(this).read(Constants.Preferences.TAX);
        float taxPrice = (Float.parseFloat(tax)  * totalPrice) / 100;
        totalPrice = totalPrice + taxPrice;


        presenter.doPayment(this, this.getResources().getString(R.string.cash_on_delivery),
                "" + totalPrice, inventoryLists, "" +
                        addressId, CustomSharedPrefs.getLoggedInUserId(this));
                        */


    }

    /**
     * this api is used to check availability of the product user want to parches
     */
    private void checkAvailability() {
        // int count = 0;
        if (availableList != null && availableList.size() > 0) {
            if (inventoryList.size() > 0) {
                for (CustomProductInventory productInventory : inventoryList) {
                    int quantity = 0, proId = 0;
                    quantity = productInventory.currentQuantity;
                    proId = productInventory.inventory_id;

                    for (InventoryModel inventoryModel : availableList) {
                        if (inventoryModel.id == proId) {
                            if (inventoryModel.quantity >= quantity) {
                                // count++;
                                isAvailable = true;
                            } else {
                                isAvailable = false;
                            }
                        } else {
                            isAvailable = false;
                        }
                    }
                }
            }
        }

    }

    /**
     * checking validation of address field
     */
    private void validateAddress() {
        address1 = etAddress1.getText().toString().trim();
        address2 = etAddress2.getText().toString().trim();
        city = etCity.getText().toString().trim();
        zip = etZip.getText().toString().trim();
        state = etState.getText().toString().trim();
        country = etCountry.getText().toString().trim();

        if (address1.equals("")) {
            isFieldEmpty = true;
            etAddress1.setBackgroundResource(R.drawable.edittext_error);
        } else {
            isFieldEmpty = false;
            etAddress1.setBackgroundResource(R.drawable.edittext_round);
        }
        if (city.equals("")) {
            isFieldEmpty = true;
            etCity.setBackgroundResource(R.drawable.edittext_error);
        } else {
            isFieldEmpty = false;
            etCity.setBackgroundResource(R.drawable.edittext_round);
        }
        if (zip.equals("")) {
            isFieldEmpty = true;
            etZip.setBackgroundResource(R.drawable.edittext_error);
        } else {
            isFieldEmpty = false;
            etZip.setBackgroundResource(R.drawable.edittext_round);
        }
        if (state.equals("")) {
            isFieldEmpty = true;
            etState.setBackgroundResource(R.drawable.edittext_error);
        } else {
            isFieldEmpty = false;
            etState.setBackgroundResource(R.drawable.edittext_round);
        }
        if (country.equals("")) {
            isFieldEmpty = true;
            etCountry.setBackgroundResource(R.drawable.edittext_error);
        } else {
            isFieldEmpty = false;
            etCountry.setBackgroundResource(R.drawable.edittext_round);
        }
    }

    /**
     * open address pop up to update address
     */
    private void openAddressPopUp() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_alert_address, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        etAddress1 = dialog.findViewById(R.id.et_shipping_address_1);
        etAddress2 = dialog.findViewById(R.id.et_shipping_address_2);
        etCity = dialog.findViewById(R.id.et_shipping_city);
        etZip = dialog.findViewById(R.id.et_shipping_zip);
        etState = dialog.findViewById(R.id.et_shipping_state);
        etCountry = dialog.findViewById(R.id.et_shipping_country);

        btnCompleteAddress = dialog.findViewById(R.id.btn_complete_address_inner);
        btnCompleteAddress.setOnClickListener(v -> {
            validateAddress();
            if (!isFieldEmpty) {
                mLoader.show();
                presenter.updateAddress(ShippingAddressActivity.this, address1, address2, city, zip, state, country);
            }
        });
    }

    @Override
    protected ShippingAddressPresenter initPresenter() {
        return new ShippingAddressPresenter();
    }

    @Override
    public void onGetAvailableAddressSuccess(UserAddressResponse addressResponse) {
        if (dialog != null) dialog.dismiss();
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        if (addressResponse != null && addressResponse.statusCode == HttpURLConnection.HTTP_OK) {
            mAdapter.addItem(addressResponse.addressModel);
        }
    }

    @Override
    public void onGetAvailableAddressError(String errorMessage) {
        if (dialog != null) dialog.dismiss();
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        clickedRadio = 0;
    }

    @Override
    public void onGettingAllAddressSuccess(UserMultipleAddressResponse response) {
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        if (response != null && response.statusCode == HttpURLConnection.HTTP_OK) {
            if (!response.addressModel.isEmpty()) {
                mAdapter.addListItem(response.addressModel);
            }
        }

    }

    @Override
    public void onAvailableInventorySuccess(AvailableInventoryResponse response) {
        isInventroyCame = true;
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        if (response.statusCode == HttpURLConnection.HTTP_OK) {
            availableList = response.inventoryModelList;
        }

        //invisible the no internet layout
        mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
        mBinding.scrollview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAvailableInventoryError(String errorMessage) {
        isInventroyCame = true;
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBrainTreeClientTokenSuccess(String tokenResponse) {  //todo
        if (tokenResponse != null) {
            clientToken = tokenResponse;
            mLoader.stopLoader();
            Intent intent = new Intent(this, CheckOutActivity.class);
            intent.putExtra(Constants.IntentKey.PAYMENT_RESPONSE, clientToken);
            intent.putExtra(Constants.IntentKey.PAYMENT_METHOD, paymentMethod);
            startActivity(intent);
        }
        mLoader.stopLoader();
    }


    @Override
    public void onBrainTreeClientTokenError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mLoader.stopLoader();
    }

    @Override
    public void allPaymentSuccess(PaymentResponse response) {
        if (response != null && response.statusCode == HttpURLConnection.HTTP_OK) {
            AsyncTask.execute(() -> {
                DatabaseUtil.on().deleteAll();

                runOnUiThread(() -> {
                    mLoader.stopLoader();
                    startActivity(new Intent(ShippingAddressActivity.this, OrderCompleteActivity.class));
                    if (ProductDetailsActivity.productDetailsActivity != null)
                        ProductDetailsActivity.productDetailsActivity.finish();
                    if (CartActivity.cartActivity != null)
                        CartActivity.cartActivity.finish();
                    finish();
                });
            });

        }
        mLoader.stopLoader();
    }

    @Override
    public void allPaymentError(String errorMessage) {
        mLoader.stopLoader();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, AddressModel item, int i) {
        if (item != null) {
            clickedRadio = 1;
            presenter.saveAddressData(item, this);
        }

    }
}
