package by.psrer.service.impl;

import by.psrer.callback.AddAdminCallback;
import by.psrer.callback.AddFaqCallback;
import by.psrer.callback.AddRouteCallback;
import by.psrer.callback.DeleteFaqCallback;
import by.psrer.callback.DeleteRouteCallback;
import by.psrer.callback.RemoveAdminCallback;
import by.psrer.command.admin.AdminsCommand;
import by.psrer.command.user.CancelCommand;
import by.psrer.command.user.FaqCommand;
import by.psrer.command.user.HelpCommand;
import by.psrer.command.user.IdCommand;
import by.psrer.command.user.RoutesCommand;
import by.psrer.command.user.StartCommand;
import by.psrer.entity.AppUser;
import by.psrer.entity.enums.UserState;
import by.psrer.service.CommandService;
import by.psrer.service.enums.Command;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.psrer.entity.enums.Role.ADMIN;
import static by.psrer.entity.enums.UserState.BASIC;
import static by.psrer.entity.enums.UserState.DELETE_QUESTION;
import static by.psrer.entity.enums.UserState.DELETE_ROUTE;
import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;
import static by.psrer.entity.enums.UserState.REMOVE_ADMIN;
import static by.psrer.entity.enums.UserState.ROUTE_SELECTION;
import static by.psrer.entity.enums.UserState.SET_ADMIN;
import static by.psrer.service.enums.Command.ADMINS;
import static by.psrer.service.enums.Command.CANCEL;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class CommandServiceImpl implements CommandService {
    private final MessageUtils messageUtils;
    private final StartCommand startCommand;
    private final IdCommand idCommand;
    private final HelpCommand helpCommand;
    private final FaqCommand faqCommand;
    private final CancelCommand cancelCommand;
    private final AddFaqCallback addFaqCallback;
    private final DeleteFaqCallback deleteFaqCallback;
    private final RoutesCommand routesCommand;
    private final AddRouteCallback addRouteCallback;
    private final DeleteRouteCallback deleteRouteCallback;
    private final AdminsCommand adminsCommand;
    private final AddAdminCallback addAdminCallback;
    private final RemoveAdminCallback removeAdminCallback;

    @Override
    public void handleCommand(final Update update) {
        final AppUser appUser = messageUtils.findOrSaveAppUser(update);

        final Answer answer = processServiceCommand(appUser, update);

        messageUtils.sendMessage(appUser.getTelegramUserId(), answer);
    }

    private Answer processServiceCommand(final AppUser appUser, final Update update) {
        final String text = update.getMessage().getText();
        final Command command = Command.fromString(text);
        final UserState userState = appUser.getUserState();

        if ((userState == QUESTION_SELECTION || userState == DELETE_QUESTION || userState == ROUTE_SELECTION ||
                userState == DELETE_ROUTE || userState == SET_ADMIN || userState == REMOVE_ADMIN) && command == CANCEL) {
            messageUtils.deleteUserMessage(appUser, update);

            return cancelCommand.cancelSelection(appUser);
        }

        if (userState != BASIC) {
            messageUtils.deleteUserMessage(appUser, update);

            final Long telegramUserId = appUser.getTelegramUserId();

            return switch (userState) {
                case ROUTE_SELECTION -> routesCommand.getRoute(telegramUserId, text);
                case QUESTION_SELECTION -> faqCommand.questionSelectionProcess(text);
                case QUESTION -> addFaqCallback.getUserQuestion(telegramUserId, text);
                case QUESTION_ANSWER -> addFaqCallback.getUserQuestionAnswer(telegramUserId, text);
                case DELETE_QUESTION -> deleteFaqCallback.deleteQuestion(telegramUserId, text);
                case ROUTE -> addRouteCallback.getUserRouteName(telegramUserId, text);
                case ROUTE_DESCRIPTION -> addRouteCallback.getUserRouteDescription(telegramUserId, text);
                case ROUTE_IMAGE_NAME -> addRouteCallback.getUserRouteImageName(telegramUserId, text);
                case ROUTE_IMAGE_URL -> addRouteCallback.getUserRouteImageUrl(telegramUserId, text);
                case DELETE_ROUTE -> deleteRouteCallback.deleteRoute(telegramUserId, text);
                case SET_ADMIN -> addAdminCallback.changeUserRole(appUser, text);
                case REMOVE_ADMIN -> removeAdminCallback.changeUserRole(appUser, text);
                default -> new Answer("Недоступно", null);
            };
        }

        if (command == null) {
            messageUtils.deleteUserMessage(appUser, update);
            return new Answer("Вы ввели неизвестную команду.\nСписок доступных команд: /help",
                    null);
        }

        if (appUser.getRole() == ADMIN && command == ADMINS) {
            return adminsCommand.handleCommandAdmins();
        }

        return switch (command) {
            case START -> startCommand.handleCommandStart();
            case ID -> idCommand.handleCommandId(appUser);
            case HELP -> helpCommand.handleCommandHelp();
            case FAQ -> faqCommand.handleCommandFaq(appUser);
            case ROUTES -> routesCommand.handleCommandRoutes(appUser);
            default -> new Answer("Недоступно", null);
        };
    }
}