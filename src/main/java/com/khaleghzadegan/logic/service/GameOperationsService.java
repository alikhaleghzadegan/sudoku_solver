package com.khaleghzadegan.logic.service;

import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.logic.service.impl.GameOperationsServiceImpl;

import java.util.List;

public interface GameOperationsService {

    void removeRepetition(SudokuGrid sudokuGrid);

    void calculateAndUpdateFitnessValue(SudokuGrid sudokuGrid);

    void performUniformRowWiseCrossover(SudokuGrid sudokuGrid1, SudokuGrid sudokuGrid2);

    void performBitWiseMutation(SudokuGrid sudokuGrid);

    List<SudokuGrid> performElitismAndGetNextGeneration(List<SudokuGrid> populationBeforeCrossover,
                                                        List<SudokuGrid> populationAfterMutation);

    static GameOperationsService build() {
        return new GameOperationsServiceImpl();
    }

}
