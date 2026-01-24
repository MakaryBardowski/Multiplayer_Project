package com.mccurry.game.locale;

import java.util.HashMap;
import java.util.Map;

public class LocaleRegistry {
    private static final Map<String,String> LOCALE_ENTRIES = new HashMap<>();

    /**
     * @param localeKey - must match an entry in the locale file. Otherwise, returns a placeholder
     * @return value from entry of currently loaded locale
     */
    static String getLocalizedString(String localeKey){
       return LOCALE_ENTRIES.get(localeKey);
    }

    static void updateLocale(Map<String,String> newLocale){
        LOCALE_ENTRIES.clear();
        LOCALE_ENTRIES.putAll(newLocale);
    }
}
