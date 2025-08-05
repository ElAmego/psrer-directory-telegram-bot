package by.psrer.command.user.impl;

import by.psrer.command.user.CancelCommand;
import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import by.psrer.utils.impl.Answer;
import org.springframework.stereotype.Service;

import static by.psrer.entity.enums.UserState.BASIC;

@Service
public final class CancelCommandImpl implements CancelCommand {
    private final AppUserDAO appUserDAO;

    public CancelCommandImpl(final AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    @Override
    public Answer cancelSelection(final AppUser appUser) {
        appUser.setUserState(BASIC);
        appUserDAO.save(appUser);

        return new Answer("Вы вышли из режима выбора.", null);
    }
}