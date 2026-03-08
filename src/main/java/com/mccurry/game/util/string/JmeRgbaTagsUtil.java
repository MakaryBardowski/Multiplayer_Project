package com.mccurry.game.util.string;

import lombok.RequiredArgsConstructor;

public class JmeRgbaTagsUtil {

    @Deprecated
    public static final String BRIGHT_RED = "\\#FF0000#";
    @Deprecated
    public static final String BRIGHT_GREEN = "\\#00FF00#";
    @Deprecated
    public static final String WHITE = "\\#000000#";
    @Deprecated
    public static final String YELLOW = "\\#FFFF00#";

    public static String colorToColor(ColorTag textColor, String coloredText, ColorTag colorTagAfterText){
        return textColor.label + coloredText + colorTagAfterText.label;
    }

    @Deprecated
    public static String greenToWhite(String greenString) {
        return BRIGHT_GREEN + greenString + WHITE;
    }

    @Deprecated
    public static String redToWhite(String redString) {
        return BRIGHT_RED + redString + WHITE;
    }

    @Deprecated
    public static String yellowToWhite(String redString) {
        return YELLOW + redString + WHITE;
    }

    @RequiredArgsConstructor
    public enum ColorTag {
        BRIGHT_RED("\\#FF0000#"),
        BRIGHT_GREEN("\\#00FF00#"),
        WHITE("\\#000000#"),
        BLACK("\\#FFFFFF#"),
        YELLOW("\\#FFFF00#");
        private final String label;
    }
}
