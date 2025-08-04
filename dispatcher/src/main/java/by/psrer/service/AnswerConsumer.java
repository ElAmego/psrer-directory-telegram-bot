package by.psrer.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface AnswerConsumer {
    void consumeAnswer(final SendMessage sendMessage);

    void consumeDeleteMessage(final DeleteMessage deleteMessage);
}
