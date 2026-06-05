package com.arsw.matrix.model;

import java.util.Objects;

public class Position {
    public final int row;
    public final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    public int hashCode() {
        return Objects.hash(row, col);
    }

    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
