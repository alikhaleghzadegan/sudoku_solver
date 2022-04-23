package com.khaleghzadegan.logic.service;

import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.logic.service.impl.GamePlayServiceImpl;

public interface GamePlayService {

    boolean validateBlueprint(SudokuGrid blueprintSudokuGrid);

    SudokuGrid tryToSolve(SudokuGrid blueprintSudokuGrid);

    static GamePlayService build() {
        return new GamePlayServiceImpl();
    }
}
