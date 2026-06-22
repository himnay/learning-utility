package com.learning.qr.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.common.web.GlobalExceptionHandler;
import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.service.QrCodeService;
import com.learning.qr.web.dto.QrDecodeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {QrCodeController.class, GlobalExceptionHandler.class})
class QrCodeControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private QrCodeService qrCodeService;

  @Test
  @DisplayName("POST /qr/scan returns 200 with the decoded text and format")
  void scanReturnsDecodedResult() throws Exception {
    when(qrCodeService.decode(any())).thenReturn(new QrDecodeResponse().text("hello").format("QR_CODE"));

    MockMultipartFile file = new MockMultipartFile("file", "qr.png", "image/png", "fake-bytes".getBytes());

    mockMvc
        .perform(multipart("/qr/scan").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value("hello"))
        .andExpect(jsonPath("$.format").value("QR_CODE"));
  }

  @Test
  @DisplayName("POST /qr/scan with an empty file returns 400 without calling the service")
  void scanWithEmptyFileReturns400() throws Exception {
    MockMultipartFile emptyFile = new MockMultipartFile("file", "qr.png", "image/png", new byte[0]);

    mockMvc.perform(multipart("/qr/scan").file(emptyFile)).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("A QrDecodeException from the service surfaces as 400")
  void scanServiceExceptionReturns400() throws Exception {
    when(qrCodeService.decode(any())).thenThrow(new QrDecodeException("No QR code could be found"));

    MockMultipartFile file = new MockMultipartFile("file", "qr.png", "image/png", "fake-bytes".getBytes());

    mockMvc
        .perform(multipart("/qr/scan").file(file))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("No QR code could be found"));
  }
}
