package com.jlvariabot.service.impl;

import com.jlvariabot.JLVariaBot;
import com.jlvariabot.service.RockPaperScissorsService;
import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.toIntExact;

@Service
public class RockPaperScissorsServiceImpl implements RockPaperScissorsService {
    @Autowired
    private JLVariaBot jlVariaBot;

    private String rockPaperScissorsName = "rockPaperScissors";
    private String paper = "\uD83E\uDD1A\uD83C\uDFFB";
    private String scissors = "✌️\uD83C\uDFFB";
    private String rock = "\uD83D\uDC4A\uD83C\uDFFB";

    private String[] botSideArr = {paper, scissors, rock};

    @Override
    public void gamePlay(Update update) {
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText("Rock Paper Scissors");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // play button
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText(paper).setCallbackData(rockPaperScissorsName + "::paper"));
        firstRow.add(new InlineKeyboardButton().setText(scissors).setCallbackData(rockPaperScissorsName + "::scissors"));
        firstRow.add(new InlineKeyboardButton().setText(rock).setCallbackData(rockPaperScissorsName + "::rock"));
        rowsInline.add(firstRow);

        // back to game menu button
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton().setText("Back to Game Menu").setCallbackData("backToGameMenu"));
        rowsInline.add(secondRow);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gamePlayCallback(Update update) {
        // bot side: 0=paper, 1=scissors, 2=rock
        int botSideRandom = ThreadLocalRandom.current().nextInt(0, 3);
        log.info("botSideRandom: " + botSideRandom);

        // Set variables
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        String[] callDataArr = callData.split("::");
        String reply = "";

        if(botSideRandom == 0) {  // paper
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + "，DRAW！";
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", you WIN!";
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", you LOSE!";
            }
        } else if(botSideRandom == 1) {   // scissors
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + ", you LOSE!";
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", DRAW!";
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", you WIN!";
            }
        } else if(botSideRandom == 2) {   // rock
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + ", you WIN!";
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", you LOSE!";
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", DRAW!";
            }
        }

        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(reply);

        // Back to menu button
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText("Play Again").setCallbackData("gameMenu::" + rockPaperScissorsName));
        firstRow.add(new InlineKeyboardButton().setText("Back to Game Menu").setCallbackData("backToGameMenu"));
        rowsInline.add(firstRow);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*========================================= Private Function =========================================*/

}
