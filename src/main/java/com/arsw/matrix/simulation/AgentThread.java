package com.arsw.matrix.simulation;

import com.arsw.matrix.logic.Pathfinder;
import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Each Agent runs as an independent thread.
 * Every step it recalculates the shortest BFS path toward Neo's current position.
 *
 * The CyclicBarrier ensures all actors start each round simultaneously,
 * but the race to write on the GameBoard is still non-deterministic.
 */
public class AgentThread extends Thread {

    private final GameBoard board;
    private final int agentIndex;
    private final CyclicBarrier barrier;

    public AgentThread(GameBoard board, int agentIndex, CyclicBarrier barrier) {
        super("Agent-" + agentIndex);
        this.board = board;
        this.agentIndex = agentIndex;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        System.out.println("[Agent-" + agentIndex + "] Cazando a Neo...");

        while (!board.isGameOver()) {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (board.isGameOver()) break;

            Position agentPos = board.getAgentPositions().get(agentIndex);
            Position neoPos = board.getNeoPosition();

            Position next = Pathfinder.nextStepToward(board, agentPos, neoPos);

            if (next != null) {
                boolean moved = board.moveAgent(agentIndex, next);
                if (moved) {
                    System.out.println("[Agent-" + agentIndex + "] se movió a " + next);
                }
            } else {
                System.out.println("[Agent-" + agentIndex + "] no hay camino hacia Neo, esperando...");
            }
        }

        System.out.println("[Agent-" + agentIndex + "] thread finalizado, resultado: " + board.getResult());
    }
}
