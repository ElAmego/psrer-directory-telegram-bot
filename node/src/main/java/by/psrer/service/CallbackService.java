package by.psrer.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackService {
    void handleCallback(final CallbackQuery callbackQuery);
}