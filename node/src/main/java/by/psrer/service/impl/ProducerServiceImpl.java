package by.psrer.service.impl;

import by.psrer.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static by.psrer.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public final class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(final SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
}
