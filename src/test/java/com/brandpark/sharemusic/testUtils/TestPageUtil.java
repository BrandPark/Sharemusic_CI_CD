package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.page.PageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@RequiredArgsConstructor
@ActiveProfiles("local")
@Component
public class TestPageUtil {

    private final ObjectMapper objectMapper;

    public <T> PageResult<T> toPageResult(String json, Class<T> clazz) throws JsonProcessingException {
        PageResult<T> result = objectMapper.readValue(json, new TypeReference<PageResult<T>>() {

        });
        return result;
    }
}
