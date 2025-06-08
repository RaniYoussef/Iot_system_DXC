package com.dxc.iotbackend.util;

import org.springframework.web.util.HtmlUtils;

public final class InputSanitizer {

    // Private constructor to prevent instantiation
    private InputSanitizer() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        String sanitized = input.trim()
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")          // Remove control characters
                .replaceAll("[\"'<>\\\\]", "")                     // Remove quotes, angle brackets, backslashes
                .replaceAll("[\\p{So}\\p{C}]", "");                // Remove emojis and symbols

        return HtmlUtils.htmlEscape(sanitized);                   // Escape HTML entities
    }
}
