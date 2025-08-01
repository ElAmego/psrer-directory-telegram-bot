package by.psrer.service.impl;

import by.psrer.service.ConsumerService;
import by.psrer.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.psrer.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
@Log4j
public final class ConsumerServiceImpl implements ConsumerService {
    private MainService mainService;

    public ConsumerServiceImpl(final MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(final Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }
}
