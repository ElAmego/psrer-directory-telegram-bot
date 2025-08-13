package by.psrer.service.impl;

import by.psrer.callback.AddFaqCallback;
import by.psrer.callback.AddRouteCallback;
import by.psrer.callback.DeleteFaqCallback;
import by.psrer.callback.DeleteRouteCallback;
import by.psrer.service.CallbackService;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class CallbackServiceImpl implements CallbackService {
    private final MessageUtils messageUtils;
    private final AddFaqCallback addFaqCallback;
    private final DeleteFaqCallback deleteFaqCallback;
    private final AddRouteCallback addRouteCallback;
    private final DeleteRouteCallback deleteRouteCallback;

    @Override
    public void handleCallback(final CallbackQuery callbackQuery) {
        final Long chatId = callbackQuery.getMessage().getChatId();
        final String action = callbackQuery.getData();

        final Answer answer = processServiceCallback(action, chatId);

        messageUtils.sendMessage(chatId, answer);
    }

    private Answer processServiceCallback(final String action, final Long chatId) {
        return switch (action) {
            case "addFaq" -> addFaqCallback.handleCallbackAddFaq(chatId);
            case "deleteFaq" -> deleteFaqCallback.handleCallbackDeleteFaq(chatId);
            case "addRoute" -> addRouteCallback.handleCallbackAddRoute(chatId);
            case "deleteRoute" -> deleteRouteCallback.handleCallbackDeleteRoute(chatId);
            default -> new Answer("Недоступно", null);
        };
    }
}