package com.learning.notification.web;

import com.learning.notification.service.NotificationService;
import com.learning.notification.web.dto.NotificationSendRequest;
import com.learning.notification.web.dto.NotificationSendResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code /notifications} — send Apple Push Notifications (APNs) via the Pushy client. */
@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications")
@RequestMapping("/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  /** Sends. */
  @PostMapping("/send")
  @Operation(
      operationId = "sendApplePushNotification",
      summary = "Send an Apple Push Notification to a device",
      description =
          "Submits an alert notification (title + body, optional badge count) to APNs for the given device "
              + "token, using token-based (.p8) authentication.")
  public NotificationSendResponse send(@Valid @RequestBody NotificationSendRequest request) {
    return notificationService.send(
        request.getDeviceToken(), request.getTitle(), request.getBody(), request.getBadge());
  }
}
