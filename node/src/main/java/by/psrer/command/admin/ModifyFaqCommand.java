package by.psrer.command.admin;

import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;

public interface ModifyFaqCommand {
    Answer handleCommandModifyFaq(final AppUser appUser);
}
