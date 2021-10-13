package com.brandpark.sharemusic.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("app")
@Configuration
public class AppProperties {
    private String host;
}
