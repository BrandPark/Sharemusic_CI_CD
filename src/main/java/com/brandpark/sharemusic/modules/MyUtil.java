package com.brandpark.sharemusic.modules;

public abstract class MyUtil {

    private MyUtil() { /* This is not support create Instance*/ }

    public static String toEscape(String text) {
        if (text != null) {
            text = text.replaceAll("<br>", "\n");
        }
        return text;
    }

    public static String toBrTag(String text) {
        if (text != null) {
            text = text.replaceAll("\n", "<br>");
        }
        return text;
    }
}
