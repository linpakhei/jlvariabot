package com.jlvariabot.service.impl;

import com.jlvariabot.JLVariaBot;
import com.jlvariabot.dao.FirebaseDao;
import com.jlvariabot.service.TicTacToeService;
import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
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
public class TicTacToeServiceImpl implements TicTacToeService {
    @Autowired
    private JLVariaBot jlVariaBot;
    @Autowired
    private FirebaseDao firebaseDao;

    private String rockPaperScissorsName = "rockPaperScissors";

    private String ticTacToePreGame = "ticTacToePreGame";
    private String ticTacToeGamePlay = "ticTacToeGamePlay";
    private String cross = "❌";
    private String circle = "⭕";
    private String normal = "⬜";

    @Override
    public void preGame(Update update) {
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText("You use " + cross + " or " + circle + "？");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // play button
        // callback data:   ticTacToeGame :: [user option] :: [Game Board 1D Array] :: [User Move]
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText(cross).setCallbackData(ticTacToeGamePlay + "::1::0,0,0,0,0,0,0,0,0::-1"));
        firstRow.add(new InlineKeyboardButton().setText(circle).setCallbackData(ticTacToeGamePlay + "::2::0,0,0,0,0,0,0,0,0::-1"));
        rowsInline.add(firstRow);

        // back to game menu button
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton().setText("Back to game menu").setCallbackData("backToGameMenu"));
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
    public void gamePlay(Update update) {
        // Set variables
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        String[] callDataArr = callData.split("::");
        int userOption = Integer.parseInt(callDataArr[1]); // 1: cross / 2: circle
        String[] gameBoard = callDataArr[2].split(",");
        int userMove = Integer.parseInt(callDataArr[3]);

        int botOption = (userOption==1) ? 2 : 1;

        // Game Play Logic
        int firstPlay = ThreadLocalRandom.current().nextInt(0, 2);  // 0: bot first, 1: user first
        log.info("firstPlay: " + (firstPlay==0 ? "bot" : "user"));
        String result = "";

        // Assign user move first
        if(userMove != -1) {
            if(!"0".equals(gameBoard[userMove])) {
                // invalid move
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(update.getCallbackQuery().getId());
                answer.setText("\uD83D\uDEAB Illigal Move! \uD83D\uDEAB");
                answer.setShowAlert(true);
                try {
                    jlVariaBot.answerCallbackQuery(answer);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                return;
            } else {
                gameBoard[userMove] = String.valueOf(userOption);
            }

            result = checkFinish(userOption, gameBoard);
            log.info("user check finish: " + result);
        }

        if("continue".equals(checkFinish(userOption, gameBoard))) {
            // Bot's move
            if (firstPlay == 0 || userMove != -1) {
//                int botMove = ThreadLocalRandom.current().nextInt(0, 9);
//                while (!"0".equals(gameBoard[botMove])) {
//                    botMove = ThreadLocalRandom.current().nextInt(0, 9);
//                }
                int botMove = findBestMove(gameBoard, userOption);

                gameBoard[botMove] = String.valueOf(botOption);

                result = checkFinish(userOption, gameBoard);
                log.info("bot check finish: " + result);
            }

            if("continue".equals(checkFinish(userOption, gameBoard))) {
                // Draw the game board
                drawGameBoard(chatId, messageId, userOption, gameBoard);
            } else {
                drawFinish(chatId, messageId, result, gameBoard, update);
            }
        } else {
            drawFinish(chatId, messageId, result, gameBoard, update);
        }
    }

    /*========================================= Private Function =========================================*/

    private void drawGameBoard(long chatId, long messageId, int userOption, String[] gameBoard) {
        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText("You use " + ((userOption==1) ? cross : circle));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // first row button
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText(assignGameBoard(0, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 0));
        firstRow.add(new InlineKeyboardButton().setText(assignGameBoard(1, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 1));
        firstRow.add(new InlineKeyboardButton().setText(assignGameBoard(2, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 2));
        rowsInline.add(firstRow);

        // first row button
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton().setText(assignGameBoard(3, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 3));
        secondRow.add(new InlineKeyboardButton().setText(assignGameBoard(4, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 4));
        secondRow.add(new InlineKeyboardButton().setText(assignGameBoard(5, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 5));
        rowsInline.add(secondRow);

        // first row button
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        thirdRow.add(new InlineKeyboardButton().setText(assignGameBoard(6, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 6));
        thirdRow.add(new InlineKeyboardButton().setText(assignGameBoard(7, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 7));
        thirdRow.add(new InlineKeyboardButton().setText(assignGameBoard(8, gameBoard)).setCallbackData(ticTacToeGamePlay + "::" + userOption + "::" + changeArrToString(gameBoard) + "::" + 8));
        rowsInline.add(thirdRow);

        // back to game menu button
        List<InlineKeyboardButton> forthRow = new ArrayList<>();
        forthRow.add(new InlineKeyboardButton().setText("Back to Game Menu").setCallbackData("backToGameMenu"));
        rowsInline.add(forthRow);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void drawFinish(long chatId, long messageId, String result, String[] gameBoard, Update update) {
        String userId = String.valueOf(update.getCallbackQuery().getFrom().getId());

        String text = "";
        Map<String, Object> data = firebaseDao.getGameResultData(userId, "ticTacToe");
        if(data == null) {
            data = new HashMap<>();
            data.put("win", Long.valueOf(0));
            data.put("draw", Long.valueOf(0));
            data.put("lose", Long.valueOf(0));

        }

        firebaseDao.addUserData(userId, update);


        if("user".equals(result)) {
            text = "You win!";
            data.put("win", ((Long)data.get("win")).intValue() + 1);
        } else if("bot".equals(result)) {
            text = "Bot win!";
            data.put("lose", ((Long)data.get("lose")).intValue() + 1);
        } else {
            text = "Draw!";
            data.put("draw", ((Long)data.get("draw")).intValue() + 1);
        }

        firebaseDao.addGameResultData(userId, data, "ticTacToe");

        String gameBoardText =  assignGameBoard(0, gameBoard) + assignGameBoard(1, gameBoard) + assignGameBoard(2, gameBoard) + "\n" +
                assignGameBoard(3, gameBoard) + assignGameBoard(4, gameBoard) + assignGameBoard(5, gameBoard) + "\n" +
                assignGameBoard(6, gameBoard) + assignGameBoard(7, gameBoard) + assignGameBoard(8, gameBoard);

        text += "\n\n" + gameBoardText;
        text += "\n\n" + "Win: " + data.get("win") + "\n";
        text += "Draw: " + data.get("draw") + "\n";
        text += "Lose: " + data.get("lose") + "\n";

        EditMessageText message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(text);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // back to game menu button
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton().setText("Play again").setCallbackData("gameMenu::" + ticTacToePreGame));
        firstRow.add(new InlineKeyboardButton().setText("Back to Main Menu").setCallbackData("backToGameMenu"));
        rowsInline.add(firstRow);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            jlVariaBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String assignGameBoard(int index, String[] gameBoard) {
        if("0".equals(gameBoard[index])) {
            return normal;
        } else if("1".equals(gameBoard[index])) {
            return cross;
        } else {
            return circle;
        }
    }

    private String changeArrToString(String[] gameBoard) {
        String output = "";
        for(String element : gameBoard) {
            output += "," + element;
        }

        return output.substring(1, output.length());
    }

    // return "user", "bot", "draw" or "continue"
    private String checkFinish(int userOption, String[] gameBoard) {
        String result = "draw";

        for(int i=1; i<=2; i++) {
            String flag = String.valueOf(i);

            // Check horizontal line
            if ((flag.equals(gameBoard[0]) && flag.equals(gameBoard[1]) && flag.equals(gameBoard[2])) ||
                    (flag.equals(gameBoard[3]) && flag.equals(gameBoard[4]) && flag.equals(gameBoard[5])) ||
                    (flag.equals(gameBoard[6]) && flag.equals(gameBoard[7]) && flag.equals(gameBoard[8])))
                return (userOption==i ? "user" : "bot");

            // Check veritical line
            if ((flag.equals(gameBoard[0]) && flag.equals(gameBoard[3]) && flag.equals(gameBoard[6])) ||
                    (flag.equals(gameBoard[1]) && flag.equals(gameBoard[4]) && flag.equals(gameBoard[7])) ||
                    (flag.equals(gameBoard[2]) && flag.equals(gameBoard[5]) && flag.equals(gameBoard[8])))
                return (userOption==i ? "user" : "bot");

            // Check diagonal line
            if ((flag.equals(gameBoard[0]) && flag.equals(gameBoard[4]) && flag.equals(gameBoard[8])) ||
                    (flag.equals(gameBoard[2]) && flag.equals(gameBoard[4]) && flag.equals(gameBoard[6])))
                return (userOption==i ? "user" : "bot");

        }

        for(String gb : gameBoard) {
            if("0".equals(gb))
                return "continue";
        }

        return result;
    }

    /*========================================= Private Function (Minimax Algorithm) =========================================*/

    private int findBestMove(String[] gameBoard, int userOption) {
        int bestVal = -1000;
        int bestMove = -1;

        for(int i=0; i<9; i++) {
            if("0".equals(gameBoard[i])) {  // check only cell is empty
                gameBoard[i] = (userOption==1 ? "2" : "1");

                int moveVal = minimax(gameBoard, 0, false, userOption);

                gameBoard[i] = "0"; // undo the move

                if(moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }

        return bestMove;
    }

    private int minimax(String[] gameBoard, int depth, boolean isMax, int userOption) {
        if("user".equals(checkFinish(userOption, gameBoard)))
            return -10;
        else if("bot".equals(checkFinish(userOption, gameBoard)))
            return 10;
        else if("draw".equals(checkFinish(userOption, gameBoard)))
            return 0;

        if(isMax) { // maximazer's move (bot's move)
            int best = -1000;
            for(int i=0; i<9; i++) {
                if("0".equals(gameBoard[i])) {
                    gameBoard[i] = (userOption==1 ? "2" : "1");

                    best = Math.max(best, minimax(gameBoard, depth+1, !isMax, userOption));

                    gameBoard[i] = "0";
                }
            }

            return best;
        } else {    // minimizer's move (user's move)
            int best = 1000;

            for(int i=0; i<9; i++) {
                if("0".equals(gameBoard[i])) {
                    gameBoard[i] = (userOption==1 ? "1" : "2");

                    best = Math.min(best, minimax(gameBoard, depth+1, !isMax, userOption));

                    gameBoard[i] = "0";
                }
            }

            return best;
        }
    }
}
