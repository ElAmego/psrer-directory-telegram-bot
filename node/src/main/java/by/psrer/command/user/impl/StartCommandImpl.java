package by.psrer.command.user.impl;

import by.psrer.command.user.StartCommand;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Component;

@Component
public final class StartCommandImpl implements StartCommand {
    @Override
    public Answer handleCommandStart() {
        return new Answer("""
                Добро пожаловать в Экскурсионный справочник ПГРЭЗ!
                
                Тут вы найдёте ответы на часто задаваемые вопросы, список доступных на данный момент маршрутов и их описание.
                
                Как с нами связаться?
                \uD83D\uDCDE+375(29)1111111 (Доступен также в WhatsApp, Telegram, Viber)
                ☎️+8(029)4033333
                
                Список доступных команд: /help""", null);
    }
}
