package com.w3engineers.ecommerce.bootic.ui.addcart;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.models.CartModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;
import com.w3engineers.ecommerce.bootic.ui.shippingaddress.ShippingAddressActivity;
import com.w3engineers.ecommerce.bootic.ui.userLogin.LoginActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends CustomMenuBaseActivity<CartMvpView, CartPresenter> implements
        CartMvpView, ItemClickListener<CustomProductInventory> {
    private Toolbar toolbar;
    private List<CustomProductInventory> inventoryList=new ArrayList<>();
    private CartAdapter mAdapter;
    private RecyclerView recyclerView;
    private float totalPrice = 0.0f;
    private TextView textViewPrice;
    private Button buttonCheckOut;
    public static int CLICK_CART_DELETE = 4646;
    public static CartActivity cartActivity;
    private LinearLayout mLinearLayoutNoDataFound;
    private RelativeLayout mRelativeLayout;
    private Loader mLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cart;
    }

    @Override
    protected void startUI() {
        mLoader = new Loader(this);
        mAdapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView = findViewById(R.id.recycler_view_cart);
        textViewPrice = findViewById(R.id.tv_total_price);
        buttonCheckOut = findViewById(R.id.btn_cart_checkout);
        mLinearLayoutNoDataFound = findViewById(R.id.layout_no_data);
        mRelativeLayout = findViewById(R.id.relative_main_container);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        cartActivity = this;

        initToolbar();

        AsyncTask.execute(() -> {

                inventoryList = DatabaseUtil.on().getAllCodes();
                if (inventoryList != null && inventoryList.size() > 0) {
                    runOnUiThread(() -> mLinearLayoutNoDataFound.setVisibility(View.GONE));
                    mAdapter.addItem(inventoryList);
                } else {
                    runOnUiThread(() -> mLinearLayoutNoDataFound.setVisibility(View.VISIBLE));
                }
                calculatePrice(inventoryList); //todo

        });

        buttonCheckOut.setOnClickListener(v -> {
            if (inventoryList.size() > 0) {
                checkRegistration();
            } else {
                Toast.makeText(CartActivity.this, getResources().getString(R.string.add_product), Toast.LENGTH_LONG).show();
            }
        });

    }



    /**
     * action upon registration state
     */
    private void checkRegistration() {
        if (!CustomSharedPrefs.getUserStatus(this)) {
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPref.getSharedPref(this).write(Constants.IntentKey.TOTAL_AMOUNT, totalPrice);
            if (totalPrice > 0) {
                Intent intent = new Intent(CartActivity.this, ShippingAddressActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, getResources().getString(R.string.add_product), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * to calculate total price based on all product in database
     *
     * @param inventoryList : list of CustomProductInventory
     */
    private void calculatePrice(List<CustomProductInventory> inventoryList) {
        if (DatabaseUtil.on().getItemCount() > 0) {
            for (CustomProductInventory productInventory : inventoryList) {
                totalPrice = totalPrice + (productInventory.price * productInventory.currentQuantity);
            }
            runOnUiThread(() -> {
                textViewPrice.setText(UtilityClass.getCurrencySymbolAndAmount(this,totalPrice));
            });
        }
    }

    /**
     * init toolbar
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.title_cart));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {
        if (cartActivity != null)
            cartActivity = null;
    }

    @Override
    public void onBackPressed() {
        ProductDetailsActivity.isFromReview = false;
        super.onBackPressed();
    }

    @Override
    protected CartPresenter initPresenter() {
        return new CartPresenter();
    }

    @Override
    public void onItemClick(View view, CustomProductInventory item, int i) {
        totalPrice = 0.0f;
        calculatePrice(mAdapter.getItems());
        if (i == CLICK_CART_DELETE) {
            Log.d("Delete",""+i);
            updateMenu();
            AsyncTask.execute(() -> {
                if (DatabaseUtil.on().getItemCount() <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLinearLayoutNoDataFound.setVisibility(View.VISIBLE);
                            textViewPrice.setText("");
                        }
                    });
                }
            });
        }

    }

    @Override
    public void onCartDataSuccess(CartModelResponse response) {
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                List<CartModel> cartModelList = response.mCartModelList;
                if (cartModelList !=null){
                    setCartModelToInventoryModel(cartModelList);
                }


            }
            mLoader.stopLoader();
        }
    }

    /**
     * Clear database and insert database
     * Update menu and add data to adapter
     */
    private void clearDatabaseAndInsertDataBase(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseUtil.on().deleteAll();
                Log.d("Deleted","Done");
                for (CustomProductInventory customProductInventory:inventoryList){
                    DatabaseUtil.on().insertItem(customProductInventory);
                }
            }
        });


        if (inventoryList != null && inventoryList.size() > 0) {
            runOnUiThread(() -> mLinearLayoutNoDataFound.setVisibility(View.GONE));
            mAdapter.addItem(inventoryList);
        } else {
            runOnUiThread(() -> mLinearLayoutNoDataFound.setVisibility(View.VISIBLE));
        }
        calculatePrice(inventoryList);
        updateMenu();
    }

    /**
     * set Cart Model To InventoryModel
     * @param cartModelList cartModelList
     */
    private void setCartModelToInventoryModel(List<CartModel> cartModelList ){
        for (CartModel cartModel:cartModelList){
            CustomProductInventory customProductInventory=new CustomProductInventory();
            customProductInventory.inventory_id=cartModel.mInventoryModel.id;
            customProductInventory.newPrice=cartModel.prevPrice;
            customProductInventory.price=cartModel.currentPrice;
            customProductInventory.imageUri=cartModel.imageUri;
            customProductInventory.available_qty=cartModel.mInventoryModel.quantity;
            customProductInventory.productName=cartModel.productName;
            customProductInventory.product_id= cartModel.id;
            customProductInventory.currentQuantity=1;
            inventoryList.add(customProductInventory);
         }

        ///need to insert database
        //clear database
        clearDatabaseAndInsertDataBase();
    }

    @Override
    public void onCartDataError(String message) {
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
