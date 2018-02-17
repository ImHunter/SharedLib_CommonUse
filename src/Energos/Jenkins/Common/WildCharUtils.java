import java.util.regex.Pattern;

public final class WildCharUtils {
    private static final char ESCAPES[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '\\', '.', '<', '>' };
    private final String regexp;

    public static Pattern compile(String pattern) {
        return new Pattern(pattern, Pattern.CASE_INSENSITIVE);
    }

    private Pattern(String pattern) {
        regexp = wildcardToRegexp(pattern);
    }

    public boolean match(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(this.regexp);
    }

    private String wildcardToRegexp(String pattern) {
        String result = "^";

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean escaped = false;
            for (int j = 0; j < ESCAPES.length; j++) {
                if (ch == ESCAPES[j]) {
                    result += "\\" + ch;
                    escaped = true;
                    break;
                }
            }

            if (!escaped) {
                if (ch == '*') {
                    result += ".*";
                } else if (ch == '?') {
                    result += ".";
                } else {
                    result += ch;
                }
            }
        }
        result += "$";
        return result;
    }
}