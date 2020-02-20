package com.w3engineers.ecommerce.bootic.data.helper.database;

import androidx.room.Database;
import android.content.Context;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;

@Database(entities = {CustomProductInventory.class},
        version = 1, exportSchema = false)
public abstract class QrCobaDatabase extends AppDatabase {

    private static volatile QrCobaDatabase sInstance;

    // Get a database instance
    public static synchronized QrCobaDatabase on() {
        return sInstance;
    }

    public static synchronized void init(Context context) {

        if (sInstance == null) {
            synchronized (QrCobaDatabase.class) {
                sInstance = createDb(context, context.getString(R.string.app_name), QrCobaDatabase.class);
            }
        }
    }

    public abstract BooticCustomDao codeDao();
}
