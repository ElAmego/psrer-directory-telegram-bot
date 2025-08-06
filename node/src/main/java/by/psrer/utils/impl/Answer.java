package by.psrer.utils.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public final class Answer {
    private final String answerText;
    private final List<InlineKeyboardButton> inlineKeyboardButtonList;
}
