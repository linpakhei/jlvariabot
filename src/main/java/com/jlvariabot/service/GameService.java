package com.jlvariabot.service;

import org.telegram.telegrambots.api.objects.Update;

public interface GameService {
    void menu(Update update);
    void menuCallback(Update update);
    void backToGameMenuCallback(Update update);
}
