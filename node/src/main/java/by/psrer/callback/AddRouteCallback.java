package by.psrer.callback;

import by.psrer.utils.impl.Answer;

public interface AddRouteCallback {
    Answer handleCallbackAddRoute (final Long chatId);
    Answer getUserRouteName(final Long chatId, final String routeName);
    Answer getUserRouteDescription(final Long chatId, final String routeDescriptionUrl);
    Answer getUserRouteImageName(final Long chatId, final String routeImageName);
    Answer getUserRouteImageUrl(final Long chatId, final String routeImageUrl);
}