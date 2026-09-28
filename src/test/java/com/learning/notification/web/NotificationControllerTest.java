package com.learning.notification.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.common.web.GlobalExceptionHandler;
import com.learning.notification.service.NotificationService;
import com.learning.notification.web.dto.NotificationSendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {NotificationController.class, GlobalExceptionHandler.class})
class NotificationControllerTest {

  /** The README's example token: 64 hex characters, as notification-api.yaml requires. */
  private static final String DEVICE_TOKEN = "740f4707bebcf74f9b7c25d48e3358945f6aa01da5ddb387462c7eaf61bb78a0";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private NotificationService notificationService;

  @Test
  @DisplayName("POST /notifications/send accepts a 64-character hex token and returns rejectionReason as a plain value")
  void sendReturnsTheApnsResult() throws Exception {
    when(notificationService.send(eq(DEVICE_TOKEN), eq("New message"), eq("Hi"), isNull()))
        .thenReturn(new NotificationSendResponse().accepted(false).apnsId("9f595474").rejectionReason("BadDeviceToken"));

    mockMvc
        .perform(post("/notifications/send").contentType(MediaType.APPLICATION_JSON).content(body(DEVICE_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accepted").value(false))
        .andExpect(jsonPath("$.rejectionReason").value("BadDeviceToken"));
  }

  @Test
  @DisplayName("POST /notifications/send with a token that isn't 64 hex characters returns 400")
  void sendRejectsAMalformedToken() throws Exception {
    mockMvc
        .perform(
            post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(DEVICE_TOKEN.substring(1))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  private static String body(String deviceToken) {
    return "{\"deviceToken\":\"" + deviceToken + "\",\"title\":\"New message\",\"body\":\"Hi\"}";
  }
}
