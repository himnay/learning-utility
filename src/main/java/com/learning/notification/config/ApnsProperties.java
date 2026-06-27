package com.learning.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Token-based APNs credentials and target environment, bound from the {@code apns.*} properties. */
@ConfigurationProperties(prefix = "apns")
public record ApnsProperties(
    boolean enabled, String signingKeyPath, String teamId, String keyId, String topic, boolean production) {}
