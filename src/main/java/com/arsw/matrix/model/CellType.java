package com.arsw.matrix.model;

public enum CellType {
    EMPTY(' '),
    WALL('#'),
    PHONE('T'),
    NEO('N'),
    AGENT('A');

    private final char symbol;

    CellType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
