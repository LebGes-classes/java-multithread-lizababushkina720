package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tgBot extends TelegramLongPollingBot {
    // Пул потоков для обработки сообщений
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final String[] memes = {
            "https://i.pinimg.com/736x/0d/ac/24/0dac2453b5d44311d4a38f945c7193fe.jpg",
            "https://i.pinimg.com/736x/d7/eb/93/d7eb936a6df7bb401eb865b0627f61e1.jpg",
            "https://i.pinimg.com/736x/45/72/a7/4572a75503800f8cc1779a567f9917d5.jpg",
            "https://i.pinimg.com/736x/30/9b/f1/309bf16c99d5438db69f0ed45b7c6cdc.jpg",
            "https://i.pinimg.com/736x/f4/ba/d3/f4bad39b22c95120be9a6eeaa2f4b44f.jpg"
    };

    @Override
    public String getBotToken() {
        return "8193331267:AAEA-od-xz0sQkC8KLDcdQfGP2np3nrl8Fs";
    }

    @Override
    public String getBotUsername() {
        return "attevka_bot"; // Имя вашего бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение и текст в нём
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            // Запускаем обработку в отдельном потоке
            executor.submit(() -> processMessage(text, chatId));
        }
    }

    private void processMessage(String text, long chatId) {
        try {
            if (text.equals("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText("Выберите номер мема от 1 до 5");
                execute(message);
            } else {
                // Пробуем преобразовать текст в число
                int memeNumber = Integer.parseInt(text);
                String memeUrl = getMeme(memeNumber);
                // Отправляем мем
                SendPhoto photo = new SendPhoto();
                photo.setChatId(String.valueOf(chatId));
                photo.setPhoto(new InputFile(memeUrl));
                execute(photo);
            }
        } catch (NumberFormatException e) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("отправьте корректный номер от 1 до 5!!!!!!!! или /start.");
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        } catch (TelegramApiException e) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Ошибка при отправке мема. Попробуйте снова.");
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getMeme(int number) {

        if (number < 1 || number > memes.length) {
            return null; // Null вызовет ошибку в processMessage
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return memes[number - 1];
    }
}