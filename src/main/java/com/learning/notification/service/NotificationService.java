package com.learning.notification.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.learning.notification.config.ApnsProperties;
import com.learning.notification.exception.NotificationConfigurationException;
import com.learning.notification.exception.NotificationDeliveryException;
import com.learning.notification.web.dto.NotificationSendResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Sends Apple Push Notifications (APNs) using the Pushy client, token-authenticated via a .p8 signing key. */
@Slf4j
@Service
public class NotificationService {

  private final Optional<ApnsClient> apnsClient;
  private final ApnsProperties properties;

  public NotificationService(Optional<ApnsClient> apnsClient, ApnsProperties properties) {
    this.apnsClient = apnsClient;
    this.properties = properties;
  }

  /** Sends. */
  public NotificationSendResponse send(String deviceToken, String title, String body, Integer badge) {
    ApnsClient client =
        apnsClient.orElseThrow(
            () ->
                new NotificationConfigurationException(
                    "APNs is not configured — set apns.enabled=true and provide signing-key-path, team-id, key-id and topic"));

    String token = TokenUtil.sanitizeTokenString(deviceToken);
    String payload = buildPayload(title, body, badge);
    SimpleApnsPushNotification notification = new SimpleApnsPushNotification(token, properties.topic(), payload);

    PushNotificationResponse<SimpleApnsPushNotification> response = sendAndWait(client, notification);
    log.info(
        "NOTIFICATION | sent | deviceToken={} | accepted={} | apnsId={}",
        token,
        response.isAccepted(),
        response.getApnsId());

    return new NotificationSendResponse()
        .accepted(response.isAccepted())
        .apnsId(response.getApnsId().toString())
        .rejectionReason(response.getRejectionReason().orElse(null));
  }

  private String buildPayload(String title, String body, Integer badge) {
    ApnsPayloadBuilder payloadBuilder =
        new SimpleApnsPayloadBuilder().setAlertTitle(title).setAlertBody(body).setSound("default");
    if (badge != null) {
      payloadBuilder.setBadgeNumber(badge);
    }
    return payloadBuilder.build();
  }

  private PushNotificationResponse<SimpleApnsPushNotification> sendAndWait(
      ApnsClient client, SimpleApnsPushNotification notification) {
    try {
      return client.sendNotification(notification).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new NotificationDeliveryException("Interrupted while sending the push notification", e);
    } catch (ExecutionException e) {
      throw new NotificationDeliveryException(
          "Failed to send the push notification: " + e.getCause().getMessage(), e.getCause());
    }
  }
}
