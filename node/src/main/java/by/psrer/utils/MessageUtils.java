package by.psrer.utils;

import by.psrer.entity.AppUser;
import by.psrer.entity.enums.UserState;
import by.psrer.utils.impl.Answer;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageUtils {
    void deleteUserMessage(final AppUser appUser, final Update update);
    void sendMessage(final Long chatId, final Answer answer);
    AppUser findOrSaveAppUser(final Update update);
    void changeUserState(final AppUser appUser, final UserState userState);
}
