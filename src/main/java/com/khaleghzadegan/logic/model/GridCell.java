package com.khaleghzadegan.logic.model;

import com.khaleghzadegan.logic.exception.FixedCellChangeNotAllowedException;

public class GridCell {
    private Integer cellValue;
    private final CellType cellType;

    public GridCell(Integer cellValue, CellType cellType) {
        this.cellValue = cellValue;
        this.cellType = cellType;
    }

    public GridCell(GridCell gridCell) {
        cellValue = gridCell.getCellValue();
        cellType = gridCell.getCellType();
    }

    public Integer getCellValue() {
        return cellValue;
    }

    public void setCellValue(Integer cellValue) {
        if (cellType.equals(CellType.CHANGEABLE)) {
            this.cellValue = cellValue;
        } else {
            throw new FixedCellChangeNotAllowedException("Cell value is fixed and can not be changed!");
        }
    }

    public CellType getCellType() {
        return cellType;
    }

    public enum CellType {
        FIXED,
        CHANGEABLE
    }

    @Override
    public String toString() {
        return "GridCell{" +
                "cellValue=" + cellValue +
                ", cellType=" + cellType +
                '}';
    }
}