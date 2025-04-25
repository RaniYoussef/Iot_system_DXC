package com.example.demo;

import org.springframework.web.util.HtmlUtils;

public class InputSanitizer {

    public static String sanitize(String input) {
        if (input == null) return null;

        // Trim leading/trailing whitespace
        String sanitized = input.trim();

        sanitized = HtmlUtils.htmlEscape(sanitized);


//        // Remove HTML and script tags
//        sanitized = sanitized.replaceAll("<[^>]*>", "");
//
//        // Remove any script-like patterns
//        sanitized = sanitized.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
//
//        // Optional: disallow quotes or special characters
//        sanitized = sanitized.replaceAll("[\"']", "");

        return sanitized;
    }
}
