package by.psrer.callback;

import by.psrer.utils.impl.Answer;

public interface DeleteFaqCallback {
    Answer handleCallbackDeleteFaq(final Long chatId);
    Answer deleteQuestion(final Long telegramUserId, final String cmd);
}
