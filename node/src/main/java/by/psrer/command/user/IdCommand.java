package by.psrer.command.user;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface IdCommand {
    Answer handleCommandId(final AppUser appUser);
}