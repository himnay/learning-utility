package com.learning.notification.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.learning.notification.exception.NotificationConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the {@link ApnsClient} bean from a token-based (.p8) signing key. Only created when
 * {@code apns.enabled=true} — otherwise {@link com.learning.notification.service.NotificationService} sees no
 * client and reports {@link NotificationConfigurationException} on send, instead of failing app startup.
 */
@Configuration
@EnableConfigurationProperties(ApnsProperties.class)
public class ApnsClientConfig {

  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(prefix = "apns", name = "enabled", havingValue = "true")
  public ApnsClient apnsClient(ApnsProperties properties) {
    try {
      ApnsSigningKey signingKey =
          ApnsSigningKey.loadFromPkcs8File(
              new File(properties.signingKeyPath()), properties.teamId(), properties.keyId());

      return new ApnsClientBuilder()
          .setApnsServer(
              properties.production() ? ApnsClientBuilder.PRODUCTION_APNS_HOST : ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
          .setSigningKey(signingKey)
          .build();
    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
      throw new NotificationConfigurationException("Failed to build the APNs client from apns.* properties", e);
    }
  }
}
