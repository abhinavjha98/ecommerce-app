package com.w3engineers.ecommerce.bootic.ui.userProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.UploadImageResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserMultipleAddressResponse;
import com.w3engineers.ecommerce.bootic.data.provider.reposervices.ApiService;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfilePresenter extends BasePresenter<ProfileMvpView> {

    private Bitmap thumbnail;
    private Bitmap bm;
    private boolean flag;
    private String path;

    public boolean isEmpty(String value) {
        return (value.length() < 1) ? true : false;
    }

    /**
     * updating address to server
     *
     * @param context context
     * @param address1 address1
     * @param address2 address2
     * @param city city
     * @param zip zip code
     * @param state state
     * @param country country
     */
    public void updateAddress(final Context context
            , final String address1
            , final String address2
            , final String city
            , final String zip
            , final String state
            , final String country
            , final String id) {


        if (NetworkHelper.hasNetworkAccess(context)) {

            RetrofitClient.getApiService().getUserAddressResponse(Constants.ServerUrl.API_TOKEN,
                    CustomSharedPrefs.getLoggedInUserId(context), address1, address2, city, zip, state, country,id).enqueue(new Callback<UserAddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserAddressResponse> call, @NonNull Response<UserAddressResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onSetAddressSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserAddressResponse> call, @NonNull Throwable t) {
                    getMvpView().onSetAddressError(t.getMessage(),true);
                }
            });
        } else {
            getMvpView().onSetAddressError(context.getResources().getString(R.string.check_net_connection),false);
        }
    }


    /**
     * this method open a alert to choose image
     *
     * @param activity activity
     */
    public void selectImage(final Activity activity) {
        final CharSequence[] items = {activity.getResources().getString(R.string.take_photo),
                activity.getResources().getString(R.string.choose_from_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.photo_title));
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(activity.getResources().getString(R.string.take_photo))) {
                cameraIntent(activity);

            } else if (items[item].equals(activity.getResources().getString(R.string.choose_from_gallery))) {
                galleryIntent(activity);

            }
        });


        builder.show();
    }


    /**
     * choose image from gallery
     *
     * @param activity
     */
    private void galleryIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getResources()
                .getString(R.string.select_file)), ProfileActivity.SELECT_FILE);
    }

    /**
     * choose image from camera
     *
     * @param activity
     */
    private void cameraIntent(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, ProfileActivity.REQUEST_CAMERA);
    }

    /**
     * getting path from taking image on camera
     *
     * @param data
     * @param activity
     */
    public void onCaptureImageResult(Intent data, Activity activity) {
        if (data.getExtras() != null) {
            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            flag = true;
            Uri savedImageURI = Uri.parse("" + thumbnail);
            path = savedImageURI.toString();
            if (thumbnail == null) {
                Toast.makeText(activity, activity.getString(R.string.image_is_corrupted), Toast.LENGTH_SHORT).show();
            } else {
                String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), thumbnail, "Title", null);
                Uri fileUri = Uri.parse(path);
                String filePath = getRealPathFromURIPath(fileUri, activity);
                getImageFromMultipart(filePath, activity);
            }
        }
    }


    /**
     * getting path from gallery image
     *
     * @param data data
     * @param activity activity
     */
    public void onSelectFromGalleryResult(Intent data, Activity activity) {
        bm = null;
        if (data != null) {
            try {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bm == null) {
                    Toast.makeText(activity, activity.getString(R.string.image_is_corrupted), Toast.LENGTH_SHORT).show();
                } else {
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    flag = true;
                    Uri savedImageURI = Uri.parse("" + bm);
                    Uri dataUri = data.getData();
                    path = savedImageURI.toString();
                    String filePath = getRealPathFromURIPath(dataUri, activity);
                    getImageFromMultipart(filePath, activity);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * getting image path from uri
     *
     * @param contentURI content URI
     * @param activity activity
     * @return : path path
     */
    public String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            cursor.moveToFirst();
            String image_id = cursor.getString(0);
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getApplicationContext().getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);

        }
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } else {

            return contentURI.getPath();
        }
    }

    /**
     * geting email from shared ref
     *
     * @param context context
     * @return email email
     */
    public String getEmail(Context context) {
        return CustomSharedPrefs.getLoggedInUser(context).userRegistrationInfo.email;
    }

    public void getImageFromMultipart(String filePath, Context context) {
        File file = new File(filePath);
        RequestBody mFile = RequestBody.create(MediaType.parse(context.getString(R.string.multipart)), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        RequestBody userID = RequestBody.create(MediaType.parse(context.getString(R.string.partbody)), CustomSharedPrefs.getLoggedInUserId(context));
        RequestBody email = RequestBody.create(MediaType.parse(context.getString(R.string.partbody)), getEmail(context));
        RequestBody apiToken = RequestBody.create(MediaType.parse(context.getString(R.string.partbody)), Constants.ServerUrl.API_TOKEN);
        uploadImage(context, apiToken, userID, fileToUpload, email);
    }

    /**
     * upload profile image to server
     *
     * @param context context
     * @param apiToken api Token
     * @param userID user ID
     * @param fileToUpload file To Upload
     * @param email email
     */
    private void uploadImage(Context context, RequestBody apiToken, RequestBody userID, MultipartBody.Part fileToUpload, RequestBody email) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            ApiService getRestInfoApi = RetrofitClient.getApiService();
            Call<UploadImageResponse> call = getRestInfoApi.uploadImage(apiToken,
                    userID,
                    email,
                    fileToUpload);
            call.enqueue(new Callback<UploadImageResponse>() {
                @Override
                public void onResponse(@NonNull Call<UploadImageResponse> call, @NonNull Response<UploadImageResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onGettingImageSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UploadImageResponse> call, @NonNull Throwable t) {
                    getMvpView().onGettingImageError(t.getMessage());
                }
            });
        } else {
            getMvpView().onGettingImageError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     * this api is used to get all address of user from server
     *
     * @param context context
     * @param userId user id
     */
    public void getAllAddress(Context context, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAllAddress(Constants.ServerUrl.API_TOKEN, userId).enqueue(new Callback<UserMultipleAddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserMultipleAddressResponse> call, @NonNull Response<UserMultipleAddressResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            getMvpView().onGettingAllAddressSuccess(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserMultipleAddressResponse> call, @NonNull Throwable t) {
                    getMvpView().onSetAddressError(t.getMessage(),true);
                }
            });
        } else {
            getMvpView().onSetAddressError(context.getResources().getString(R.string.check_net_connection),false);
        }
    }


    public void removeAddress(Context context, String id) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().removeAddress(Constants.ServerUrl.API_TOKEN, id).enqueue(new Callback<UserAddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserAddressResponse> call, @NonNull Response<UserAddressResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            getMvpView().onRemoveAddressSuccess(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserAddressResponse> call, @NonNull Throwable t) {
                    getMvpView().onSetAddressError(t.getMessage(),true);
                }
            });
        } else {
            getMvpView().onSetAddressError(context.getResources().getString(R.string.check_net_connection),false);
        }
    }


}
