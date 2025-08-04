package by.psrer.service.impl;

import by.psrer.controller.UpdateController;
import by.psrer.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import static by.psrer.model.RabbitQueue.ANSWER_MESSAGE;
import static by.psrer.model.RabbitQueue.DELETE_MESSAGE;

@Service
public final class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(final UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeAnswer(final SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = DELETE_MESSAGE)
    public void consumeDeleteMessage(final DeleteMessage deleteMessage) {
        updateController.deleteTelegramMessage(deleteMessage);
    }
}