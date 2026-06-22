package com.learning.totp.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.common.web.GlobalExceptionHandler;
import com.learning.totp.exception.TotpAccountNotFoundException;
import com.learning.totp.service.TotpService;
import com.learning.totp.web.dto.TotpGenerateResponse;
import com.learning.totp.web.dto.TotpVerifyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {TotpController.class, GlobalExceptionHandler.class})
class TotpControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockitoBean private TotpService totpService;

  @Test
  @DisplayName("POST /totp/generate returns 200 with the seed/code/otpauth URI")
  void generateReturns200() throws Exception {
    when(totpService.generate(eq("alice@example.com")))
        .thenReturn(
            new TotpGenerateResponse()
                .accountName("alice@example.com")
                .secret("JBSWY3DPEHPK3PXP")
                .currentCode("123456")
                .otpAuthUri("otpauth://totp/learning-utility:alice@example.com?secret=JBSWY3DPEHPK3PXP"));

    mockMvc
        .perform(
            post("/totp/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountName\":\"alice@example.com\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountName").value("alice@example.com"))
        .andExpect(jsonPath("$.currentCode").value("123456"));
  }

  @Test
  @DisplayName("POST /totp/generate with a blank accountName returns 400")
  void generateWithBlankAccountNameReturns400() throws Exception {
    mockMvc
        .perform(
            post("/totp/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountName\":\"\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /totp/verify returns 200 with valid=true")
  void verifyReturns200() throws Exception {
    when(totpService.verify("alice@example.com", "123456")).thenReturn(new TotpVerifyResponse().valid(true));

    mockMvc
        .perform(
            post("/totp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountName\":\"alice@example.com\",\"code\":\"123456\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid").value(true));
  }

  @Test
  @DisplayName("POST /totp/verify with a non-6-digit code returns 400")
  void verifyWithInvalidCodeFormatReturns400() throws Exception {
    mockMvc
        .perform(
            post("/totp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountName\":\"alice@example.com\",\"code\":\"12\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /totp/verify for an unknown account returns 404")
  void verifyUnknownAccountReturns404() throws Exception {
    when(totpService.verify("ghost@example.com", "123456"))
        .thenThrow(new TotpAccountNotFoundException("ghost@example.com"));

    mockMvc
        .perform(
            post("/totp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountName\":\"ghost@example.com\",\"code\":\"123456\"}"))
        .andExpect(status().isNotFound());
  }
}
