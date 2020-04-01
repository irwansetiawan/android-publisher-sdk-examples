package com.criteo.publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;
import com.criteo.publisher.Util.AdvertisingInfo;
import com.criteo.publisher.Util.DeviceUtil;
import com.criteo.publisher.csm.MetricSendingQueue;
import com.criteo.publisher.model.Config;
import com.criteo.publisher.network.PubSdkApi;
import java.util.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DependencyProviderTest {

  @Mock
  private DependencyProvider dependencyProvider;

  @Mock
  private PubSdkApi pubSdkApi;

  @Mock
  private AdvertisingInfo advertisingInfo;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    DependencyProvider.setInstance(null);
  }

  @Test
  public void setCriteoPublisherId_GivenNullCpId_ThrowException() throws Exception {
    dependencyProvider = DependencyProvider.getInstance();

    assertThatCode(() -> {
      dependencyProvider.setCriteoPublisherId(null);
    }).isNotNull();
  }

  @Test
  public void setApplication_GivenNullApplication_ThrowException() throws Exception {
    dependencyProvider = DependencyProvider.getInstance();

    assertThatCode(() -> {
      dependencyProvider.setApplication(null);
    }).isNotNull();
  }

  @Test
  public void provideContext_GivenApplication_ReturnApplicationContext() throws Exception {
    Application application = mock(Application.class);
    Context applicationContext = mock(Context.class);
    when(application.getApplicationContext()).thenReturn(applicationContext);

    dependencyProvider = DependencyProvider.getInstance();
    dependencyProvider.setApplication(application);
    Context context = dependencyProvider.provideContext();

    assertThat(context).isEqualTo(applicationContext);
  }

  @Test
  public void verifyDependencies() {
    // given
    when(dependencyProvider.providePubSdkApi()).thenReturn(pubSdkApi);
    when(dependencyProvider.provideAdvertisingInfo()).thenReturn(advertisingInfo);
    DependencyProvider.setInstance(dependencyProvider);

    // when, then
    assertTrue(pubSdkApi == DependencyProvider.getInstance().providePubSdkApi());
    assertTrue(advertisingInfo == DependencyProvider.getInstance().provideAdvertisingInfo());
  }

  @Test
  public void provideDeviceUtil_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideDeviceUtil);
  }

  @Test
  public void provideUserPrivacyUtil_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideUserPrivacyUtil);
  }

  @Test
  public void provideConfig_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideConfig);
  }

  @Test
  public void provideAndroidUtil_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideAndroidUtil);
  }

  @Test
  public void provideAdUnitMapper_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideAdUnitMapper);
  }

  @Test
  public void provideBidManager_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(provider -> {
      doReturn(mock(DeviceUtil.class)).when(provider).provideDeviceUtil();
      doReturn(mock(Config.class)).when(provider).provideConfig();
      doReturn(mock(MetricSendingQueue.class)).when(provider).provideMetricSendingQueue();

      return provider.provideBidManager();
    });
  }

  @Test
  public void providePubSdkApi_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::providePubSdkApi);
  }

  @Test
  public void provideDeviceInfo_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideDeviceInfo);
  }

  @Test
  public void provideAdvertisingInfo_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideAdvertisingInfo);
  }

  @Test
  public void provideClock_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideClock);
  }

  @Test
  public void provideLoggingUtil_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideLoggingUtil);
  }

  @Test
  public void provideSerialExecutor_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideSerialExecutor);
  }

  @Test
  public void provideThreadPoolExecutor_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideThreadPoolExecutor);
  }

  @Test
  public void provideBuildConfigWrapper_WhenProvidedTwice_ReturnsTheSame() throws Exception {
    provideBean_WhenProvidedTwice_ReturnsTheSame(DependencyProvider::provideBuildConfigWrapper);
  }

  private <T> void provideBean_WhenProvidedTwice_ReturnsTheSame(
      Function<DependencyProvider, T> providing) {
    DependencyProvider instance = spy(DependencyProvider.getInstance());
    instance.setApplication(mock(Application.class, RETURNS_DEEP_STUBS));
    instance.setCriteoPublisherId(CriteoUtil.TEST_CP_ID);

    T bean1 = providing.apply(instance);
    T bean2 = providing.apply(instance);

    assertTrue(bean1 == bean2);
  }
}
