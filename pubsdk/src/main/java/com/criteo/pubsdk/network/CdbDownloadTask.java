package com.criteo.pubsdk.network;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.criteo.pubsdk.BuildConfig;
import com.criteo.pubsdk.Util.DeviceUtil;
import com.criteo.pubsdk.Util.HostAppUtil;
import com.criteo.pubsdk.cache.SdkCache;
import com.criteo.pubsdk.model.AdUnit;
import com.criteo.pubsdk.model.Cdb;
import com.criteo.pubsdk.model.Config;
import com.criteo.pubsdk.model.Publisher;
import com.criteo.pubsdk.model.Slot;
import com.criteo.pubsdk.model.User;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class CdbDownloadTask extends AsyncTask<Object, Void, Cdb> {
    private static final String TAG = CdbDownloadTask.class.getSimpleName();
    private final Context mContext;
    private final SdkCache cache;
    private boolean callConfig;

    public CdbDownloadTask(Context context, SdkCache cache, boolean callConfig) {
        this.mContext = context;
        this.cache = cache;
        this.callConfig = callConfig;
    }

    @Override
    protected Cdb doInBackground(Object... objects) {
        int profile = (Integer) objects[0];
        User user = (User) objects[1];
        Publisher publisher = (Publisher) objects[2];
        ArrayList<AdUnit> slots = (ArrayList<AdUnit>) objects[3];
        if (DeviceUtil.hasPlayServices(mContext)) {
            String addId = DeviceUtil.getAdvertisingId(mContext);
            if (!TextUtils.isEmpty(addId)) {
                user.setDeviceId(addId);
            }
        }
        if (callConfig) {
            Config config = PubSdkNetwork.loadConfig(mContext, publisher.getNetworkId(),
                    user.getSdkVer(), publisher.getBundleId());
            if (config != null && config.isKillSwitch()) {
                return null;
            }
        }
        Cdb cdb = new Cdb();
        cdb.setAdUnits(slots);
        cdb.setUser(user);
        cdb.setPublisher(publisher);
        cdb.setSdkVersion(String.valueOf(BuildConfig.VERSION_NAME));
        cdb.setProfileId(profile);
        JsonObject gdpr = HostAppUtil.gdpr(mContext.getApplicationContext());
        if (gdpr != null) {
            cdb.setGdprConsent(gdpr);
        }
        Cdb response = PubSdkNetwork.loadCdb(mContext, cdb);
        if (response != null && response.getSlots() != null && response.getSlots().size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Slot slot : response.getSlots()) {
                builder.append(slot.toString());
                builder.append("\n");
            }
            Log.d(TAG, builder.toString());
        }
        return response;
    }

    @Override
    protected void onPostExecute(Cdb cdb) {
        super.onPostExecute(cdb);
        if (cdb != null) {
            cache.setAdUnits(cdb.getSlots());
        }
    }
}