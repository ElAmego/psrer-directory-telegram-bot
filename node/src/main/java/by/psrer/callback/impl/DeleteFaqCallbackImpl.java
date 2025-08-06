package by.psrer.callback.impl;

import by.psrer.callback.DeleteFaqCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static by.psrer.entity.enums.UserState.BASIC;
import static by.psrer.entity.enums.UserState.DELETE_QUESTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class DeleteFaqCallbackImpl implements DeleteFaqCallback {
    private final MessageUtils messageUtils;
    private final QuestionDAO questionDAO;
    private final AppUserDAO appUserDAO;

    @Override
    public Answer handleCallbackDeleteFaq(final Long chatId) {
        final String output = "Введите номер вопроса, который вы хотите удалить (Например: 1):";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserState(appUser, DELETE_QUESTION);
        return new Answer(output, null);
    }

    @Override
    public Answer deleteQuestion(final Long telegramUserId, final String cmd) {
        if (cmd.matches("[-+]?\\d+")) {
            final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(telegramUserId);
            final StringBuilder output = new StringBuilder();
            final int selectedValue = Integer.parseInt(cmd);
            final Optional<Question> question = questionDAO.findNthSafely(selectedValue);

            if (question.isPresent()) {
                questionDAO.deleteQuestionByQuestionId(question.get().getQuestionId());
                messageUtils.changeUserState(appUser, BASIC);
                output.append("Вопрос \"").append(question.get().getQuestion()).append("\" успешно удалён.");
            } else {
                output.append("Такого вопроса нет в базе данных, введите корректное значение или выйдите из режима: " +
                        "/cancel");
            }

            return new Answer(output.toString(), null);
        } else {
            return new Answer("Вы ввели некорректное число, введите заново или выйдите из режима: /cancel",
                    null);
        }
    }
}