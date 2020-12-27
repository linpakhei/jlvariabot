package com.jlvariabot.service;

import org.telegram.telegrambots.api.objects.Update;

public interface RockPaperScissorsService {
    void gamePlay(Update update);
    void gamePlayCallback(Update update);
}
