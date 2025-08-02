package by.psrer.service.impl;

import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import by.psrer.service.CommandService;
import by.psrer.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static by.psrer.entity.enums.Role.USER;
import static by.psrer.entity.enums.UserState.BASIC;

@Service
@Log4j
public final class CommandServiceImpl implements CommandService {
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public CommandServiceImpl(final ProducerService producerService, AppUserDAO appUserDAO) {
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void handleCommand(final Update update) {
        var appUser = findOrSaveAppUser(update);
        String output = "";
        output+= "Сообщение получено!";

        sendMessage(appUser, output);
    }

    private void sendMessage(final AppUser appUser, final String output) {
        final Long chatId = appUser.getTelegramUserId();
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(final Update update) {
        User telegramUser = update.getMessage().getFrom();

        AppUser persistanceAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistanceAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    .userState(BASIC)
                    .role(USER)
                    .build();

            return appUserDAO.save(transientAppUser);
        }
        return persistanceAppUser;
    }
}
