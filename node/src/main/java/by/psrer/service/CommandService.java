package by.psrer.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandService {
    void handleCommand(final Update update);
}
