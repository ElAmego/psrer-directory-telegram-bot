package by.psrer.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackProducer {
    void produce(final String rabbitQueue, final CallbackQuery callbackQuery);
}
