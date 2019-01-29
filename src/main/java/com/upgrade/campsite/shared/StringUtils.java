package com.upgrade.campsite.shared;

public class StringUtils {
  public static boolean isEmpty(String source) {
    return source == null || source.trim().isEmpty();
  }

  public static boolean isNotEmpty(String source) {
    return !isEmpty(source);
  }
}
