package by.psrer.utils;

import by.psrer.entity.AppUser;
import by.psrer.entity.RouteImage;
import by.psrer.entity.enums.UserState;
import by.psrer.utils.impl.Answer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface MessageUtils {
    void deleteUserMessage(final AppUser appUser, final Update update);
    void sendMessage(final Long chatId, final Answer answer);
    AppUser findOrSaveAppUser(final Update update);
    void changeUserState(final AppUser appUser, final UserState userState);
    void changeUserStateWithIntermediateValue(final AppUser appUser, final UserState userState,
                                              final Long intermediateValue);
    String extractMessageIdFromUrl(final String url);
    String getTextFromTxtFile(final String fileId);
    void sendImage(final Long telegramUserId, final RouteImage routeImageId);
    Optional<Answer> checkLimit(final int limit, final String text);
}