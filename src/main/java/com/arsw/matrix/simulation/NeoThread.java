package com.arsw.matrix.simulation;

import com.arsw.matrix.logic.Pathfinder;
import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

import java.util.List;

public class NeoThread extends Thread {

    private final GameBoard board;
    private final int stepDelayMs;
    private static final int DANGER_RADIUS = 2;

    public NeoThread(GameBoard board, int stepDelayMs) {
        super("Neo");
        this.board = board;
        this.stepDelayMs = stepDelayMs;
    }

    @Override
    public void run() {
        System.out.println("[Neo] Entrando a la Matrix...");

        while (!board.isGameOver()) {
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
                System.out.println("[Neo] no hay camino, esperando...");
            }

            try {
                Thread.sleep(stepDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Neo] thread finalizado, resultado: " + board.getResult());
    }
}
