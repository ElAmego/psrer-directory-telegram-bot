package by.psrer.command.user.impl;

import by.psrer.command.user.FaqCommand;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.psrer.entity.enums.Role.ADMIN;
import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class FaqCommandImpl implements FaqCommand {
    private final MessageUtils messageUtils;
    private final QuestionDAO questionDAO;

    @Override
    public Answer handleCommandFaq(final AppUser appUser) {
        final List<Question> questionList = questionDAO.findAll();
        final StringBuilder output = new StringBuilder();

        if (questionList.isEmpty()) {
            output.append("Список вопросов пуст.");
        } else {
            output.append("""
                    Вы переключились в режим выбора, для выхода из режима введите команду /cancel. \
                    Введите в чат цифру интересующего вас вопроса (например: 1)
                    
                    Список вопросов:""");

            int index = 0;

            for (final Question question: questionList) {
                output.append("\n").append(++index).append(": ").append(question.getQuestion());
            }

            messageUtils.changeUserState(appUser, QUESTION_SELECTION);
        }

        List<InlineKeyboardButton> inlineKeyboardButtonList = appUser.getRole() == ADMIN
                ? createFaqButtons(questionList.isEmpty())
                : null;

        return new Answer(output.toString(), inlineKeyboardButtonList);
    }

    private List<InlineKeyboardButton> createFaqButtons(final boolean isEmptyList) {
        final List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                .text("Добавить вопрос")
                .callbackData("addFaq")
                .build());

        if (!isEmptyList) {
            inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                    .text("Удалить вопрос")
                    .callbackData("deleteFaq")
                    .build());
        }

        return inlineKeyboardButtonList;
    }

    @Override
    public Answer questionSelectionProcess(final String cmd) {
        final StringBuilder output = new StringBuilder();
        if (cmd.matches("[-+]?\\d+")) {
            final int selectedValue = Integer.parseInt(cmd);
            final Optional<Question> question = questionDAO.findNthSafely(selectedValue);

            if (question.isPresent()) {
                final String questionText = question.get().getQuestion();
                final String questionAnswer = question.get().getQuestionAnswer();
                output.append(questionText).append("\n").append(questionAnswer).append("\n\n")
                        .append("Вы можете ввести другой номер или выйти из режима выбора – /cancel");
            } else {
                output.append("Такого значения нет в списке! Введите заново (Например: 1) или выйдите из режима выбора " +
                        "/cancel");
            }
        } else {
            output.append("Вы ввели некорректное значение! Введите заново (Например: 1) или выйдите из режима выбора " +
                    "/cancel");
        }

        return new Answer(output.toString(), null);
    }
}