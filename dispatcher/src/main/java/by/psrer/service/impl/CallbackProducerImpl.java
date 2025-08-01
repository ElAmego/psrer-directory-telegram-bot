package by.psrer.service.impl;

import by.psrer.service.CallbackProducer;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j
public class CallbackProducerImpl implements CallbackProducer {
    private final RabbitTemplate rabbitTemplate;

    public CallbackProducerImpl(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(final String rabbitQueue, final CallbackQuery callbackQuery) {
        log.debug(callbackQuery.getData());
        rabbitTemplate.convertAndSend(rabbitQueue, callbackQuery);
    }
}
