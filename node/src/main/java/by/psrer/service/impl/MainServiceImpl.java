package by.psrer.service.impl;

import by.psrer.service.MainService;
import by.psrer.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

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
    }

    @Override
    public void processCallback(final CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var action = callbackQuery.getData();
    }
}
