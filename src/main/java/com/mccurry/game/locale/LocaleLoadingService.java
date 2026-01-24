package com.mccurry.game.locale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mccurry.game.locale.exception.LocaleLoadingException;
import com.mccurry.game.locale.model.GameLocale;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mccurry.game.locale.model.GameLocale.ENGLISH;

public class LocaleLoadingService {
    private static final String LOCALE_FILENAME_TEMPLATE = "locale/locale_%s.json";
    private static final Map<GameLocale,String> FILE_SUFFIX_BY_GAME_LOCALE = new HashMap<>(){{
        put(ENGLISH, "english");
    }};

    private static final ObjectMapper LOCALE_FILE_MAPPER = new ObjectMapper();

    LocaleLoadingService(){}

    Map<String,String> loadLocale(GameLocale gameLocale){
        var localeFile = new File(getLocaleFileName(gameLocale));

        if(!localeFile.exists() || !localeFile.isFile()){
            LocaleRegistry.updateLocale(new HashMap<>());
        }
        return parseLocale(localeFile);
    }

    private Map<String,String> parseLocale(File localeFile){
        try {
            var mapReference = new TypeReference<Map<String,String>>(){};
            return LOCALE_FILE_MAPPER.readValue(localeFile, mapReference);
        } catch (IOException e) {
            throw new LocaleLoadingException("Could not parse locale file '%s'".formatted(localeFile.getPath()));
        }
    }

    private String getLocaleFileName(GameLocale gameLocale){
        var localeFileSuffix = FILE_SUFFIX_BY_GAME_LOCALE.get(gameLocale);
        return LOCALE_FILENAME_TEMPLATE.formatted(localeFileSuffix);
    }
}
