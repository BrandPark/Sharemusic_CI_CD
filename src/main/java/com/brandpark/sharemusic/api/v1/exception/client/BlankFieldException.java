package com.brandpark.sharemusic.api.v1.exception.client;

public class BlankFieldException extends RuntimeException {
    public BlankFieldException(String message) {
        super(message);
    }
}
