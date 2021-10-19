package com.brandpark.sharemusic.api;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
public class StaticResourceCachingController {

    @GetMapping(value = "/images/{path}", produces = {MediaType.IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @Cacheable("images")
    public byte[] image(@PathVariable String path) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("images/"+path);
        return FileCopyUtils.copyToByteArray(in);
    }
}
