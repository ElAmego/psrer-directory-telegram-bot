package by.psrer.service.impl;

import by.psrer.command.user.CancelCommand;
import by.psrer.command.user.FaqCommand;
import by.psrer.command.user.HelpCommand;
import by.psrer.command.user.StartCommand;
import by.psrer.entity.AppUser;
import by.psrer.service.CommandService;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;
import static by.psrer.service.enums.ServiceCommands.CANCEL;
import static by.psrer.service.enums.ServiceCommands.FAQ;
import static by.psrer.service.enums.ServiceCommands.HELP;
import static by.psrer.service.enums.ServiceCommands.START;

@Service
@Log4j
public final class CommandServiceImpl implements CommandService {
    private final MessageUtils messageUtils;
    private final StartCommand startCommand;
    private final HelpCommand helpCommand;
    private final FaqCommand faqCommand;
    private final CancelCommand cancelCommand;

    public CommandServiceImpl(final MessageUtils messageUtils, final StartCommand startCommand,
                              final HelpCommand helpCommand, final FaqCommand faqCommand,
                              final CancelCommand cancelCommand) {
        this.messageUtils = messageUtils;
        this.startCommand = startCommand;
        this.helpCommand = helpCommand;
        this.faqCommand = faqCommand;
        this.cancelCommand = cancelCommand;
    }

    @Override
    public void handleCommand(final Update update) {
        final AppUser appUser = messageUtils.findOrSaveAppUser(update);

        final Answer answer = processServiceCommand(appUser, update);

        messageUtils.sendMessage(appUser, answer);
    }

    private Answer processServiceCommand(final AppUser appUser, final Update update) {
        final String cmd = update.getMessage().getText();

        if (appUser.getUserState() == QUESTION_SELECTION) {
            messageUtils.deleteUserMessage(appUser, update);

            if (CANCEL.equals(cmd)) {
                return cancelCommand.cancelSelection(appUser);
            }

            return faqCommand.questionSelectionProcess(cmd);
        }

        if (START.equals(cmd)) {
            return startCommand.handleCommandStart();
        } else if (HELP.equals(cmd)) {
            return helpCommand.handleCommandHelp();
        } else if (FAQ.equals(cmd)) {
            return faqCommand.handleCommandFaq(appUser);
        } else {
            messageUtils.deleteUserMessage(appUser, update);
            return new Answer("Вы ввели неизвестную команду.\nСписок доступных команд: /help",
                    null);
        }
    }
}
