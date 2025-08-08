package by.psrer.controller;

import by.psrer.dto.ImageDTO;
import by.psrer.service.CallbackProducer;
import by.psrer.service.UpdateProducer;
import by.psrer.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static by.psrer.model.RabbitQueue.BUTTON_CALLBACK;
import static by.psrer.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j
public final class UpdateController {
    private TelegramBot telegramBot;
    private final UpdateProducer updateProducer;
    private final CallbackProducer callbackProducer;

    public UpdateController(final UpdateProducer updateProducer, CallbackProducer callbackProducer) {
        this.updateProducer = updateProducer;
        this.callbackProducer = callbackProducer;
    }

    public void registerBot(final TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(final Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else if (update.hasCallbackQuery()) {
            processCallback(update);
        } else {
            log.error("Received unsupported message type " + update);
        }
    }

    private void distributeMessagesByType(final Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            processTextMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processTextMessage(final Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processCallback(final Update update) {
        callbackProducer.produce(BUTTON_CALLBACK, update.getCallbackQuery());
    }

    private void setUnsupportedMessageTypeView(final Update update) {
        SendMessage sendMessage = MessageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    public void setView(final SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void deleteTelegramMessage(final DeleteMessage deleteMessage) {
        telegramBot.deleteMessage(deleteMessage);
    }

    public void imageDTOHandler(final ImageDTO imageDTO) {
        try (final InputStream is = new ByteArrayInputStream(imageDTO.getFileBytes())) {
            final InputFile photo = new InputFile(is, imageDTO.getFileName());
            final SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(imageDTO.getChatId())
                    .photo(photo)
                    .build();

            telegramBot.sendImage(sendPhoto);
        } catch (Exception e) {
            log.error(e);
        }
    }
}