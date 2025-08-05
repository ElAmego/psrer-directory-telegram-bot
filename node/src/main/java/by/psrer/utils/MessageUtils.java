package by.psrer.utils;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageUtils {
    void deleteUserMessage(final AppUser appUser, final Update update);
    void sendMessage(final AppUser appUser, final Answer answer);
    AppUser findOrSaveAppUser(final Update update);
}
