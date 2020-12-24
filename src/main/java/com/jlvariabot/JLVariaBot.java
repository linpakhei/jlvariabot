package com.jlvariabot;

import com.jlvariabot.Utils.log;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class JLVariaBot extends TelegramLongPollingBot {
    public void onUpdateReceived(Update update) {

        String inputText=update.getMessage().getText();
        log.info("input message: " + update.getMessage());

        SendMessage message = new SendMessage();

        if(checkPG(inputText, "/hello")){
            message.setText("World");
        }

        if (checkPG(inputText, "/test")){
            message.setText("good");
        }

        message.setChatId(update.getMessage().getChatId());


        try {
            if(message.getText() != null)
                execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    public String getBotUsername() {
        return "jlvariabot";
    }

    public String getBotToken() {
        return "1429201383:AAG97dhytzYKCBBttkTSSwICRXkJ416pmIk";
    }

    /*========================================= Private Function =========================================*/

    private boolean checkPG(String inputText, String command) {
        return (inputText.equals(command) || inputText.equals(command + "@jlvariabot"));
    }
}
