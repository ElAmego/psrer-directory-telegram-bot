package by.psrer.callback.impl;

import by.psrer.callback.AddFaqCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static by.psrer.entity.enums.UserState.QUESTION;
import static by.psrer.entity.enums.UserState.QUESTION_ANSWER;
import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class AddFaqCallbackImpl implements AddFaqCallback {
    private final MessageUtils messageUtils;
    private final AppUserDAO appUserDAO;
    private final QuestionDAO questionDAO;
    private static final int QUESTION_LIMIT = 255;
    private static final int QUESTION_ANSWER_LIMIT = 4000;

    @Override
    public Answer handleCallbackAddFaq(final Long chatId) {
        final String output = "Введите вопрос: ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserState(appUser, QUESTION);
        return new Answer(output, null);
    }

    @Override
    public Answer getUserQuestion(final Long chatId, final String question) {
        final Optional<Answer> limitError = messageUtils.checkLimit(QUESTION_LIMIT, question);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = "Введите ответ на вопрос: ";

        Question newQuestion = Question.builder()
                .question(question)
                .build();
        newQuestion = questionDAO.save(newQuestion);

        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserStateWithIntermediateValue(appUser, QUESTION_ANSWER, newQuestion.getQuestionId());

        return new Answer(output, null);
    }

    @Override
    public Answer getUserQuestionAnswer(final Long chatId, final String questionAnswer) {
        final Optional<Answer> limitError = messageUtils.checkLimit(QUESTION_ANSWER_LIMIT, questionAnswer);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = """
                Вопрос сохранен в базу данных.
                
                Вы возвращены в режим выбора, введите номер вопроса \
                (Например: 1) или выйдите из режима /cancel""";

        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserState(appUser, QUESTION_SELECTION);

        final Long questionId = appUser.getIntermediateValue();
        final Question question = questionDAO.findQuestionByQuestionId(questionId);
        question.setQuestionAnswer(questionAnswer);
        questionDAO.save(question);

        return new Answer(output, null);
    }
}