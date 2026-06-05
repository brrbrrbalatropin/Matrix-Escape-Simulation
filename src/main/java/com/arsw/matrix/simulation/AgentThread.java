package com.arsw.matrix.simulation;

import com.arsw.matrix.logic.Pathfinder;
import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

public class AgentThread extends Thread {

    private final GameBoard board;
    private final int agentIndex;
    private final int stepDelayMs;

    public AgentThread(GameBoard board, int agentIndex, int stepDelayMs) {
        super("Agent-" + agentIndex);
        this.board = board;
        this.agentIndex = agentIndex;
        this.stepDelayMs = stepDelayMs;
    }

    @Override
    public void run() {
        System.out.println("[Agent-" + agentIndex + "] Cazando a Neo...");

        while (!board.isGameOver()) {
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

            try {
                Thread.sleep(stepDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Agent-" + agentIndex + "] thread finalizado, resultado: " + board.getResult());
    }
}
