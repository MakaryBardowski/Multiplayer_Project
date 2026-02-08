package com.mccurry.game.util.string;

public class JmeRgbaTagsUtil {

    public static final String BRIGHT_RED = "\\#FF0000#";
    public static final String BRIGHT_GREEN = "\\#00FF00#";
    public static final String WHITE = "\\#000000#";

    public static String greenToWhite(String greenString){
        return BRIGHT_GREEN+ greenString+WHITE;
    }
    public static String redToWhite(String redString){
        return BRIGHT_RED+ redString+WHITE;
    }
}
