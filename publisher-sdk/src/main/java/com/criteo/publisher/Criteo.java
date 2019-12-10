package com.criteo.publisher;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.criteo.publisher.Util.AdUnitType;
import com.criteo.publisher.Util.DeviceUtil;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.Config;
import com.criteo.publisher.model.DeviceInfo;
import com.criteo.publisher.model.Slot;
import com.criteo.publisher.model.TokenValue;
import java.util.List;

public abstract class Criteo {

  private static final String TAG = Criteo.class.getSimpleName();
  private static Criteo criteo;

  public static class Builder {
    private String criteoPublisherId;
    private List<AdUnit> adUnits;
    private Application application;
    private Boolean usPrivacyOptOut;

    public Builder(@NonNull Application application, @NonNull String criteoPublisherId) {
      this.application = application;
      this.criteoPublisherId = criteoPublisherId;
    }

    public Builder adUnits(@Nullable List<AdUnit> adUnits) {
      this.adUnits = adUnits;
      return this;
    }

    public Builder usPrivacyOptOut(boolean usPrivacyOptOut) {
      this.usPrivacyOptOut = usPrivacyOptOut;
      return this;
    }

    public Criteo init() throws CriteoInitException {
      return Criteo.init(application, criteoPublisherId, adUnits, usPrivacyOptOut);
    }
  }

  public static Criteo init(
      @NonNull Application application,
      @NonNull String criteoPublisherId,
      @Nullable List<AdUnit> adUnits
  ) throws CriteoInitException {

    Criteo.Builder builder = new Criteo.Builder(application, criteoPublisherId);
    builder.adUnits(adUnits);
    return builder.init();
  }

  private static Criteo init(
      Application application,
      String criteoPublisherId,
      List<AdUnit> adUnits,
      @Nullable Boolean usPrivacyOptOut
  ) throws CriteoInitException {

    synchronized (Criteo.class) {
      if (criteo == null) {
        try {
          DependencyProvider dependencyProvider = DependencyProvider.getInstance();
          DeviceUtil deviceUtil = dependencyProvider
              .provideDeviceUtil(application.getApplicationContext());

          if (deviceUtil.isVersionSupported()) {
            criteo = new CriteoInternal(application, adUnits, criteoPublisherId, usPrivacyOptOut,
                dependencyProvider);
          } else {
            criteo = new DummyCriteo();
          }
        } catch (IllegalArgumentException iae) {
          throw iae;
        } catch (Throwable tr) {
          Log.e(TAG, "Internal error initializing Criteo instance.", tr);
          throw new CriteoInitException("Internal error initializing Criteo instance.", tr);
        }
      }
      return criteo;
    }
  }

  public static Criteo getInstance() {
      if (criteo == null) {
          throw new IllegalStateException("You must call Criteo.Init() before calling Criteo.getInstance()");
      }

      return criteo;
  }

  @VisibleForTesting
  static void setInstance(@Nullable Criteo instance) {
      criteo = instance;
  }

  public abstract void setBidsForAdUnit(Object object, AdUnit adUnit);

  @Nullable
  abstract Slot getBidForAdUnit(@Nullable AdUnit adUnit);

  public abstract BidResponse getBidResponse(AdUnit adUnit);

  @Nullable
  abstract TokenValue getTokenValue(BidToken bidToken, AdUnitType adUnitType);

  abstract DeviceInfo getDeviceInfo();

  abstract Config getConfig();

  public abstract void setUsPrivacyOptOut(boolean usPrivacyOptOut);
}
