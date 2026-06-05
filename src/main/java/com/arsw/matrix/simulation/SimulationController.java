package com.arsw.matrix.simulation;

import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.GameResult;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private final GameBoard board;
    private final int stepDelayMs;
    private final int renderDelayMs;

    public SimulationController(GameBoard board, int stepDelayMs, int renderDelayMs) {
        this.board = board;
        this.stepDelayMs = stepDelayMs;
        this.renderDelayMs = renderDelayMs;
    }

    public void start() throws InterruptedException {
        int agentCount = board.getAgentPositions().size();

        NeoThread neo = new NeoThread(board, stepDelayMs);
        List<AgentThread> agents = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            agents.add(new AgentThread(board, i, stepDelayMs));
        }

        // render de los Threats
        Thread renderer = new Thread(() -> {
            while (!board.isGameOver()) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("=== LA MATRIX ===");
                System.out.println(board.render());
                try {
                    Thread.sleep(renderDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Renderer");
        renderer.setDaemon(true);

        System.out.println("Iniciando la simulacion...\n");
        System.out.println(board.render());
        Thread.sleep(1000);

        renderer.start();
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
