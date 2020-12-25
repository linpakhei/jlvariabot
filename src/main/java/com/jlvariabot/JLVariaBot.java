package com.jlvariabot;

import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

@Component
public class JLVariaBot extends TelegramLongPollingBot {
    @Value("${bot.prod.username}")
    private String prodUserName;
    @Value("${bot.prod.token}")
    private String prodToken;
    @Value("${bot.dev.username}")
    private String devUserName;
    @Value("${bot.dev.token}")
    private String devToken;

    @Value("${bot.env}")
    private String env;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void registerBot(){
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String inputText = update.getMessage().getText();
            log.info("input message: " + update.getMessage());

            if (checkPG(inputText, "/hello")) {
                sendMessage("World Dev", update);
            } else if (checkPG(inputText, "/test")) {
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(update.getMessage().getChatId())
                        .setText("You send /start");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Update message text").setCallbackData("update_msg_text"));
                rowInline.add(new InlineKeyboardButton().setText("Update message text 2").setCallbackData("update_msg_text_2"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                message.setReplyMarkup(markupInline);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals("update_msg_text")) {
                String answer = "Updated message text";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public String getBotUsername() {
        return ("prod".equals(env) ? prodUserName : devUserName);
    }

    @Override
    public String getBotToken() {
        return ("prod".equals(env) ? prodToken : devToken);
    }

    /*========================================= Private Function =========================================*/

    private boolean checkPG(String inputText, String command) {
        return (inputText.equals(command) || inputText.equals(command + "@" + getBotUsername()));
    }

    private void sendMessage(String text, Update update) {
        try {
            SendMessage sdMsgObj = new SendMessage();

            sdMsgObj.setText(text);
            sdMsgObj.setChatId(update.getMessage().getChatId());


            if(sdMsgObj.getText() != null)
                execute(sdMsgObj);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
