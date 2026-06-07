package com.arsw.matrix.simulation;

import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.GameResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class SimulationController {

    private final GameBoard board;
    private final int roundDelayMs;

    public SimulationController(GameBoard board, int roundDelayMs) {
        this.board = board;
        this.roundDelayMs = roundDelayMs;
    }

    public void start() throws InterruptedException {
        int agentCount = board.getAgentPositions().size();
        int totalActors = agentCount + 1;

        Runnable barrierAction = () -> {
            if (board.isGameOver()) return;
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("=== LA MATRIX ===");
            System.out.println(board.render());
            try {
                Thread.sleep(roundDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        CyclicBarrier barrier = new CyclicBarrier(totalActors, barrierAction);

        NeoThread neo = new NeoThread(board, barrier);
        List<AgentThread> agents = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            agents.add(new AgentThread(board, i, barrier));
        }

        System.out.println("=== LA MATRIX ===");
        System.out.println(board.render());
        Thread.sleep(1000);

        neo.start();
        for (AgentThread agent : agents) {
            agent.start();
        }

        neo.join();
        for (AgentThread agent : agents) {
            agent.join();
        }

        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("=== FIN DE LA SIMULACION ===");
        System.out.println(board.render());

        GameResult result = board.getResult();
        if (result == GameResult.NEO_WINS) {
            System.out.println(">>> NEO ESCAPÓ LA MATRIX <<<");
        } else if (result == GameResult.AGENTS_WIN) {
            System.out.println(">>> LOS AGENTES ATRAPARON A NEO <<<");
        }
    }
}
