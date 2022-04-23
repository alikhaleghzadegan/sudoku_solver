package com.khaleghzadegan.logic.exception;

public class FixedCellChangeNotAllowedException extends RuntimeException {

    public FixedCellChangeNotAllowedException(String message) {
        super(message);
    }

    public FixedCellChangeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
