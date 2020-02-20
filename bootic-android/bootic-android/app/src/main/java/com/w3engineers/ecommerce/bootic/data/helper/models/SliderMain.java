package com.w3engineers.ecommerce.bootic.data.helper.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderMain implements Parcelable {
    @SerializedName("tag")
    @Expose
    private String id;

    @SerializedName("detail")
    @Expose
    private String heading;

    @SerializedName("title")
    @Expose
    private String preHeading;

    @SerializedName("image_name")
    @Expose
    private String imageName;

    public SliderMain(String id, String heading, String preHeading,String imageName) {
        this.id = id;
        this.heading = heading;
        this.preHeading = preHeading;
        this.imageName = imageName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setPreHeading(String preHeading) {
        this.preHeading = preHeading;
    }

    public String getId() {
        return id;
    }

    public String getHeading() {
        return heading;
    }

    public String getPreHeading() {
        return preHeading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.heading);
        dest.writeString(this.preHeading);
    }

    public SliderMain() {
    }

    protected SliderMain(Parcel in) {
        this.id = in.readString();
        this.heading = in.readString();
        this.preHeading = in.readString();
    }

    public static final Creator<SliderMain> CREATOR = new Creator<SliderMain>() {
        @Override
        public SliderMain createFromParcel(Parcel source) {
            return new SliderMain(source);
        }

        @Override
        public SliderMain[] newArray(int size) {
            return new SliderMain[size];
        }
    };
}