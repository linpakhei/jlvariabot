package com.jlvariabot;

import com.jlvariabot.service.GameService;
import com.jlvariabot.service.RockPaperScissorsService;
import com.jlvariabot.service.TicTacToeService;
import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class JLVariaBot extends TelegramLongPollingBot {
    @Autowired
    private GameService gameService;
    @Autowired
    private RockPaperScissorsService rpsService;
    @Autowired
    private TicTacToeService ticTacToeService;

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

            if(checkPG(inputText, "/game")) {
                gameService.menu(update);
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String callData = update.getCallbackQuery().getData();

            log.info("callData: " + callData);

            if(callData.contains("gameMenu::")) {
                gameService.menuCallback(update);
            } else if(callData.equals("backToGameMenu")) {
                gameService.backToGameMenuCallback(update);
            } else if(callData.contains("rockPaperScissors::")) {
                rpsService.gamePlayCallback(update);
            } else if(callData.contains("ticTacToePreGame::") || callData.contains("ticTacToeGamePlay::")) {
                ticTacToeService.gamePlay(update);
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

    // Check command belongs to private and group
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
