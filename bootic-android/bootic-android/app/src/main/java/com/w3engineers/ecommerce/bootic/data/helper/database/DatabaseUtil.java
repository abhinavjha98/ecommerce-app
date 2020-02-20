package com.w3engineers.ecommerce.bootic.data.helper.database;

import android.content.Context;

import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;

import java.util.List;


public class DatabaseUtil {
    /**
     * Fields
     */
    private static DatabaseUtil sInstance;
    private BooticCustomDao mCodeDao;

    private DatabaseUtil() {
        setCodeDao(QrCobaDatabase.on().codeDao());
    }

    /**
     * This method builds an instance
     */
    public static void init(Context context) {
        QrCobaDatabase.init(context);

        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }
    }

    public static DatabaseUtil on() {
        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }

        return sInstance;
    }

    private BooticCustomDao getCodeDao() {
        return mCodeDao;
    }

    private void setCodeDao(BooticCustomDao codeDao) {
        mCodeDao = codeDao;
    }

    public long[] insertItem(CustomProductInventory productInventory) {
        return getCodeDao().insert(productInventory);
    }

    public List<CustomProductInventory> getAllCodes() {
        return getCodeDao().getAllFlowableCodes();
    }

    public int deleteEntity(CustomProductInventory code) {
        return getCodeDao().delete(code);
    }

    public int deleteCartProduct(int productId,int inventoryId){
        return getCodeDao().deleteProduct(productId,inventoryId);

    }
    public int getItemCount() {
        return getCodeDao().getRowCount();
    }

    public void deleteAll() {
        getCodeDao().nukeTable();
    }

    public void updateQuantity(int quantity, int id) {
        getCodeDao().update(quantity, id);
    }
}
