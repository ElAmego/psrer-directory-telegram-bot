package by.psrer.command.user.impl;

import by.psrer.command.user.IdCommand;
import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public final class IdCommandImpl implements IdCommand {
    @Override
    public Answer handleCommandId(final AppUser appUser) {
        final Long appUserTelegramId = appUser.getTelegramUserId();

        return new Answer("Ваш id: " + appUserTelegramId, null);
    }
}