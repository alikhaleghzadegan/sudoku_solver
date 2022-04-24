package com.khaleghzadegan.ui.service;

import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.ui.cli.impl.UserInterfaceServiceImpl;

public interface UserInterfaceService {
    void initializeUserInterface();

    static UserInterfaceService build() {
        return new UserInterfaceServiceImpl();
    }

}
