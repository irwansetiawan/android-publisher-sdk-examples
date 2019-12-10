package com.criteo.publisher.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.criteo.publisher.BuildConfig;
import com.criteo.publisher.Util.DeviceUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {

    //TODO: Rename to advertisingId
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_ID_TYPE = "deviceIdType";
    private static final String DEVICE_MODEL = "deviceModel";
    private static final String DEVICE_OS = "deviceOs";
    private static final String SDK_VER = "sdkver";
    private static final String LIMIT = "lmt";
    private static final String CONNECTION = "connection";
    private static final String GAID = "gaid";
    private static final String ANDROID = "android";
    private static final String USP_IAB = "uspIab";
    private static final String USP_OPTOUT = "uspOptout";
    private static final int LMT_VAL = 0;

    private String deviceId;
    private String deviceIdType;
    private String deviceModel;
    private String deviceOs;
    private String sdkVer;
    private int limit;
    private String connection;

    /** US Privacy consent IAB format (for CCPA) */
    private String uspIab;

    /** US Privacy optout in binary format (for CCPA) */
    private String uspOptout;

    public User(@NonNull DeviceUtil deviceUtil) {
        deviceId = "";
        deviceIdType = GAID;
        deviceModel = deviceUtil.getDeviceModel();
        deviceOs = ANDROID;
        sdkVer = BuildConfig.VERSION_NAME;
        limit = LMT_VAL;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSdkVer() {
        return sdkVer;
    }

    public void setUspIab(@NonNull String uspIab) {
        this.uspIab = uspIab;
    }

    @Nullable
    public String getUspIab() {
        return uspIab;
    }

    public void setUspOptout(@NonNull String uspOptout) {
        this.uspOptout = uspOptout;
    }

    @Nullable
    public String getUspOptout() {
        return uspOptout;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(DEVICE_ID, deviceId);
        object.put(DEVICE_ID_TYPE, deviceIdType);
        object.put(DEVICE_MODEL, deviceModel);
        object.put(DEVICE_OS, deviceOs);
        object.put(SDK_VER, sdkVer);
        object.put(LIMIT, limit);
        object.put(CONNECTION, connection);

        if (this.uspIab != null && !this.uspIab.isEmpty()) {
            object.put(USP_IAB, uspIab);
        }

        if (this.uspOptout != null && !this.uspOptout.isEmpty()) {
            object.put(USP_OPTOUT, uspOptout);
        }

        return object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceId);
        dest.writeString(this.deviceIdType);
        dest.writeString(this.deviceModel);
        dest.writeString(this.deviceOs);
        dest.writeString(this.sdkVer);
        dest.writeInt(this.limit);
        dest.writeString(this.connection);
        dest.writeString(this.uspIab);
        dest.writeString(this.uspOptout);
    }

    protected User(Parcel in) {
        this.deviceId = in.readString();
        this.deviceIdType = in.readString();
        this.deviceModel = in.readString();
        this.deviceOs = in.readString();
        this.sdkVer = in.readString();
        this.limit = in.readInt();
        this.connection = in.readString();
        this.uspIab = in.readString();
        this.uspOptout = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
