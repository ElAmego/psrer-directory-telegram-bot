package by.psrer.command.user.impl;

import by.psrer.command.user.RoutesCommand;
import by.psrer.dao.RouteDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Route;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.psrer.entity.enums.Role.ADMIN;
import static by.psrer.entity.enums.UserState.ROUTE_SELECTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class RoutesCommandImpl implements RoutesCommand {
    private final MessageUtils messageUtils;
    private final RouteDAO routeDAO;

    @Override
    public Answer handleCommandRoutes(AppUser appUser) {
        final List<Route> routeList = routeDAO.findAll();
        final StringBuilder output = new StringBuilder();

        if (routeList.isEmpty()) {
            output.append("Список доступных маршрутов пуст.");
        } else {
            output.append("""
                    Вы переключились в режим выбора, для выхода из режима введите команду /cancel. \
                    Введите в чат цифру интересующего вас маршрута (например: 1)
                    
                    Список доступных на данный момент маршрутов:""");

            int index = 0;

            for (final Route route: routeList) {
                output.append("\n").append(++index).append(": ").append(route.getRouteName());
            }

            messageUtils.changeUserState(appUser, ROUTE_SELECTION);
        }

        final List<InlineKeyboardButton> inlineKeyboardButtonList = appUser.getRole() == ADMIN
                ? createRoutesButtons(routeList.isEmpty())
                : null;

        return new Answer(output.toString(), inlineKeyboardButtonList);
    }

    private List<InlineKeyboardButton> createRoutesButtons(final boolean isEmptyList) {
        final List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                .text("Добавить маршрут")
                .callbackData("addRoute")
                .build());

        if (!isEmptyList) {
            inlineKeyboardButtonList.add(InlineKeyboardButton.builder()
                    .text("Удалить маршрут")
                    .callbackData("deleteRoute")
                    .build());
        }

        return inlineKeyboardButtonList;
    }

    @Override
    public Answer getRoute(final Long telegramUserId, final String cmd) {
        final StringBuilder output = new StringBuilder();
        if (cmd.matches("[-+]?\\d+")) {
            final int selectedValue = Integer.parseInt(cmd);
            final Optional<Route> optionalRoute = routeDAO.findNthSafely(selectedValue);

            if (optionalRoute.isPresent()) {
                final Route route = optionalRoute.get();
                final String routeDescriptionUrl = route.getRouteDescriptionUrl();
                final String fileId = messageUtils.extractMessageIdFromUrl(routeDescriptionUrl);
                final String routeDescription = messageUtils.getTextFromTxtFile(fileId);

                if (routeDescription == null || routeDescription.isEmpty()) {
                    output.append("При получении описания маршрута возникла ошибка.");
                } else {
                    output.append(routeDescription);
                }

                output.append("\n\n").append("Вы можете ввести другой номер или выйти из режима выбора – /cancel");

                messageUtils.sendImage(telegramUserId, route.getRouteImageId());
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