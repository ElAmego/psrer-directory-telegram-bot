package by.psrer.service.impl;

import by.psrer.callback.AddFaqCallback;
import by.psrer.callback.DeleteFaqCallback;
import by.psrer.service.CallbackService;
import by.psrer.utils.MessageUtils;
import by.psrer.utils.impl.Answer;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@Log4j
public final class CallbackServiceImpl implements CallbackService {
    private final MessageUtils messageUtils;
    private final AddFaqCallback addFaqCallback;
    private final DeleteFaqCallback deleteFaqCallback;

    public CallbackServiceImpl(final MessageUtils messageUtils, final AddFaqCallback addFaqCallback,
                               final DeleteFaqCallback deleteFaqCallback) {
        this.messageUtils = messageUtils;
        this.addFaqCallback = addFaqCallback;
        this.deleteFaqCallback = deleteFaqCallback;
    }

    @Override
    public void handleCallback(final CallbackQuery callbackQuery) {
        final Long chatId = callbackQuery.getMessage().getChatId();
        final String action = callbackQuery.getData();

        final Answer answer = processServiceCallback(action, chatId);

        messageUtils.sendMessage(chatId, answer);
    }

    private Answer processServiceCallback(final String action, final Long chatId) {
        if (action.equals("addFaq")) {
            return addFaqCallback.handleCallbackAddFaq(chatId);
        } else if (action.equals("deleteFaq")) {
            return deleteFaqCallback.handleCallbackDeleteFaq(chatId);
        }

        return new Answer("Недоступно", null);
    }
}
