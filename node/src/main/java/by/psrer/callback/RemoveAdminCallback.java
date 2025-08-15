package by.psrer.callback;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface RemoveAdminCallback {
    Answer handleCallbackRemoveAdmin (final Long chatId);
    Answer changeUserRole(final AppUser appUser, final String text);
}