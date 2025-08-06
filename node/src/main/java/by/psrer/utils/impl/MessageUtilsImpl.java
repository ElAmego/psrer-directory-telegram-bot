package by.psrer.utils.impl;

import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.enums.UserState;
import by.psrer.service.ProducerService;
import by.psrer.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static by.psrer.entity.enums.Role.USER;
import static by.psrer.entity.enums.UserState.BASIC;

@Service
@RequiredArgsConstructor

@SuppressWarnings("unused")
public final class MessageUtilsImpl implements MessageUtils {
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    @Override
    public void deleteUserMessage(final AppUser appUser, final Update update) {
        final DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(appUser.getTelegramUserId());
        deleteMessage.setMessageId(update.getMessage().getMessageId());
        producerService.produceDeleteMessage(deleteMessage);
    }

    @Override
    public void sendMessage(final Long chatId, final Answer answer) {
        final List<InlineKeyboardButton> inlineKeyboardButtonList = answer.getInlineKeyboardButtonList();
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(answer.getAnswerText())
                    .build();

        if (inlineKeyboardButtonList != null) {
            sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                    .keyboard(List.of(
                            inlineKeyboardButtonList))
                    .build());
        }

        producerService.produceAnswer(sendMessage);
    }

    @Override
    public AppUser findOrSaveAppUser(final Update update) {
        final User telegramUser = update.getMessage().getFrom();

        final AppUser persistanceAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistanceAppUser == null) {
            final AppUser transientAppUser = AppUser.builder()
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

    @Override
    public void changeUserState(final AppUser appUser, final UserState userState) {
        appUser.setUserState(userState);
        appUserDAO.save(appUser);
    }
}