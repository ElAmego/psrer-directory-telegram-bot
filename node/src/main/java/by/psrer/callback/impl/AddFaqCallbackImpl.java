package by.psrer.callback.impl;

import by.psrer.callback.AddFaqCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Service;

import static by.psrer.entity.enums.UserState.BASIC;
import static by.psrer.entity.enums.UserState.QUESTION;
import static by.psrer.entity.enums.UserState.QUESTION_ANSWER;

@Service
public final class AddFaqCallbackImpl implements AddFaqCallback {
    private final AppUserDAO appUserDAO;
    private final QuestionDAO questionDAO;

    public AddFaqCallbackImpl(final AppUserDAO appUserDAO, final QuestionDAO questionDAO) {
        this.appUserDAO = appUserDAO;
        this.questionDAO = questionDAO;
    }

    @Override
    public Answer handleCallbackAddFaq(final Long chatId) {
        final String output = "Введите вопрос: ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        appUser.setUserState(QUESTION);
        appUserDAO.save(appUser);
        return new Answer(output, null);
    }

    @Override
    public Answer getUserQuestion(final Long chatId, final String question) {
        final String output = "Введите ответ на вопрос: ";

        Question newQuestion = new Question();
        newQuestion.setQuestion(question);
        newQuestion = questionDAO.save(newQuestion);

        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        appUser.setUserState(QUESTION_ANSWER);
        appUser.setIntermediateValue(newQuestion.getQuestionId());
        appUserDAO.save(appUser);

        return new Answer(output, null);
    }

    @Override
    public Answer getUserQuestionAnswer(final Long chatId, final String questionAnswer) {
        final String output = "Вопрос сохранен в базу данных.";

        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        appUser.setUserState(BASIC);
        appUserDAO.save(appUser);

        Long questionId = appUser.getIntermediateValue();
        Question question = questionDAO.findQuestionByQuestionId(questionId);
        question.setQuestionAnswer(questionAnswer);
        questionDAO.save(question);

        return new Answer(output, null);
    }
}
