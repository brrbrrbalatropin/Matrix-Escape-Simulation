package com.arsw.matrix.model;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {

    private final int rows;
    private final int cols;
    private final boolean[][] walls;
    private final List<Position> phones;

    private Position neoPosition;
    private final List<Position> agentPositions;

    private volatile GameResult result = GameResult.ONGOING;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.walls = new boolean[rows][cols];
        this.phones = new ArrayList<>();
        this.agentPositions = new ArrayList<>();
    }

    public void placeWall(int row, int col) {
        walls[row][col] = true;
    }

    public void placePhone(int row, int col) {
        phones.add(new Position(row, col));
    }

    public void placeNeo(int row, int col) {
        this.neoPosition = new Position(row, col);
    }

    public void addAgent(int row, int col) {
        agentPositions.add(new Position(row, col));
    }

    public synchronized boolean moveNeo(Position target) {
        if (result != GameResult.ONGOING) return false;
        if (!isValidMove(target)) return false;

        neoPosition = target;

        if (phones.contains(target)) {
            result = GameResult.NEO_WINS;
            return true;
        }

        if (agentPositions.contains(target)) {
            result = GameResult.AGENTS_WIN;
            return true;
        }

        return true;
    }

    public synchronized boolean moveAgent(int agentIndex, Position target) {
        if (result != GameResult.ONGOING) return false;
        if (!isValidMove(target)) return false;
        if (phones.contains(target)) return false;

        agentPositions.set(agentIndex, target);

        if (target.equals(neoPosition)) {
            result = GameResult.AGENTS_WIN;
            return true;
        }

        return true;
    }

    public synchronized Position getNeoPosition() {
        return neoPosition;
    }

    public synchronized List<Position> getAgentPositions() {
        return new ArrayList<>(agentPositions);
    }

    public List<Position> getPhones() {
        return new ArrayList<>(phones);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isWall(int row, int col) {
        return walls[row][col];
    }

    public boolean isWall(Position p) {
        return walls[p.row][p.col];
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean inBounds(Position p) {
        return inBounds(p.row, p.col);
    }

    public GameResult getResult() {
        return result;
    }

    public boolean isGameOver() {
        return result != GameResult.ONGOING;
    }

    private boolean isValidMove(Position p) {
        return inBounds(p) && !isWall(p);
    }

    public synchronized String render() {
        char[][] grid = new char[rows][cols];

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = walls[r][c] ? '#' : ' ';

        for (Position p : phones)
            grid[p.row][p.col] = 'T';

        for (Position a : agentPositions)
            grid[a.row][a.col] = 'A';

        if (neoPosition != null)
            grid[neoPosition.row][neoPosition.col] = 'N';

        StringBuilder sb = new StringBuilder();
        String border = "+" + "-".repeat(cols * 2 - 1) + "+\n";
        sb.append(border);
        for (int r = 0; r < rows; r++) {
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                sb.append(grid[r][c]);
                if (c < cols - 1) sb.append(" ");
            }
            sb.append("|\n");
        }
        sb.append(border);
        return sb.toString();
    }
}
