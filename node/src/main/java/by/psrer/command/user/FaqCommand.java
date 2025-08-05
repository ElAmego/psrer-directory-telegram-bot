package by.psrer.command.user;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface FaqCommand {
    Answer handleCommandFaq(final AppUser appUser);
    Answer questionSelectionProcess(final String cmd);
}