package by.psrer.callback.impl;

import by.psrer.callback.AddAdminCallback;
import by.psrer.dao.AppUserDAO;
import by.psrer.entity.AppUser;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static by.psrer.entity.enums.Role.ADMIN;
import static by.psrer.entity.enums.UserState.BASIC;
import static by.psrer.entity.enums.UserState.SET_ADMIN;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class AddAdminCallbackImpl implements AddAdminCallback {
    private final MessageUtils messageUtils;
    private final AppUserDAO appUserDAO;

    @Override
    public Answer handleCallbackAddAdmin(final Long chatId) {
        final String output = "Введите id пользователя: ";
        final AppUser appUser = appUserDAO.findAppUserByTelegramUserId(chatId);

        messageUtils.changeUserState(appUser, SET_ADMIN);

        return new Answer(output, null);
    }

    @Override
    public Answer changeUserRole(final AppUser appUser, final String text) {
        final String output = "";

        if (text.matches("[-+]?\\d+")) {
            final Long selectedAppUserId = Long.valueOf(text);
            final AppUser selectedAppUser = appUserDAO.findAppUserByTelegramUserId(selectedAppUserId);

            if (selectedAppUser == null) {
                return new Answer("Пользователь с таким id не найден! Введите заново или выйдите из режима" +
                        " выбора /cancel", null);
            }

            if (selectedAppUser.getRole() == ADMIN) {
                return new Answer("Пользователь с таким id уже ADMIN. Введите заново или выйдите из режима " +
                        "выбора /cancel", null);
            }

            selectedAppUser.setRole(ADMIN);
            appUserDAO.save(selectedAppUser);

            messageUtils.changeUserState(appUser, BASIC);

            messageUtils.sendMessage(selectedAppUser.getTelegramUserId(),
                    new Answer("Вам изменили роль на ADMIN", null));

            return new Answer("Роль изменена, вы вышли из режима выбора.", null);
        } else {
            return new Answer("Вы ввели некорректный id! Введите заново или выйдите из режима выбора /cancel",
                    null);
        }
    }
}