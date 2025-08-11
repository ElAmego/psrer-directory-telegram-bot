package by.psrer.service.impl;

import by.psrer.callback.AddFaqCallback;
import by.psrer.callback.DeleteFaqCallback;
import by.psrer.command.user.CancelCommand;
import by.psrer.command.user.FaqCommand;
import by.psrer.command.user.HelpCommand;
import by.psrer.command.user.RoutesCommand;
import by.psrer.command.user.StartCommand;
import by.psrer.entity.AppUser;
import by.psrer.service.CommandService;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.psrer.entity.enums.UserState.DELETE_QUESTION;
import static by.psrer.entity.enums.UserState.QUESTION;
import static by.psrer.entity.enums.UserState.QUESTION_ANSWER;
import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;
import static by.psrer.entity.enums.UserState.ROUTE_SELECTION;
import static by.psrer.service.enums.ServiceCommands.CANCEL;
import static by.psrer.service.enums.ServiceCommands.FAQ;
import static by.psrer.service.enums.ServiceCommands.HELP;
import static by.psrer.service.enums.ServiceCommands.ROUTES;
import static by.psrer.service.enums.ServiceCommands.START;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Log4j
public final class CommandServiceImpl implements CommandService {
    private final MessageUtils messageUtils;
    private final StartCommand startCommand;
    private final HelpCommand helpCommand;
    private final FaqCommand faqCommand;
    private final CancelCommand cancelCommand;
    private final AddFaqCallback addFaqCallback;
    private final DeleteFaqCallback deleteFaqCallback;
    private final RoutesCommand routesCommand;

    @Override
    public void handleCommand(final Update update) {
        final AppUser appUser = messageUtils.findOrSaveAppUser(update);

        final Answer answer = processServiceCommand(appUser, update);

        messageUtils.sendMessage(appUser.getTelegramUserId(), answer);
    }

    private Answer processServiceCommand(final AppUser appUser, final Update update) {
        final String cmd = update.getMessage().getText();

        if (appUser.getUserState() == QUESTION_SELECTION) {
            messageUtils.deleteUserMessage(appUser, update);

            if (CANCEL.equals(cmd)) {
                return cancelCommand.cancelSelection(appUser);
            }

            return faqCommand.questionSelectionProcess(cmd);
        } else if (appUser.getUserState() == QUESTION) {
            messageUtils.deleteUserMessage(appUser, update);
            return addFaqCallback.getUserQuestion(appUser.getTelegramUserId(), cmd);
        } else if (appUser.getUserState() == QUESTION_ANSWER) {
            messageUtils.deleteUserMessage(appUser, update);
            return addFaqCallback.getUserQuestionAnswer(appUser.getTelegramUserId(), cmd);
        } else if (appUser.getUserState() == DELETE_QUESTION) {
            messageUtils.deleteUserMessage(appUser, update);

            if (CANCEL.equals(cmd)) {
                return cancelCommand.cancelSelection(appUser);
            }

            return deleteFaqCallback.deleteQuestion(appUser.getTelegramUserId(), cmd);
        } else if (appUser.getUserState() == ROUTE_SELECTION) {
            messageUtils.deleteUserMessage(appUser, update);

            if (CANCEL.equals(cmd)) {
                return cancelCommand.cancelSelection(appUser);
            }

            return routesCommand.getRoute(appUser.getTelegramUserId(), cmd);
        }

        if (START.equals(cmd)) {
            return startCommand.handleCommandStart();
        } else if (HELP.equals(cmd)) {
            return helpCommand.handleCommandHelp();
        } else if (FAQ.equals(cmd)) {
            return faqCommand.handleCommandFaq(appUser);
        } else if (ROUTES.equals(cmd)) {
            return routesCommand.handleCommandRoutes(appUser);
        } else {
            messageUtils.deleteUserMessage(appUser, update);
            return new Answer("Вы ввели неизвестную команду.\nСписок доступных команд: /help",
                    null);
        }
    }
}