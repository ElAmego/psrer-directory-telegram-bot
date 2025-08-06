package by.psrer.command.user.impl;

import by.psrer.command.user.FaqCommand;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;

@Service
public final class FaqCommandImpl implements FaqCommand {
    private final AppUserDAO appUserDAO;
    private final QuestionDAO questionDAO;

    public FaqCommandImpl(final AppUserDAO appUserDAO, final QuestionDAO questionDAO) {
        this.appUserDAO = appUserDAO;
        this.questionDAO = questionDAO;
    }

    @Override
    public Answer handleCommandFaq(final AppUser appUser) {
        final List<Question> questionList = questionDAO.findAll();

        if (questionList.isEmpty()) {
            return new Answer("Список вопросов пуст.", null);
        }

        final StringBuilder output = new StringBuilder("Вы переключились в режим выбора вопросов, для выхода из режима " +
                "введите команду /cancel. Введите в чат цифру интересующего вас вопроса (например: 1)\nСписок вопросов:");
        int inc = 0;

        for (final Question question: questionList) {
            output.append("\n").append(++inc).append(": ").append(question.getQuestion());
        }

        appUser.setUserState(QUESTION_SELECTION);
        appUserDAO.save(appUser);
        return new Answer(output.toString(), null);
    }

    @Override
    public Answer questionSelectionProcess(final String cmd) {
        String output = "";
        if (cmd.matches("[-+]?\\d+")) {
            final int selectedValue = Integer.parseInt(cmd);
            final Optional<Question> question = questionDAO.findNthSafely(selectedValue);

            if (question.isPresent()) {
                final String questionText = question.get().getQuestion();
                final String questionAnswer = question.get().getQuestionAnswer();
                output += questionText + "\n" + questionAnswer;
            } else {
                output += "Такого значения нет в списке! Введите заново (Например: 1) или выйдите из режима выбора " +
                        "/cancel";
            }
        } else {
            output += "Вы ввели некорректное значение! Введите заново (Например: 1) или выйдите из режима выбора /cancel";
        }

        return new Answer(output, null);
    }
}