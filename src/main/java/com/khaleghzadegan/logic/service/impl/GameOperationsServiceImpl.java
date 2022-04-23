package com.khaleghzadegan.logic.service.impl;

import com.khaleghzadegan.logic.model.GridCell;
import com.khaleghzadegan.logic.model.SudokuGrid;
import com.khaleghzadegan.logic.service.GameOperationsService;

import java.util.*;

public class GameOperationsServiceImpl implements GameOperationsService {

    private static final Random RANDOM = new Random();

    @Override
    public void removeRepetition(SudokuGrid sudokuGrid) {
        replaceRepetitiveNumbersWithRandomNumbersRowWise(sudokuGrid);
        replaceRepetitiveNumbersWithRandomNumbersColumnWise(sudokuGrid);
    }

    private void replaceRepetitiveNumbersWithRandomNumbersRowWise(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] row = gridCells[i];
            replaceRepetitiveNumbersWithZeros(row);
            fillZeroCellsWithNoneRepeatedRandomNumbers(row);
        }
    }

    private void replaceRepetitiveNumbersWithRandomNumbersColumnWise(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] column = getColumn(i, gridCells);
            replaceRepetitiveNumbersWithZeros(column);
            fillZeroCellsWithNoneRepeatedRandomNumbers(column);
        }
    }

    private GridCell[] getColumn(int columnIndex, GridCell[][] gridCells) {
        GridCell[] array = new GridCell[SudokuGrid.GAME_BOUNDARY];
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            System.arraycopy(gridCells[i], columnIndex, array, i, 1);
        }
        return array;
    }


    private void replaceRepetitiveNumbersWithZeros(GridCell[] gridCellArray) {
        int[] lookupArray = getLookupArray(gridCellArray);
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            Integer value = gridCellArray[i].getCellValue();
            if (lookupArray[value] > 1 && gridCellArray[i].getCellType() == GridCell.CellType.CHANGEABLE) {
                lookupArray[value]--;
                gridCellArray[i].setCellValue(0);
            }
        }
    }

    private int[] getLookupArray(GridCell[] gridCellArray) {
        int[] lookupArray = new int[SudokuGrid.GAME_BOUNDARY + 1];
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            Integer value = gridCellArray[i].getCellValue();
            lookupArray[value]++;
        }
        return lookupArray;
    }

    private void fillZeroCellsWithNoneRepeatedRandomNumbers(GridCell[] gridCellArray) {
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            if (gridCellArray[i].getCellValue() == 0) {
                Integer nextInt = getRandomNumberNotIn(gridCellArray);
                gridCellArray[i].setCellValue(nextInt);
            }
        }
    }

    private Integer getRandomNumberNotIn(GridCell[] gridCellArray) {
        Integer[] availableNumbers = getAllNonZeroIntegersFromOneToMaxNotIn(gridCellArray);
        int index = RANDOM.nextInt(Objects.requireNonNull(availableNumbers).length);
        return availableNumbers[index];
    }

    private Integer[] getAllNonZeroIntegersFromOneToMaxNotIn(GridCell[] gridCellArray) {
        Set<Integer> temp = getAllIntegersFromOneToMax();
        Set<Integer> availableNumbers = getAvailableNonZeroIntegersIn(gridCellArray);
        temp.removeAll(availableNumbers);
        return temp.toArray(new Integer[0]);
    }

    private Set<Integer> getAllIntegersFromOneToMax() {
        Set<Integer> allNumbersSet = new HashSet<>();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            allNumbersSet.add(i + 1);
        }
        return allNumbersSet;
    }

    private Set<Integer> getAvailableNonZeroIntegersIn(GridCell[] gridCellArray) {
        Set<Integer> availableNumbers = new HashSet<>();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            Integer value = gridCellArray[i].getCellValue();
            if (value != 0) availableNumbers.add(value);
        }
        return availableNumbers;
    }


    @Override
    public void calculateAndUpdateFitnessValue(SudokuGrid sudokuGrid) {
        Integer rowFitnessValue = getFitnessValueForRows(sudokuGrid);
        Integer columnFitnessValue = getFitnessValueForColumns(sudokuGrid);
        Integer subSquareFitnessValue = getFitnessValueForSubSquares(sudokuGrid);
        Integer totalFitnessValue = rowFitnessValue + columnFitnessValue + subSquareFitnessValue;
        sudokuGrid.setGridFitnessValue(totalFitnessValue);
    }

    private Integer getFitnessValueForRows(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        var result = 0;
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] row = gridCells[i];
            result += getFitnessValueFor(row);
        }
        return result;
    }

    private Integer getFitnessValueForColumns(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        var result = 0;
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            GridCell[] column = getColumn(i, gridCells);
            result += getFitnessValueFor(column);
        }
        return result;
    }

    private Integer getFitnessValueFor(GridCell[] array) {
        var result = 0;
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            for (int j = i + 1; j < SudokuGrid.GAME_BOUNDARY; j++) {
                if (!array[i].getCellValue().equals(array[j].getCellValue())) result++;
            }
        }
        return result;
    }

    private Integer getFitnessValueForSubSquares(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        var result = 0;
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY / 3; i++) {
            for (int j = 0; j < SudokuGrid.GAME_BOUNDARY / 3; j++) {
                GridCell[] array = getFlattenedThreeByThreeSubSquare(i, j, gridCells);
                result += getFitnessValueFor(array);
            }
        }
        return result;
    }

    private GridCell[] getFlattenedThreeByThreeSubSquare(Integer row, Integer column, GridCell[][] gridCells) {
        GridCell[] array = new GridCell[9];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(gridCells[i + (3 * row)], 3 * column, array, i * 3, 3);
        }
        return array;
    }

    @Override
    public void performUniformRowWiseCrossover(SudokuGrid sudokuGrid1, SudokuGrid sudokuGrid2) {
        final var gridCells1 = sudokuGrid1.getGridCells();
        final var gridCells2 = sudokuGrid2.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            int temp = RANDOM.nextInt(10) + 1;
            if (temp <= 8) {
                System.arraycopy(gridCells1[i], 0, gridCells2[i], 0, SudokuGrid.GAME_BOUNDARY);
            }
        }
    }

    @Override
    public void performBitWiseMutation(SudokuGrid sudokuGrid) {
        final var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < SudokuGrid.GAME_BOUNDARY; i++) {
            for (int j = 0; j < SudokuGrid.GAME_BOUNDARY; j++) {
                int temp = RANDOM.nextInt(10) + 1;
                if (temp <= 2 && gridCells[i][j].getCellType() == GridCell.CellType.CHANGEABLE)
                    gridCells[i][j].setCellValue(RANDOM.nextInt(SudokuGrid.GAME_BOUNDARY) + 1);
            }
        }
    }

    @Override
    public List<SudokuGrid> performElitismAndGetNextGeneration(List<SudokuGrid> populationBeforeCrossover,
                                                               List<SudokuGrid> populationAfterMutation) {
        List<SudokuGrid> sudokuGrids = new ArrayList<>();
        sudokuGrids.addAll(populationBeforeCrossover);
        sudokuGrids.addAll(populationAfterMutation);
        sudokuGrids.sort((g1, g2) -> {
            if (g1.getGridFitnessValue().equals(g2.getGridFitnessValue())) {
                return 0;
            } else if (g1.getGridFitnessValue() < g2.getGridFitnessValue()) {
                return -1;
            } else {
                return 1;
            }
        });
        return sudokuGrids.subList(sudokuGrids.size() / 2, sudokuGrids.size());
    }

}
