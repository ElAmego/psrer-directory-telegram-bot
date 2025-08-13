package by.psrer.utils.impl;

import by.psrer.dao.AppUserDAO;
import by.psrer.dto.ImageDTO;
import by.psrer.entity.AppUser;
import by.psrer.entity.RouteImage;
import by.psrer.entity.enums.UserState;
import by.psrer.service.ProducerService;
import by.psrer.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.psrer.entity.enums.Role.USER;
import static by.psrer.entity.enums.UserState.BASIC;

@Log4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class MessageUtilsImpl implements MessageUtils {
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final static Integer TELEGRAM_MESSAGE_LIMIT = 2500;

    @Override
    public void deleteUserMessage(final AppUser appUser, final Update update) {
        final DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(appUser.getTelegramUserId());
        deleteMessage.setMessageId(update.getMessage().getMessageId());
        producerService.produceDeleteMessage(deleteMessage);
    }

    @Override
    public void sendMessage(final Long chatId, final Answer answer) {
        if (answer.getAnswerText().length() > TELEGRAM_MESSAGE_LIMIT) {
            final List<Answer> answers = splitAnswer(answer);
            for (final Answer answerFromList: answers) {
                sendMessage(chatId, answerFromList);
            }
        } else {
            final List<InlineKeyboardButton> inlineKeyboardButtonList = answer.getInlineKeyboardButtonList();
            final SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(answer.getAnswerText())
                    .build();

            if (inlineKeyboardButtonList != null) {
                sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(
                                inlineKeyboardButtonList))
                        .build());
            }

            producerService.produceAnswer(sendMessage);
        }
    }

    private List<Answer> splitAnswer(Answer answer) {
        final List<Answer> answers = new ArrayList<>();
        final String mainAnswerText = answer.getAnswerText();
        final int outputLength = mainAnswerText.length();

        for (int i = 0; i < outputLength; i += TELEGRAM_MESSAGE_LIMIT) {
            int end = Math.min(i + TELEGRAM_MESSAGE_LIMIT, outputLength);
            final String chunk = mainAnswerText.substring(i, end);

            if (i + TELEGRAM_MESSAGE_LIMIT >= outputLength) {
                answers.add(new Answer(chunk, answer.getInlineKeyboardButtonList()));
            } else {
                answers.add(new Answer(chunk, null));
            }
        }

        return answers;
    }

    @Override
    public AppUser findOrSaveAppUser(final Update update) {
        final User telegramUser = update.getMessage().getFrom();

        final AppUser persistanceAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistanceAppUser == null) {
            final AppUser transientAppUser = AppUser.builder()
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

    public byte[] getImageBytes(final String fileId) {
        byte[] bytes = null;
        final String url = "https://lh3.googleusercontent.com/d/" + fileId + "=w1000";
        try {
            final HttpClient httpClient = HttpClient.newHttpClient();
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<byte[]> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            bytes = httpResponse.body();
        } catch (InterruptedException | IOException e) {
            log.error(e);
        }

        return bytes;
    }

    public String getTextFromTxtFile(final String fileId) {
        String text = null;
        final String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;

        try {
            final HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .cookieHandler(new CookieManager())
                    .build();

            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(downloadUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            final HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            text = httpResponse.body();

            if (text.contains("Google Drive - Virus scan warning")) {
                final String confirmUrl = extractConfirmUrl(text);
                if (confirmUrl != null) {
                    final HttpRequest confirmRequest = HttpRequest.newBuilder()
                            .uri(URI.create(confirmUrl))
                            .header("User-Agent", "Mozilla/5.0")
                            .build();
                    final HttpResponse<String> confirmResponse = httpClient.send(confirmRequest,
                            HttpResponse.BodyHandlers.ofString());
                    text = confirmResponse.body();
                }
            }

            if (text.contains("Error 404 (Not Found)")) {
                text = "При получении описания маршрута возникла ошибка.";
            }

        } catch (Exception e) {
            log.error("Failed to download text file: " + e.getMessage());
        }

        return text;
    }

    private String extractConfirmUrl(final String html) {
        final Pattern pattern = Pattern.compile("action=\"([^\"]+confirm=[^\"]+)\"");
        final Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return "https://drive.google.com" + matcher.group(1).replace("&amp;", "&");
        }
        return null;
    }

    public String extractMessageIdFromUrl(final String url) {
        final Pattern pattern = Pattern.compile("(?:/d/|id=|/open\\?id=)([a-zA-Z0-9_-]+)");
        final Matcher matcher = pattern.matcher(url);
        String link = null;
        if (matcher.find()) {
            link = matcher.group(1);
        }

        return link;
    }

    @Override
    public void changeUserState(final AppUser appUser, final UserState userState) {
        appUser.setUserState(userState);
        appUserDAO.save(appUser);
    }

    @Override
    public void changeUserStateWithIntermediateValue(final AppUser appUser, final UserState userState,
                                                     final Long intermediateValue) {
        appUser.setUserState(userState);
        appUser.setIntermediateValue(intermediateValue);
        appUserDAO.save(appUser);
    }

    @Override
    public void sendImage(final Long telegramUserId, final RouteImage routeImage) {
        final String routeImageUrl = routeImage.getRouteImageUrl();
        final String fileId = extractMessageIdFromUrl(routeImageUrl);
        final byte[] imageBytes = getImageBytes(fileId);

        if (imageBytes != null) {
            final String routeImageName = routeImage.getRouteImageFileName();
            final ImageDTO imageDTO = ImageDTO.builder()
                    .chatId(telegramUserId)
                    .fileBytes(imageBytes)
                    .fileName(routeImageName)
                    .build();

            producerService.produceImage(imageDTO);
        }
    }
}