package com.khaleghzadegan.logic.service.impl;

import com.khaleghzadegan.logic.model.GridCell;
import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.logic.service.GameOperationsService;
import com.khaleghzadegan.logic.service.GamePlayService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayServiceImpl implements GamePlayService {

    private static final Random RANDOM = new Random();
    private static final Integer FITNESS_GOAL = 972;
    private static final Integer POPULATION_COUNT = 180;
    private static final Integer ITERATION_COUNT = 100_000;

    private final GameOperationsService gameOperationsService;


    public GamePlayServiceImpl() {
        this.gameOperationsService = GameOperationsService.build();
    }

    @Override
    public boolean validateBlueprint(SudokuGrid blueprintSudokuGrid) {
        if (validateRows(blueprintSudokuGrid) && validateColumns(blueprintSudokuGrid))
            return validateSubSquares(blueprintSudokuGrid);
        return false;
    }

    private boolean validateRows(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] row = gridCells[i];
            if (duplicateCellsExists(row)) return false;
        }
        return true;
    }

    private boolean validateColumns(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] column = getColumn(i, gridCells);
            if (duplicateCellsExists(column)) return false;
        }
        return true;
    }

    private GridCell[] getColumn(int columnIndex, GridCell[][] gridCells) {
        GridCell[] array = new GridCell[SudokuGrid.GAME_BOUNDARY];
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            System.arraycopy(gridCells[i], columnIndex, array, i, 1);
        }
        return array;
    }

    private boolean validateSubSquares(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY / 3; i++) {
            for (int j = 0; j < SudokuGrid.GAME_BOUNDARY / 3; j++) {
                GridCell[] array = getFlattenedThreeByThreeSubSquare(i, j, gridCells);
                if (duplicateCellsExists(array)) return false;
            }
        }
        return true;
    }

    private GridCell[] getFlattenedThreeByThreeSubSquare(Integer row, Integer column, GridCell[][] gridCells) {
        GridCell[] array = new GridCell[9];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(gridCells[i + (3 * row)], 3 * column, array, i * 3, 3);
        }
        return array;
    }

    private boolean duplicateCellsExists(GridCell[] array) {
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            for (int j = i + 1; j < SudokuGrid.GAME_BOUNDARY; j++) {
                if (gridCellsAreFixedAndHaveEqualValue(array[i], array[j])) return true;
            }
        }
        return false;
    }

    private boolean gridCellsAreFixedAndHaveEqualValue(GridCell gc1, GridCell gc2) {
        return gc1.getCellType().equals(GridCell.CellType.FIXED) &&
                gc2.getCellType().equals(GridCell.CellType.FIXED) &&
                gc1.getCellValue().equals(gc2.getCellValue());
    }

    @Override
    public SudokuGrid tryToSolve(SudokuGrid blueprintSudokuGrid) {
        if (!validateBlueprint(blueprintSudokuGrid))
            return null;

        int resetPoint = getResetPoint(blueprintSudokuGrid);
        int iterationCounter = 0;
        int resetPointCounter = 0;
        SudokuGrid answer;

        while (iterationCounter < ITERATION_COUNT) {
            var population = generateInitialPopulation(blueprintSudokuGrid);
            while (resetPointCounter < resetPoint) {
                answer = removeRepetitionAndUpdateFitnessValuesAndGetSolutionIfExist(population);
                if (answer != null) return answer;
                var populationBeforeCrossover = clonePopulation(population);
                performCrossover(population);
                answer = removeRepetitionAndUpdateFitnessValuesAndGetSolutionIfExist(population);
                if (answer != null) return answer;
                population.forEach(gameOperationsService::performBitWiseMutation);
                var populationAfterMutation = clonePopulation(population);
                answer = removeRepetitionAndUpdateFitnessValuesAndGetSolutionIfExist(population);
                if (answer != null) return answer;
                population = gameOperationsService.performElitismAndGetNextGeneration(populationBeforeCrossover, populationAfterMutation);
                iterationCounter++;
                resetPointCounter++;
            }
            resetPointCounter = 0;
        }
        return null;
    }

    private void performCrossover(List<SudokuGrid> population) {
        for (int i = 0; i < POPULATION_COUNT - 1; i++) {
            gameOperationsService.performUniformRowWiseCrossover(population.get(i), population.get(i + 1));
        }
    }

    private List<SudokuGrid> clonePopulation(List<SudokuGrid> population) {
        List<SudokuGrid> clonedPopulation = new ArrayList<>();
        for (var sudokuGrid : population) {
            clonedPopulation.add(new SudokuGrid(sudokuGrid));
        }
        return clonedPopulation;
    }

    private SudokuGrid removeRepetitionAndUpdateFitnessValuesAndGetSolutionIfExist(List<SudokuGrid> population) {
        population.forEach(gameOperationsService::removeRepetition);
        population.forEach(gameOperationsService::calculateAndUpdateFitnessValue);
        return searchForSolutionAndGetIfExists(population);
    }


    private List<SudokuGrid> generateInitialPopulation(SudokuGrid blueprintSudokuGrid) {
        List<SudokuGrid> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_COUNT; i++) {
            population.add(getNewSudokuGridBasedOn(blueprintSudokuGrid));
        }
        return population;
    }

    private SudokuGrid getNewSudokuGridBasedOn(SudokuGrid blueprintSudokuGrid) {
        SudokuGrid sudokuGrid = new SudokuGrid();
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            for (int j = 0; j < SudokuGrid.GAME_BOUNDARY; j++) {
                gridCells[i][j] = getNewGridCellBasedOn(blueprintSudokuGrid.getGridCells()[i][j]);
            }
        }
        return sudokuGrid;
    }

    private GridCell getNewGridCellBasedOn(GridCell gridCell) {
        if (gridCell.getCellType().equals(GridCell.CellType.FIXED)) {
            var value = gridCell.getCellValue();
            return new GridCell(value, GridCell.CellType.FIXED);
        } else {
            var value = RANDOM.nextInt(SudokuGrid.GAME_BOUNDARY) + 1;
            return new GridCell(value, GridCell.CellType.CHANGEABLE);
        }
    }

    private Integer getResetPoint(SudokuGrid blueprintSudokuGrid) {
        final var gridCells = blueprintSudokuGrid.getGridCells();
        Integer givenNumbers = getGivenNumbersCount(gridCells);
        if (givenNumbers <= 27) {
            return 2000;
        } else if (givenNumbers <= 29) {
            return 350;
        } else if (givenNumbers <= 31) {
            return 300;
        } else return 200;
    }

    private Integer getGivenNumbersCount(GridCell[][] gridCells) {
        Integer counter = 0;
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            for (int j = 0; j < SudokuGrid.GAME_BOUNDARY; j++) {
                if (gridCells[i][j].getCellType().equals(GridCell.CellType.FIXED)) counter++;
            }
        }
        return counter;
    }

    private SudokuGrid searchForSolutionAndGetIfExists(List<SudokuGrid> population) {
        for (var sudokuGrid : population) {
            if (sudokuGrid.getGridFitnessValue().equals(FITNESS_GOAL)) {
                return sudokuGrid;
            }
        }
        return null;
    }
}
