package by.psrer.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void produce(final String rabbitQueue, final Update update);
}
