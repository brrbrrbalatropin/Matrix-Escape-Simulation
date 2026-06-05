package com.arsw.matrix.logic;

import com.arsw.matrix.model.GameBoard;
import com.arsw.matrix.model.Position;

import java.util.*;

public class Pathfinder {

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
            {1, 0}, {1, -1}, {0, -1}, {-1, -1}
    };

    public static Position nextStepToward(GameBoard board, Position start, Position goal) {
        if (start.equals(goal)) return null;

        Map<Position, Position> parent = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();

        queue.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.row + dir[0], current.col + dir[1]);

                if (!board.inBounds(next) || board.isWall(next) || parent.containsKey(next))
                    continue;

                parent.put(next, current);

                if (next.equals(goal)) {
                    return reconstructFirstStep(parent, start, next);
                }

                queue.add(next);
            }
        }

        return null;
    }

    public static Position nextStepForNeo(GameBoard board, Position start,
                                          List<Position> phones,
                                          List<Position> agentPositions,
                                          int dangerRadius) {
        // Las 2 casillas cercanas a un agente son marcadas como peligrosas
        Set<Position> dangerZone = buildDangerZone(board, agentPositions, dangerRadius);

        Position bestStep = null;
        int bestDist = Integer.MAX_VALUE;

        for (Position phone : phones) {
            Position step = bfsWithDangerAvoidance(board, start, phone, dangerZone);
            if (step != null) {
                int dist = bfsDistance(board, start, phone, dangerZone);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestStep = step;
                }
            }
        }

        // Fallback
        if (bestStep == null) {
            for (Position phone : phones) {
                Position step = nextStepToward(board, start, phone);
                if (step != null) {
                    int dist = bfsDistanceSimple(board, start, phone);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestStep = step;
                    }
                }
            }
        }

        return bestStep;
    }

    private static Position bfsWithDangerAvoidance(GameBoard board, Position start,
                                                    Position goal, Set<Position> dangerZone) {
        if (start.equals(goal)) return null;

        Map<Position, Position> parent = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();

        queue.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.row + dir[0], current.col + dir[1]);

                if (!board.inBounds(next) || board.isWall(next) || parent.containsKey(next))
                    continue;
                if (dangerZone.contains(next))
                    continue;

                parent.put(next, current);

                if (next.equals(goal)) {
                    return reconstructFirstStep(parent, start, next);
                }

                queue.add(next);
            }
        }

        return null;
    }

    private static int bfsDistance(GameBoard board, Position start, Position goal,
                                   Set<Position> dangerZone) {
        if (start.equals(goal)) return 0;

        Map<Position, Integer> dist = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();

        queue.add(start);
        dist.put(start, 0);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int d = dist.get(current);

            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.row + dir[0], current.col + dir[1]);

                if (!board.inBounds(next) || board.isWall(next) || dist.containsKey(next))
                    continue;
                if (dangerZone.contains(next))
                    continue;

                dist.put(next, d + 1);
                if (next.equals(goal)) return d + 1;
                queue.add(next);
            }
        }

        return Integer.MAX_VALUE;
    }

    private static int bfsDistanceSimple(GameBoard board, Position start, Position goal) {
        if (start.equals(goal)) return 0;

        Map<Position, Integer> dist = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();

        queue.add(start);
        dist.put(start, 0);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int d = dist.get(current);

            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.row + dir[0], current.col + dir[1]);

                if (!board.inBounds(next) || board.isWall(next) || dist.containsKey(next))
                    continue;

                dist.put(next, d + 1);
                if (next.equals(goal)) return d + 1;
                queue.add(next);
            }
        }

        return Integer.MAX_VALUE;
    }

    private static Set<Position> buildDangerZone(GameBoard board,
                                                  List<Position> agents, int radius) {
        Set<Position> danger = new HashSet<>();
        for (Position agent : agents) {
            bfsFlood(board, agent, radius, danger);
        }
        return danger;
    }

    private static void bfsFlood(GameBoard board, Position source, int maxDist,
                                  Set<Position> visited) {
        Queue<Position> queue = new LinkedList<>();
        Map<Position, Integer> dist = new HashMap<>();
        queue.add(source);
        dist.put(source, 0);
        visited.add(source);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int d = dist.get(current);
            if (d >= maxDist) continue;

            for (int[] dir : DIRECTIONS) {
                Position next = new Position(current.row + dir[0], current.col + dir[1]);
                if (!board.inBounds(next) || board.isWall(next) || dist.containsKey(next))
                    continue;
                dist.put(next, d + 1);
                visited.add(next);
                queue.add(next);
            }
        }
    }

    private static Position reconstructFirstStep(Map<Position, Position> parent,
                                                   Position start, Position goal) {
        Position current = goal;
        while (parent.get(current) != null && !parent.get(current).equals(start)) {
            current = parent.get(current);
        }
        return current;
    }
}
