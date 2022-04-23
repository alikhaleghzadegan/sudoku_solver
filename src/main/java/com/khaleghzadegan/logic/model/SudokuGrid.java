package com.khaleghzadegan.logic.model;

public class SudokuGrid {

    public static final Integer GAME_BOUNDARY = 9;
    private final GridCell[][] gridCells;
    private Integer gridFitnessValue;

    public SudokuGrid() {
        gridFitnessValue = 0;
        gridCells = new GridCell[GAME_BOUNDARY][GAME_BOUNDARY];
    }

    public SudokuGrid(SudokuGrid sudokuGrid) {
        gridFitnessValue = sudokuGrid.getGridFitnessValue();
        gridCells = new GridCell[GAME_BOUNDARY][GAME_BOUNDARY];
        for (int i = 0; i < GAME_BOUNDARY; i++)
            for (int j = 0; j < GAME_BOUNDARY; j++) {
                var cell = sudokuGrid.getGridCells()[i][j];
                gridCells[i][j] = new GridCell(cell);
            }
    }


    public Integer getGridFitnessValue() {
        return gridFitnessValue;
    }

    public void setGridFitnessValue(Integer gridFitnessValue) {
        this.gridFitnessValue = gridFitnessValue;
    }

    public GridCell[][] getGridCells() {
        return gridCells;
    }
}
