package com.DXC.iotbackend.util;

import org.springframework.web.util.HtmlUtils;

public class InputSanitizer {

    public static String sanitize(String input) {
        if (input == null) return null;

        // Trim whitespace
        String sanitized = input.trim();

        // Remove hidden/control characters (Unicode 0x00–0x1F and 0x7F)
        sanitized = sanitized.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // Escape HTML to prevent XSS
        sanitized = HtmlUtils.htmlEscape(sanitized);

        // Disallow quotes, angle brackets, backslashes
        sanitized = sanitized.replaceAll("[\"'<>\\\\]", "");

        // Remove emojis (by excluding surrogate pairs and certain Unicode blocks)
        sanitized = sanitized.replaceAll("[\\p{So}\\p{C}]", "");

        // Whitelist approach: allow only alphanumerics and safe punctuation
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9@._\\-\\s]", "");

        return sanitized;
    }
}
