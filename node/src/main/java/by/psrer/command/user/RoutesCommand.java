package by.psrer.command.user;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface RoutesCommand {
    Answer handleCommandRoutes(final AppUser appUser);
    Answer getRoute(final Long telegramUserId, final String cmd);
}