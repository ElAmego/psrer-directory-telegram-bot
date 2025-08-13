package by.psrer.callback.impl;

import by.psrer.callback.DeleteRouteCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.RouteDAO;
import by.psrer.dao.RouteImageDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Route;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static by.psrer.entity.enums.UserState.DELETE_ROUTE;
import static by.psrer.entity.enums.UserState.ROUTE_SELECTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class DeleteRouteCallbackImpl implements DeleteRouteCallback {
    private final MessageUtils messageUtils;
    private final RouteDAO routeDAO;
    private final RouteImageDAO routeImageDAO;
    private final AppUserDAO appUserDAO;

    @Override
    public Answer handleCallbackDeleteRoute(final Long chatId) {
        final String output = "Введите номер маршрута, который вы хотите удалить (Например: 1):";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserState(appUser, DELETE_ROUTE);
        return new Answer(output, null);
    }

    @Override
    public Answer deleteRoute(final Long telegramUserId, final String cmd) {
        if (cmd.matches("[-+]?\\d+")) {
            final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(telegramUserId);
            final StringBuilder output = new StringBuilder();
            final int selectedValue = Integer.parseInt(cmd);
            final Optional<Route> route = routeDAO.findNthSafely(selectedValue);

            if (route.isPresent()) {
                routeDAO.deleteRouteByRouteId(route.get().getRouteId());
                routeImageDAO.deleteRouteImageByRouteImageId(route.get().getRouteImageId().getRouteImageId());
                messageUtils.changeUserState(appUser, ROUTE_SELECTION);
                output.append("Маршрут \"").append(route.get().getRouteName()).append("\" успешно удалён.");
            } else {
                output.append("Такого маршрута нет в базе данных, введите корректное значение или выйдите из режима: " +
                        "/cancel");
            }

            return new Answer(output.toString(), null);
        } else {
            return new Answer("Вы ввели некорректное число, введите заново или выйдите из режима: /cancel",
                    null);
        }
    }
}
