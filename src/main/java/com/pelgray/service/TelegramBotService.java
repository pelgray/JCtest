package com.pelgray.service;

import com.pelgray.commands.DefaultHandler;
import com.pelgray.commands.ICommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotService.class);
    @Value("${tgBot.Name}")
    private String botUsername;
    @Value("${tgBot.Token}")
    private String botToken;
    @Autowired
    private List<ICommandHandler> commands;

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Метод для приема сообщений
     *
     * @param update содержит сообщение от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        LOG.debug("Получен запрос id={}", update.getUpdateId());
        if (!update.hasMessage()) {
            LOG.warn("Не понятно, как обработать запрос: {}", update.toString());
            return;
        }
        Message msg = update.getMessage();
        LOG.debug("Получено сообщение \"{}\" от пользователя {}", msg.getText(), msg.getFrom().getUserName());

        try {
            execute(handleCommand(msg));
            LOG.debug("Сообщение \"{}\" от пользователя {} обработано", msg.getText(), msg.getFrom().getUserName());
        } catch (TelegramApiException e) {
            LOG.error("Не удалось выполнить отправку ответного сообщения", e);
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Проверка наличия параметров
     *
     * @return {@code true}, если хотя бы один параметр не указан
     */
    public boolean parametersIsEmpty() {
        return (botUsername.isEmpty() || botToken.isEmpty());
    }

    /**
     * Метод возвращает ответ по полученному сообщению
     *
     * @param message сообщение от пользователя
     * @return сообщение-ответ
     */
    private SendMessage handleCommand(Message message) {
        ICommandHandler handler = commands.stream()
                .filter(command -> command.accept(message))
                .findFirst().orElse(new DefaultHandler());
        return handler.handle(message);
    }
}
