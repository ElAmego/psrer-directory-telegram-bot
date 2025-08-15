package by.psrer.command.admin.impl;

import by.psrer.command.admin.AdminsCommand;
import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.psrer.entity.enums.Role.ADMIN;

@Service
@RequiredArgsConstructor
@Log4j
@SuppressWarnings("unused")
public class AdminsCommandImpl implements AdminsCommand {
    private final AppUserDAO appUserDAO;
    private final MessageUtils messageUtils;

    @Override
    public Answer handleCommandAdmins() {
        final StringBuilder output = new StringBuilder("Список администраторов:");
        final List<AppUser> appUserList = appUserDAO.findAllByRole(ADMIN);
        int inc = 0;

        for (final AppUser appUserFromList: appUserList) {
            log.debug(appUserFromList.getTelegramUserId());
            output.append("\n%d: %s %s, @%s\nid: %d\n".formatted(
                    ++inc, appUserFromList.getFirstName(), appUserFromList.getLastName(), appUserFromList.getUserName(),
                    appUserFromList.getTelegramUserId()));
        }

        return new Answer(output.toString(), null);
    }
}