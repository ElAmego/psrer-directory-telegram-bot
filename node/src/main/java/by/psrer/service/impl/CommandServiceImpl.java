package by.psrer.service.impl;

import by.psrer.dao.AppUserDAO;
import by.psrer.dao.QuestionDAO;
import by.psrer.entity.AppUser;
import by.psrer.entity.Question;
import by.psrer.service.CommandService;
import by.psrer.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

import static by.psrer.entity.enums.Role.USER;
import static by.psrer.entity.enums.UserState.BASIC;
import static by.psrer.entity.enums.UserState.QUESTION_SELECTION;
import static by.psrer.service.enums.ServiceCommands.CANCEL;
import static by.psrer.service.enums.ServiceCommands.FAQ;
import static by.psrer.service.enums.ServiceCommands.HELP;
import static by.psrer.service.enums.ServiceCommands.START;

@Service
@Log4j
public final class CommandServiceImpl implements CommandService {
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final QuestionDAO questionDAO;

    public CommandServiceImpl(final ProducerService producerService, AppUserDAO appUserDAO, QuestionDAO questionDAO) {
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.questionDAO = questionDAO;
    }

    @Override
    public void handleCommand(final Update update) {
        var appUser = findOrSaveAppUser(update);

        String output = processServiceCommand(appUser, update);

        sendMessage(appUser, output);
    }

    private String processServiceCommand(final AppUser appUser, final Update update) {
        final String cmd = update.getMessage().getText();

        if (appUser.getUserState() == QUESTION_SELECTION) {
            if (CANCEL.equals(cmd)) {
                return cancelSelection(appUser);
            }

            return questionSelectionProcess(cmd);
        }

        if (START.equals(cmd)) {
            return handleCommandStart();
        } else if (HELP.equals(cmd)) {
            return handleCommandHelp();
        } else if (FAQ.equals(cmd)) {
            return handleCommandFaq(appUser);
        } else {
            return "Вы ввели неизвестную команду.\n" +
                    "Список доступных команд: /help";
        }
    }

    private String questionSelectionProcess(final String cmd) {
        if (cmd.matches("[-+]?\\d+")) {
            final Integer selectedValue = Integer.valueOf(cmd);
            final Optional<Question> question = questionDAO.findNthSafely(selectedValue);

            if (question.isPresent()) {
                final String questionText = question.get().getQuestion();
                final String questionAnswer = question.get().getQuestionAnswer();
                return questionText + "\n" + questionAnswer;
            } else {
                return "Такого значения нет в списке! Введите заново (Например: 1) или выйдите из режима выбора /cancel";
            }
        } else {
            return "Вы ввели некорректное значение! Введите заново (Например: 1) или выйдите из режима выбора /cancel";
        }
    }

    private String cancelSelection(final AppUser appUser) {
        appUser.setUserState(BASIC);
        appUserDAO.save(appUser);

        return "Вы вышли из режима выбора.";
    }

    private String handleCommandFaq(final AppUser appUser) {
        String output = "Вы переключились в режим выбора вопросов, для выхода из режима введите команду /cancel. " +
                "Введите в чат цифру интересующего вас вопроса (например: 1)\n" +
                "Список вопросов:";

        final List<Question> questionList = questionDAO.findAll();
        int inc = 0;

        for (Question question: questionList) {
            output += "\n*" + ++inc + ": " + question.getQuestion() + "*";
        }

        appUser.setUserState(QUESTION_SELECTION);
        appUserDAO.save(appUser);
        return output;
    }

    private String handleCommandHelp() {
        return "Список доступных команд:\n" +
                "/start – Стартовая страница бота.\n" +
                "/help – Список доступных команд.";
    }

    private String handleCommandStart() {
        return "Добро пожаловать в Экскурсионный справочник ПГРЭЗ!\n" +
                "\n" +
                "Тут вы найдёте ответы на часто задаваемые вопросы, список доступных на данный момент маршрутов и их " +
                "описание.\n" +
                "\n" +
                "Как с нами связаться?\n" +
                "\uD83D\uDCDE+375(29)1111111 (Доступен также в WhatsApp, Telegram, Viber)\n" +
                "☎\uFE0F+8(029)4033333\n" +
                "\n" +
                "Список доступных команд: /help";
    }

    private void sendMessage(final AppUser appUser, final String output) {
        final Long chatId = appUser.getTelegramUserId();
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(final Update update) {
        User telegramUser = update.getMessage().getFrom();

        AppUser persistanceAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistanceAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    .userState(BASIC)
                    .role(USER)
                    .build();

            return appUserDAO.save(transientAppUser);
        }
        return persistanceAppUser;
    }
}
