package by.psrer.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessageUpdates(final Update update);
    void consumeCallback(final CallbackQuery callbackQuery);
}