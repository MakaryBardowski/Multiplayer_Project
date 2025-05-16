package Utils;

public class Ansi {

    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    private static final String RESET = "\u001B[0m";

    public static String withAnsiColor(String colored, String color) {
        return color + colored + RESET;
    }
}
