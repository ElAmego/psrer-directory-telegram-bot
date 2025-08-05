package by.psrer.command.user;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface CancelCommand {
    Answer cancelSelection(final AppUser appUser);
}