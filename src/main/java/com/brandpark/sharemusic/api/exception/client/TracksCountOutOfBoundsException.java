package com.brandpark.sharemusic.api.exception.client;

public class TracksCountOutOfBoundsException extends RuntimeException{
    public TracksCountOutOfBoundsException(String message) {
        super(message);
    }
}
