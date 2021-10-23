package com.brandpark.sharemusic.api.v1.exception.client;

public class TracksCountOutOfBoundsException extends RuntimeException{
    public TracksCountOutOfBoundsException(String message) {
        super(message);
    }
}
