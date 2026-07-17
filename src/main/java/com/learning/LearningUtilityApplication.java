package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
class LearningUtilityApplication {
  public static void main(String[] args) {
    SpringApplication.run(LearningUtilityApplication.class, args);
  }
}
