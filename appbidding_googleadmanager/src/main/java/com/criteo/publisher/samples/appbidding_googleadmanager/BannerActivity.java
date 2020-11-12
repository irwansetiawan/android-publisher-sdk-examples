/*
 *    Copyright 2020 Criteo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.criteo.publisher.samples.appbidding_googleadmanager;

import static com.criteo.publisher.samples.appbidding_googleadmanager.CriteoSampleApplication.CRITEO_BANNER_AD_UNIT;
import static com.criteo.publisher.samples.appbidding_googleadmanager.CriteoSampleApplication.GAM_BANNER_AD_UNIT_ID;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.criteo.publisher.Bid;
import com.criteo.publisher.BidResponseListener;
import com.criteo.publisher.Criteo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class BannerActivity extends AppCompatActivity {

  private PublisherAdView publisherAdView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_banner);

    publisherAdView = new PublisherAdView(this);
    publisherAdView.setAdSizes(AdSize.BANNER);
    publisherAdView.setAdUnitId(GAM_BANNER_AD_UNIT_ID);
    publisherAdView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        super.onAdLoaded();
        Log.d("CriteoSDK", "publisherAdView onAdLoaded");
      }

      @Override
      public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        Log.d("CriteoSDK", "publisherAdView onAdFailedToLoad");
      }

      @Override
      public void onAdOpened() {
        super.onAdOpened();
        Log.d("CriteoSDK", "publisherAdView onAdOpened");
      }
    });
    FrameLayout publisherAdViewContainer = findViewById(R.id.publisherAdViewContainer);
    publisherAdViewContainer.addView(publisherAdView);

    displayBanner();
  }

  @Override
  protected void onDestroy() {
    publisherAdView.destroy();
    super.onDestroy();
  }

  private void displayBanner() {
    Log.d("CriteoSDK", "Start Loading bids");
    Criteo.getInstance().loadBid(CriteoSampleApplication.CRITEO_BANNER_AD_UNIT, new BidResponseListener() {
      @Override
      public void onResponse(@Nullable Bid bid) {
        Log.d("CriteoSDK", "OnResponse");

        // Your existing Ad manager request builder
        PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();

        if (bid != null) {
          Log.d("CriteoSDK", String.format("Bid = %s", bid.getPrice()));
          Criteo.getInstance().enrichAdObjectWithBid(builder, bid);
        } else {
          Log.d("CriteoSDK", "No Bid");
        }

        // load Banner ad after Criteo bid is loaded
        PublisherAdRequest request = builder.build();
        Log.d("CriteoSDK", "Loading publisher ad view");
        publisherAdView.loadAd(request);
      }
    });

  }
}
