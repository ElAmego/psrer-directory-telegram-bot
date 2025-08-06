package by.psrer.callback;

import by.psrer.utils.impl.Answer;

public interface AddFaqCallback {
    Answer handleCallbackAddFaq (final Long chatId);
    Answer getUserQuestion(final Long chatId, final String question);
    Answer getUserQuestionAnswer(final Long chatId, final String questionAnswer);
}
