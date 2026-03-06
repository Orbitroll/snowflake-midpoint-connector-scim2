package com.exclamationlabs.connid.base.scim2.util;

import org.apache.commons.lang3.StringUtils;

public class Scim2Utils {

  private Scim2Utils() {}

  /**
   * Sanitize URL values copied from UI/config by removing control characters and trimming spaces.
   */
  public static String sanitizeUrl(String value) {
    String cleaned = removeControlCharacters(value);
    return StringUtils.trimToNull(cleaned);
  }

  /**
   * Normalize endpoint path values so they are safe for request URI composition.
   */
  public static String normalizeEndpointPath(String endpoint, String defaultPath) {
    String normalized = sanitizeUrl(StringUtils.defaultIfBlank(endpoint, defaultPath));
    if (normalized == null) {
      normalized = defaultPath;
    }
    if (!normalized.startsWith("/")) {
      normalized = "/" + normalized;
    }
    return normalized;
  }

  private static String removeControlCharacters(String value) {
    if (value == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (!Character.isISOControl(ch)) {
        sb.append(ch);
      }
    }
    return sb.toString();
  }
}
