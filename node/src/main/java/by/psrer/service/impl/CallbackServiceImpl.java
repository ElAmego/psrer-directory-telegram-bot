package by.psrer.service.impl;

import by.psrer.service.CallbackService;
import by.psrer.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@Log4j
public final class CallbackServiceImpl implements CallbackService {
    private final ProducerService producerService;

    public CallbackServiceImpl(final ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void handleCallback(final CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var action = callbackQuery.getData();
    }
}
