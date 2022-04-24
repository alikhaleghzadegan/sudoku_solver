package com.khaleghzadegan;

import com.khaleghzadegan.logic.service.GamePlayService;
import com.khaleghzadegan.ui.service.UserInterfaceService;

public class App {

    public static void main(String[] args) {
        UserInterfaceService userInterfaceService = UserInterfaceService.build();
        userInterfaceService.initializeUserInterface();
    }


}
