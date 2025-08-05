package by.psrer.command.user;

import by.psrer.entity.AppUser;

public interface FaqCommand {
    String handleCommandFaq(final AppUser appUser);
    String questionSelectionProcess(final String cmd);
}