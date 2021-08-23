package com.brandpark.sharemusic.exception;

public class NotFoundAlbumException extends RuntimeException {
    public NotFoundAlbumException() {
    }

    public NotFoundAlbumException(String message) {
        super(message);
    }

    public NotFoundAlbumException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundAlbumException(Throwable cause) {
        super(cause);
    }
}
