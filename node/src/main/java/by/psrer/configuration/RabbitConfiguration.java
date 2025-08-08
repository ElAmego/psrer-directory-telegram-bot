package by.psrer.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static by.psrer.model.RabbitQueue.ANSWER_MESSAGE;
import static by.psrer.model.RabbitQueue.BUTTON_CALLBACK;
import static by.psrer.model.RabbitQueue.DELETE_MESSAGE;
import static by.psrer.model.RabbitQueue.IMAGES;
import static by.psrer.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Configuration
@SuppressWarnings("unused")
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(ANSWER_MESSAGE);
    }

    @Bean
    public Queue buttonCallbackQueue() {
        return new Queue(BUTTON_CALLBACK);
    }

    @Bean
    public Queue deleteMessageQueue() {
        return new Queue(DELETE_MESSAGE);
    }

    @Bean
    public Queue imagesQueue() {
        return new Queue(IMAGES);
    }
}