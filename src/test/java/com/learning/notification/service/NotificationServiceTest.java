package com.learning.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.learning.notification.config.ApnsProperties;
import com.learning.notification.exception.NotificationConfigurationException;
import com.learning.notification.exception.NotificationDeliveryException;
import com.learning.notification.web.dto.NotificationSendResponse;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

  private static final String DEVICE_TOKEN = "740f4707bebcf74f9b7c25d48e3358945f6aa01da5ddb387462c7eaf61bb78a";
  private static final ApnsProperties PROPERTIES =
      new ApnsProperties(true, "/dev/null", "team", "key", "com.learning.app", false);

  @Test
  @DisplayName("send() returns the APNs response when the notification is accepted")
  void sendReturnsAcceptedResponse() {
    UUID apnsId = UUID.randomUUID();
    ApnsClient client = clientThatResponds(true, apnsId, Optional.empty());
    NotificationService service = new NotificationService(Optional.of(client), PROPERTIES);

    NotificationSendResponse result = service.send(DEVICE_TOKEN, "Hello", "World", 3);

    assertThat(result.getAccepted()).isTrue();
    assertThat(result.getApnsId()).isEqualTo(apnsId.toString());
    assertThat(result.getRejectionReason().get()).isNull();
  }

  @Test
  @DisplayName("send() carries through the rejection reason when APNs rejects the notification")
  void sendReturnsRejectionReason() {
    UUID apnsId = UUID.randomUUID();
    ApnsClient client = clientThatResponds(false, apnsId, Optional.of("BadDeviceToken"));
    NotificationService service = new NotificationService(Optional.of(client), PROPERTIES);

    NotificationSendResponse result = service.send(DEVICE_TOKEN, "Hello", "World", null);

    assertThat(result.getAccepted()).isFalse();
    assertThat(result.getRejectionReason().get()).isEqualTo("BadDeviceToken");
  }

  @Test
  @DisplayName("send() throws NotificationConfigurationException when APNs isn't configured")
  void sendThrowsWhenNotConfigured() {
    NotificationService service = new NotificationService(Optional.empty(), PROPERTIES);

    assertThatThrownBy(() -> service.send(DEVICE_TOKEN, "Hello", "World", null))
        .isInstanceOf(NotificationConfigurationException.class);
  }

  @Test
  @DisplayName("send() wraps a failed APNs send in NotificationDeliveryException")
  void sendWrapsExecutionFailure() {
    ApnsClient client = mock(ApnsClient.class);
    when(client.sendNotification(any()))
        .thenAnswer(
            invocation -> {
              SimpleApnsPushNotification notification = invocation.getArgument(0);
              PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                  future = new PushNotificationFuture<>(notification);
              future.completeExceptionally(new RuntimeException("boom"));
              return future;
            });
    NotificationService service = new NotificationService(Optional.of(client), PROPERTIES);

    assertThatThrownBy(() -> service.send(DEVICE_TOKEN, "Hello", "World", null))
        .isInstanceOf(NotificationDeliveryException.class)
        .hasMessageContaining("boom");
  }

  @SuppressWarnings("unchecked")
  private static ApnsClient clientThatResponds(boolean accepted, UUID apnsId, Optional<String> rejectionReason) {
    ApnsClient client = mock(ApnsClient.class);
    when(client.sendNotification(any()))
        .thenAnswer(
            invocation -> {
              SimpleApnsPushNotification notification = invocation.getArgument(0);
              PushNotificationResponse<SimpleApnsPushNotification> response = mock(PushNotificationResponse.class);
              when(response.isAccepted()).thenReturn(accepted);
              when(response.getApnsId()).thenReturn(apnsId);
              when(response.getRejectionReason()).thenReturn(rejectionReason);
              PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                  future = new PushNotificationFuture<>(notification);
              future.complete(response);
              return future;
            });
    return client;
  }
}
