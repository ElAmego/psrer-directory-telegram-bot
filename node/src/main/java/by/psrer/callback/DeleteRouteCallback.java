package by.psrer.callback;

import by.psrer.utils.impl.Answer;

public interface DeleteRouteCallback {
    Answer handleCallbackDeleteRoute(final Long chatId);
    Answer deleteRoute(final Long telegramUserId, final String cmd);
}