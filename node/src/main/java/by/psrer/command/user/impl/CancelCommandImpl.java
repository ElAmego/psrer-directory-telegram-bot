package by.psrer.command.user.impl;

import by.psrer.command.user.CancelCommand;
import by.psrer.entity.AppUser;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static by.psrer.entity.enums.UserState.BASIC;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class CancelCommandImpl implements CancelCommand {
    private final MessageUtils messageUtils;

    @Override
    public Answer cancelSelection(final AppUser appUser) {
        messageUtils.changeUserState(appUser, BASIC);

        return new Answer("Вы вышли из режима выбора.", null);
    }
}