package com.jlvariabot.service;

import org.telegram.telegrambots.api.objects.Update;

public interface TicTacToeService {
    void preGame(Update update);
    void gamePlay(Update update);
}
