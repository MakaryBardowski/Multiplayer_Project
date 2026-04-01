package com.mccurry.game.locale;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.locale.event.MainThreadLocaleChangedEvent;
import com.mccurry.game.locale.model.GameLocale;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.event.MainThreadLocaleReloadedEvent;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class LocaleService implements EventSubscriber, SyncEventHandler<MainThreadLocaleChangedEvent> {
    private final LocaleLoadingService localeLoadingService = new LocaleLoadingService();
    private final EventBus orchestrationBus;

    private LocaleService(EventBus orchestrationBus){
        this.orchestrationBus = orchestrationBus;
    }

    public static LocaleService withLocaleFromSettings(EventBus orchestrationBus, UserSettings settings){
        var service = new LocaleService(orchestrationBus);
        service.changeLocale(settings.getLocale());
        service.subscribe(orchestrationBus);
        return service;
    }

    private void changeLocale(GameLocale newLocale){
        var localeMap = localeLoadingService.loadLocale(newLocale);
        LocaleRegistry.updateLocale(localeMap);
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(MainThreadLocaleChangedEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(MainThreadLocaleChangedEvent.class, this);
    }

    @Override
    public void handle(@NonNull MainThreadLocaleChangedEvent event, @Nullable EventLifecycleManager<MainThreadLocaleChangedEvent> lifecycleManager) {
        changeLocale(event.getNewLocale());
        orchestrationBus.publish(new MainThreadLocaleReloadedEvent(), null);
    }
}
