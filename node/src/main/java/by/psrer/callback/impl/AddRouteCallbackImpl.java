package by.psrer.callback.impl;

import by.psrer.callback.AddRouteCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.dao.RouteDAO;
import by.psrer.dao.RouteImageDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Route;
import by.psrer.entity.RouteImage;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static by.psrer.entity.enums.UserState.ROUTE;
import static by.psrer.entity.enums.UserState.ROUTE_DESCRIPTION;
import static by.psrer.entity.enums.UserState.ROUTE_IMAGE_NAME;
import static by.psrer.entity.enums.UserState.ROUTE_IMAGE_URL;
import static by.psrer.entity.enums.UserState.ROUTE_SELECTION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class AddRouteCallbackImpl implements AddRouteCallback {
    private final MessageUtils messageUtils;
    private final AppUserDAO appUserDAO;
    private final RouteDAO routeDAO;
    private final RouteImageDAO routeImageDAO;
    private final static int ROUTE_NAME_LIMIT = 1000;
    private final static int ROUTE_DESCRIPTION_URL = 255;
    private final static int ROUTE_IMAGE_FILE_NAME = 255;
    private final static int ROUTE_IMAGE_FILE_URL = 255;

    @Override
    public Answer handleCallbackAddRoute(final Long chatId) {
        final String output = "Введите название маршрута: ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserState(appUser, ROUTE);
        return new Answer(output, null);
    }

    @Override
    public Answer getUserRouteName(final Long chatId, final String routeName) {
        final Optional<Answer> limitError = messageUtils.checkLimit(ROUTE_NAME_LIMIT, routeName);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = "Введите ссылку на описание маршрута (.txt файл) в облаке. Не забудьте выдать общий " +
                "доступ (Пункт -> Все, у кого есть ссылка):";

        Route newRoute = Route.builder()
                .routeName(routeName)
                .build();
        newRoute = routeDAO.save(newRoute);

        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        messageUtils.changeUserStateWithIntermediateValue(appUser, ROUTE_DESCRIPTION, newRoute.getRouteId());

        return new Answer(output, null);
    }

    @Override
    public Answer getUserRouteDescription(final Long chatId, final String routeDescriptionUrl) {
        final Optional<Answer> limitError = messageUtils.checkLimit(ROUTE_DESCRIPTION_URL, routeDescriptionUrl);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = "Введите название с форматом изображения (Например: image.png) в облаке: ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);

        RouteImage routeImage = new RouteImage();
        routeImage = routeImageDAO.save(routeImage);

        final Long routeId = appUser.getIntermediateValue();
        final Route route = routeDAO.findRouteByRouteId(routeId);
        route.setRouteDescriptionUrl(routeDescriptionUrl);
        route.setRouteImageId(routeImage);
        routeDAO.save(route);

        messageUtils.changeUserStateWithIntermediateValue(appUser, ROUTE_IMAGE_NAME, routeImage.getRouteImageId());

        return new Answer(output, null);
    }

    @Override
    public Answer getUserRouteImageName(final Long chatId, final String routeImageName) {
        final Optional<Answer> limitError = messageUtils.checkLimit(ROUTE_IMAGE_FILE_NAME, routeImageName);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = "Введите ссылку на изображение в облаке. Не забудьте выдать общий доступ (Пункт -> Все," +
                " у кого есть ссылка): ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        final Long routeImageId = appUser.getIntermediateValue();

        final RouteImage routeImage = routeImageDAO.findRouteImageByRouteImageId(routeImageId);
        routeImage.setRouteImageFileName(routeImageName);
        routeImageDAO.save(routeImage);

        messageUtils.changeUserState(appUser, ROUTE_IMAGE_URL);

        return new Answer(output, null);
    }

    @Override
    public Answer getUserRouteImageUrl(final Long chatId, final String routeImageUrl) {
        final Optional<Answer> limitError = messageUtils.checkLimit(ROUTE_IMAGE_FILE_URL, routeImageUrl);

        if (limitError.isPresent()) {
            return limitError.get();
        }

        final String output = """
                Маршрут сохранен в базу данных.

                Вы возвращены в режим выбора, введите номер маршрута \
                (Например: 1) или выйдите из режима /cancel""";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);
        final Long routeImageId = appUser.getIntermediateValue();

        final RouteImage routeImage = routeImageDAO.findRouteImageByRouteImageId(routeImageId);
        routeImage.setRouteImageUrl(routeImageUrl);
        routeImageDAO.save(routeImage);

        messageUtils.changeUserState(appUser, ROUTE_SELECTION);

        return new Answer(output, null);
    }
}