package by.psrer.service.impl;

import by.psrer.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import static by.psrer.model.RabbitQueue.ANSWER_MESSAGE;
import static by.psrer.model.RabbitQueue.DELETE_MESSAGE;

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

    @Override
    public void produceDeleteMessage(final DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(DELETE_MESSAGE, deleteMessage);
    }
}
