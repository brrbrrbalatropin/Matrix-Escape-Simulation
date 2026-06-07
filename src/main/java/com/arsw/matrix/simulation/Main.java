package com.arsw.matrix.simulation;

import com.arsw.matrix.model.GameBoard;
public class Main {

    public static void main(String[] args) throws InterruptedException {
        GameBoard board = new GameBoard(12, 20);

        int[][] walls = {
            {0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{0,9},
            {0,10},{0,11},{0,12},{0,13},{0,14},{0,15},{0,16},{0,17},{0,18},{0,19},
            {11,0},{11,1},{11,2},{11,3},{11,4},{11,5},{11,6},{11,7},{11,8},{11,9},
            {11,10},{11,11},{11,12},{11,13},{11,14},{11,15},{11,16},{11,17},{11,18},{11,19},
            {1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{7,0},{8,0},{9,0},{10,0},
            {1,19},{2,19},{3,19},{4,19},{5,19},{6,19},{7,19},{8,19},{9,19},{10,19},
            {2,3},{3,3},{4,3},{5,3},
            {2,7},{2,8},{2,9},
            {4,6},{5,6},{6,6},{7,6},
            {3,12},{4,12},{5,12},
            {7,10},{8,10},{9,10},
            {6,15},{7,15},{8,15},
            {4,16},{4,17},
            {9,3},{9,4},{9,5},
        };
        for (int[] w : walls) board.placeWall(w[0], w[1]);

        board.placePhone(2, 17);
        board.placePhone(9, 1);

        board.placeNeo(5, 10);

        board.addAgent(2, 2);
        board.addAgent(8, 16);
        board.addAgent(1, 14);

        SimulationController controller = new SimulationController(board, 400);
        controller.start();
    }
}
