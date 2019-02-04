package bookingapp.shared;

public class StringUtils {
  public static final String EMPTY = "";
  
  private StringUtils() {
    // empty constructor
  }
  
  public static boolean isEmpty(String source) {
    return source == null || source.trim().isEmpty();
  }

  public static boolean isNotEmpty(String source) {
    return !isEmpty(source);
  }
}
