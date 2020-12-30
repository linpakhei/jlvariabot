package com.jlvariabot.service.impl;

import com.jlvariabot.JLVariaBot;
import com.jlvariabot.dao.FirebaseDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.toIntExact;

@Service
public class RockPaperScissorsServiceImpl implements RockPaperScissorsService {
    @Autowired
    private JLVariaBot jlVariaBot;
    @Autowired
    private FirebaseDao firebaseDao;

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

        String userId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        Map<String, Object> data = firebaseDao.getGameResultData(userId, "paperRockScissors");
        if(data == null) {
            data = new HashMap<>();
            data.put("win", Long.valueOf(0));
            data.put("draw", Long.valueOf(0));
            data.put("lose", Long.valueOf(0));
        }

        firebaseDao.addUserData(userId, update);

        String[] callDataArr = callData.split("::");
        String reply = "";

        if(botSideRandom == 0) {  // paper
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + "，DRAW！";
                data.put("draw", ((Long)data.get("draw")).intValue() + 1);
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", you WIN!";
                data.put("win", ((Long)data.get("win")).intValue() + 1);
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", you LOSE!";
                data.put("lose", ((Long)data.get("lose")).intValue() + 1);
            }
        } else if(botSideRandom == 1) {   // scissors
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + ", you LOSE!";
                data.put("lose", ((Long)data.get("lose")).intValue() + 1);
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", DRAW!";
                data.put("draw", ((Long)data.get("draw")).intValue() + 1);
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", you WIN!";
                data.put("win", ((Long)data.get("win")).intValue() + 1);
            }
        } else if(botSideRandom == 2) {   // rock
            if(callDataArr[1].equals("paper")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + paper + ", you WIN!";
                data.put("win", ((Long)data.get("win")).intValue() + 1);
            } else if(callDataArr[1].equals("scissors")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + scissors + ", you LOSE!";
                data.put("lose", ((Long)data.get("lose")).intValue() + 1);
            } else if(callDataArr[1].equals("rock")) {
                reply = "Me" + botSideArr[botSideRandom] + ", you" + rock + ", DRAW!";
                data.put("draw", ((Long)data.get("draw")).intValue() + 1);
            }
        }

        firebaseDao.addGameResultData(userId, data, "paperRockScissors");
        reply += "\n\n" + "Win: " + data.get("win") + "\n";
        reply += "Draw: " + data.get("draw") + "\n";
        reply += "Lose: " + data.get("lose") + "\n";

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
