package com.mccurry.game.locale;

public class LocalizedString {
    public static String get(String localeKey){
        var localizedString = LocaleRegistry.getLocalizedString(localeKey);
        return localizedString != null ? localizedString : "???%s???".formatted(localeKey);
    }
}
