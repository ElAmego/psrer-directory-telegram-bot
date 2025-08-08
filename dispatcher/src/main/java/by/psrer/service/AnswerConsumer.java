package by.psrer.service;

import by.psrer.dto.ImageDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface AnswerConsumer {
    void consumeAnswer(final SendMessage sendMessage);
    void consumeImage(final ImageDTO imageDTO);
    void consumeDeleteMessage(final DeleteMessage deleteMessage);
}
