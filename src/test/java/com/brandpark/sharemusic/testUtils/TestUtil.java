package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class TestUtil {
    public static ExceptionResult getExceptionResult(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);
    }
}
