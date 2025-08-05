package by.psrer.command.user.impl;

import by.psrer.command.user.CancelCommand;
import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import org.springframework.stereotype.Service;

import static by.psrer.entity.enums.UserState.BASIC;

@Service
public final class CancelCommandImpl implements CancelCommand {
    private final AppUserDAO appUserDAO;

    public CancelCommandImpl(final AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    @Override
    public String cancelSelection(final AppUser appUser) {
        appUser.setUserState(BASIC);
        appUserDAO.save(appUser);

        return "Вы вышли из режима выбора.";
    }
}