package by.psrer.service;

import by.psrer.dto.ImageDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface ProducerService {
    void produceAnswer(final SendMessage sendMessage);
    void produceDeleteMessage(final DeleteMessage deleteMessage);
    void produceImage(final ImageDTO imageDTO);
}