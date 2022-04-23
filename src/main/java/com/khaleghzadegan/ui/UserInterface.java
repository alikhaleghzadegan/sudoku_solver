package com.khaleghzadegan.ui;

import com.khaleghzadegan.logic.exception.InvalidInputException;
import com.khaleghzadegan.logic.model.GridCell;
import com.khaleghzadegan.logic.model.SudokuGrid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class UserInterface {

    public static SudokuGrid getSudokuGridFromUser() {
        SudokuGrid sudokuGrid = new SudokuGrid();
        var gridCells = sudokuGrid.getGridCells();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                gridCells[i][j] = getGridCell(i, j);
        return sudokuGrid;
    }

    private static GridCell getGridCell(int i, int j) {
        int iIndenx = i + 1;
        int jIndex = j + 1;
        System.out.print("Enter sudoku item " + iIndenx + ", " + jIndex + " (Enter 0 for not given): ");
        do {
            int value = getIntValueFromStdin();
            if (value == 0) {
                return new GridCell(value, GridCell.CellType.CHANGEABLE);
            } else if ((value >= 1) && (value <= 9)) {
                return new GridCell(value, GridCell.CellType.FIXED);
            } else {
                System.out.print("ERROR: invalid input range... Please try again: ");
            }
        } while (true);
    }

    private static int getIntValueFromStdin() {
        do {
            try {
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                return Integer.parseInt(stdin.readLine());
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.print("ERROR: invalid input value... Please try again: ");
            }
        } while (true);
    }


    public static void greetUser() {
        System.out.println("""
                Welcome to Sudoku Solver! ver 1.0
                Author: Ali Khaleghzadegan
                Copyright ©2022 under GPL v3
                """);
    }

    public static void displaySudoku(SudokuGrid sudokuGrid) {
        GridCell[][] gridCells = sudokuGrid.getGridCells();

        System.out.println("");
        System.out.println("          sudoku gird          ".toUpperCase());
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (gridCells[i][j].getCellValue() >= 1) {
                    System.out.print(gridCells[i][j].getCellValue() + ",");
                } else {
                    System.out.print("X,");
                }

                if ((j == 2) || (j == 5)) {
                    System.out.print("      ");
                }
                if (((i == 2) && (j == 8)) || ((i == 5) && (j == 8))) {
                    System.out.println("");
                    System.out.print("                              ");
                }
            }
            System.out.println("");
        }
    }

    public static boolean userReadyToStart() {
        System.out.println("");
        System.out.println("Ok! Now are you ready to begin? ");
        System.out.print("Please enter YES to start or NO to exit: ");
        String s = new Scanner(System.in).nextLine();
        if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y")) {
            return true;
        } else {
            return false;
        }
    }

    public static void printMessage(String message) {
        System.out.println(message);
    }

}
