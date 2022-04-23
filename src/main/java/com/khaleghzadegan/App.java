package com.khaleghzadegan;

import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.logic.service.GamePlayService;

import static com.khaleghzadegan.ui.UserInterface.*;

public class App {
    private static final GamePlayService gamePlayService = GamePlayService.build();

    public static void main(String[] args) {
        greetUser();
        SudokuGrid sudokuGrid = getSudokuGridFromUser();
        displaySudoku(sudokuGrid);
        if (userReadyToStart()) {
            SudokuGrid answer = gamePlayService.tryToSolve(sudokuGrid);
            if (answer != null) {
                printMessage("Hooray! Answer found :)");
                displaySudoku(answer);
            } else {
                printMessage("Sorry! No answer found :(");
            }

        }
    }


}
