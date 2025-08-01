package by.psrer.service.impl;

import by.psrer.service.MainService;
import by.psrer.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j
public final class MainServiceImpl implements MainService {
    private final ProducerService producerService;

    public MainServiceImpl(final ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(final Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();
        sendAnswer("Сообщение получено!", chatId);
    }

    private void sendAnswer(final String output, final String chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }
}
