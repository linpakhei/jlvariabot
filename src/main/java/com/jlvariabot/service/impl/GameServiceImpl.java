package com.jlvariabot.service.impl;

import com.jlvariabot.JLVariaBot;
import com.jlvariabot.service.GameService;
import com.jlvariabot.service.RockPaperScissorsService;
import com.jlvariabot.service.TicTacToeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    private JLVariaBot jlVariaBot;
    @Autowired
    private RockPaperScissorsService rpsService;
    @Autowired
    private TicTacToeService ticTacToeService;

    private String rockPaperScissorsName = "rockPaperScissors";
    private String ticTacToePreGame = "ticTacToePreGame";

    @Override
    public void menu(Update update) {
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(update.getMessage().getChatId())
                .setText("Which game you want to play?")
                .setReplyMarkup(inlineKeyboardMarkup());

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void menuCallback(Update update) {
        String callData = update.getCallbackQuery().getData();
        String[] callDataArr = callData.split("::");
        String gameName = callDataArr[1];

        if(gameName.equals(rockPaperScissorsName)) {
            rpsService.gamePlay(update);
        } else if(gameName.equals(ticTacToePreGame)) {
            ticTacToeService.preGame(update);
        }
    }

    @Override
    public void backToGameMenuCallback(Update update) {
        // Set variables
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText("Which game you want to play?")
                .setReplyMarkup(inlineKeyboardMarkup());

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*========================================= Private Function =========================================*/

    private InlineKeyboardMarkup inlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText("Rock Paper Scissors").setCallbackData("gameMenu::" + rockPaperScissorsName));
        firstRow.add(new InlineKeyboardButton().setText("Tic Tac Toe").setCallbackData("gameMenu::" + ticTacToePreGame));
        rowsInline.add(firstRow);
        return markupInline.setKeyboard(rowsInline);
    }
}
