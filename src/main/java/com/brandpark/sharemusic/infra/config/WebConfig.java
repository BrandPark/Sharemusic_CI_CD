package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.modules.notification.service.NotificationInterceptor;
import com.brandpark.sharemusic.modules.search.SearchInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;
    private final SearchInterceptor searchInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> staticResourcesPathList = Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(Collectors.toList());
        staticResourcesPathList.add("/node_modules/**");
        staticResourcesPathList.add("/custom/**");

        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticResourcesPathList);

        registry.addInterceptor(searchInterceptor)
                .excludePathPatterns(staticResourcesPathList);
    }
}
