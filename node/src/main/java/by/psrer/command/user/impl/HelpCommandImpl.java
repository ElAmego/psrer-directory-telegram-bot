package by.psrer.command.user.impl;

import by.psrer.command.user.HelpCommand;
import org.springframework.stereotype.Component;

@Component
public final class HelpCommandImpl implements HelpCommand {
    @Override
    public String handleCommandHelp() {
        return """
                Список доступных команд:
                /start – Стартовая страница бота.
                /faq – Список часто задаваемых вопросов и ответы на них.
                /help – Список доступных команд.""";
    }
}