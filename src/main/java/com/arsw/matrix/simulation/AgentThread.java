package com.arsw.matrix.simulation;

import com.arsw.matrix.logic.Pathfinder;
import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class AgentThread extends Thread {

    private final GameBoard board;
    private final int agentIndex;
    private final CyclicBarrier calculateBarrier;
    private final CyclicBarrier moveBarrier;

    public AgentThread(GameBoard board, int agentIndex,
                       CyclicBarrier calculateBarrier, CyclicBarrier moveBarrier) {
        super("Agent-" + agentIndex);
        this.board = board;
        this.agentIndex = agentIndex;
        this.calculateBarrier = calculateBarrier;
        this.moveBarrier = moveBarrier;
    }

    @Override
    public void run() {
        System.out.println("[Agent-" + agentIndex + "] Online. Hunting Neo...");

        while (!board.isGameOver()) {
            Position agentPos = board.getAgentPositions().get(agentIndex);
            Position neoPos = board.getNeoPosition();
            Position next = Pathfinder.nextStepToward(board, agentPos, neoPos);

            try {
                calculateBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (board.isGameOver()) break;

            if (next != null) {
                boolean moved = board.moveAgent(agentIndex, next);
                if (moved) {
                    System.out.println("[Agent-" + agentIndex + "] se movió a " + next);
                }
            } else {
                System.out.println("[Agent-" + agentIndex + "] no hay camino hacia Neo, esperando...");
            }

            try {
                moveBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Agent-" + agentIndex + "] thread finalizado, resultado: " + board.getResult());
    }
}
