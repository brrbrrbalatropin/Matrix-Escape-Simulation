package com.arsw.matrix.simulation;

import com.arsw.matrix.logic.Pathfinder;
import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class NeoThread extends Thread {

    private final GameBoard board;
    private final CyclicBarrier barrier;
    private static final int DANGER_RADIUS = 2;

    public NeoThread(GameBoard board, CyclicBarrier barrier) {
        super("Neo");
        this.board = board;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        System.out.println("[Neo] Entrando a la Matrix...");

        while (!board.isGameOver()) {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (board.isGameOver()) break;

            Position current = board.getNeoPosition();
            List<Position> phones = board.getPhones();
            List<Position> agents = board.getAgentPositions();

            Position next = Pathfinder.nextStepForNeo(board, current, phones, agents, DANGER_RADIUS);

            if (next != null) {
                boolean moved = board.moveNeo(next);
                if (moved) {
                    System.out.println("[Neo] Se movió a " + next);
                }
            } else {
                System.out.println("[Neo] no path found, waiting...");
            }
        }

        System.out.println("[Neo] thread finalizado, resultado: " + board.getResult());
    }
}
