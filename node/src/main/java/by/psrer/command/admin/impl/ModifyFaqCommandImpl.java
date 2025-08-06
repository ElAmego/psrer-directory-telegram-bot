package by.psrer.command.admin.impl;

import by.psrer.command.admin.ModifyFaqCommand;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public final class ModifyFaqCommandImpl implements ModifyFaqCommand {
    private final QuestionDAO questionDAO;

    public ModifyFaqCommandImpl(final QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    @Override
    public Answer handleCommandModifyFaq(AppUser appUser) {
        final List<Question> questionList = questionDAO.findAll();
        final List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        StringBuilder output = new StringBuilder();

        inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                        .text("Добавить вопрос")
                        .callbackData("addFaq")
                        .build());

        if (questionList.isEmpty()) {
          output.append("Список вопросов пуст.");
        } else {
            output.append("Список вопросов:\n");
            int inc = 0;

            for (final Question question: questionList) {
                output.append("\n").append(++inc).append(": ").append(question.getQuestion());
            }

            inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                    .text("Удалить вопрос")
                    .callbackData("deleteFaq")
                    .build());
        }

        return new Answer(output.toString(), inlineKeyboardButtonList);
    }
}