package program.intro_to_cs_lab_final_project;

import javafx.scene.layout.GridPane;
import java.util.*;

public class MonsterManager {

    private static final Random random = new Random();

    public static void updateMonsterAI(Map mapManager, GridPane mapGrid, Entity player, List<Monster> monsters) {
        Set<String> movedPositions = new HashSet<>();
        Set<String> currentMonsterPositions = new HashSet<>();

        for (Monster m : monsters) {
            currentMonsterPositions.add(m.getCol() + "," + m.getRow());
        }

        for (Monster monster : monsters) {
            String monsterType = monster.getType();
            String currentPosKey = monster.getCol() + "," + monster.getRow();

            if (movedPositions.contains(currentPosKey)) continue;
            if (!monster.shouldMoveThisTick()) {
                movedPositions.add(currentPosKey); // 標記為處理過，不重複動
                continue;
            }
            currentMonsterPositions.remove(currentPosKey);

            switch (monsterType) {
                case "RandomWalk"        -> executeRandomWalkAI(monster, mapManager, currentMonsterPositions);
                case "SmartRandom"       -> executeSmartRandomAI(monster, mapManager, currentMonsterPositions);
                case "Patrol"            -> executePatrolAI(monster, mapManager, currentMonsterPositions);
                case "Chaser"            -> executeChaserAI(monster, player, mapManager, currentMonsterPositions);
                case "Ghost"             -> executeGhostAI(monster, player, mapManager, currentMonsterPositions);
                case "LineOfSightChaser" -> executeLineOfSightChaserAI(monster, player, mapManager, currentMonsterPositions);
                case "PathfindingChaser" -> executePathfindingChaserAI(monster, player, mapManager, currentMonsterPositions);
                case "AStarChaser"       -> executeAStarChaserAI(monster, player, mapManager, currentMonsterPositions);
                default -> {}
            }

            String newPosKey = monster.getCol() + "," + monster.getRow();
            movedPositions.add(newPosKey);
            currentMonsterPositions.add(newPosKey);
        }

        // ================= 玩家Game Over 串接 ====================
        for (Monster monster : monsters) {
            if (monster.getCol() == player.getCol() && monster.getRow() == player.getRow()) {
                // 主角掛了
                // 呼叫 Game Over相關函式
            }
        }
        // ============================================================

        javafx.application.Platform.runLater(() -> {
            mapManager.render(mapGrid);
            player.addToMap(mapGrid);
            for (Monster monster : monsters) {
                monster.addToMap(mapGrid);
                if (monster.imageView != null) monster.imageView.toFront();
            }
        });
    }

    // 純隨機游走雜魚
    private static void executeRandomWalkAI(Monster monster, Map mapManager, Set<String> monsterPositions) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int[] d = dirs[random.nextInt(4)];
        int nc = monster.getCol() + d[0]; int nr = monster.getRow() + d[1];

        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
            if (mapManager.getTileType(nc, nr) == 0 && mapManager.getItemType(nc, nr) == 0 && !monsterPositions.contains(nc + "," + nr)) {
                monster.setCol(nc); monster.setRow(nr);
            }
        }
    }

    // 具前進慣性之隨機游走雜魚
    private static void executeSmartRandomAI(Monster monster, Map mapManager, Set<String> monsterPositions) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int lastDc = monster.getDeltaCol();
        int lastDr = monster.getDeltaRow();

        int nc = monster.getCol() + lastDc;
        int nr = monster.getRow() + lastDr;

        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 &&
                mapManager.getTileType(nc, nr) == 0 && mapManager.getItemType(nc, nr) == 0 &&
                !monsterPositions.contains(nc + "," + nr) && random.nextDouble() < 0.75) {
            monster.setCol(nc); monster.setRow(nr);
            return;
        }

        List<int[]> validDirs = new ArrayList<>();
        for (int[] d : dirs) {
            int testC = monster.getCol() + d[0]; int testR = monster.getRow() + d[1];
            if (testC >= 0 && testC < 16 && testR >= 0 && testR < 12) {
                if (mapManager.getTileType(testC, testR) == 0 && mapManager.getItemType(testC, testR) == 0 && !monsterPositions.contains(testC + "," + testR)) {
                    validDirs.add(d);
                }
            }
        }

        if (!validDirs.isEmpty()) {
            int[] chosenDir = validDirs.get(random.nextInt(validDirs.size()));
            monster.setDeltaCol(chosenDir[0]); monster.setDeltaRow(chosenDir[1]);
            monster.setCol(monster.getCol() + chosenDir[0]); monster.setRow(monster.getRow() + chosenDir[1]);
        }
    }

    // 巡邏雜魚
    private static void executePatrolAI(Monster monster, Map mapManager, Set<String> monsterPositions) {
        int dc = monster.getDeltaCol(); int dr = monster.getDeltaRow();
        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;

        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
            if (mapManager.getTileType(nc, nr) == 0 && mapManager.getItemType(nc, nr) == 0 && !monsterPositions.contains(nc + "," + nr)) {
                monster.setCol(nc); monster.setRow(nr); return;
            }
        }
        monster.setDeltaCol(-dc); monster.setDeltaRow(-dr);
    }

    // 曼哈頓距離 + Greedy 初階版 (Beast)
    private static void executeChaserAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        int dx = player.getCol() - monster.getCol(); int dy = player.getRow() - monster.getRow();
        int dc = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? 1 : -1) : 0;
        int dr = (Math.abs(dx) > Math.abs(dy)) ? 0 : (dy > 0 ? 1 : -1);

        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;
        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 && !monsterPositions.contains(nc + "," + nr)) {
            int targetTile = mapManager.getTileType(nc, nr);

            // 拆技能方塊
            if (targetTile > 1) {
                mapManager.setTileType(nc, nr, 0);
                return;
            }

            // 只有前方是平地(0)時才能走過去，且不消除場上食物
            if (targetTile == 0) {
                monster.setCol(nc); monster.setRow(nr);
            }
        }
    }

    // 曼哈頓距離 + Greedy 無視地形版 (Spirit)
    private static void executeGhostAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        int dx = player.getCol() - monster.getCol(); int dy = player.getRow() - monster.getRow();
        int dc = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? 1 : -1) : 0;
        int dr = (Math.abs(dx) > Math.abs(dy)) ? 0 : (dy > 0 ? 1 : -1);

        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;
        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 && !monsterPositions.contains(nc + "," + nr)) {
            monster.setCol(nc); monster.setRow(nr);
        }
    }

    // 十字視線 & 近距離觸發追擊 (沒觸發時維持 SmartRandom)
    private static void executeLineOfSightChaserAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        int mCol = monster.getCol(); int mRow = monster.getRow();
        int pCol = player.getCol(); int pRow = player.getRow();

        int dist = Math.abs(mCol - pCol) + Math.abs(mRow - pRow);
        boolean hasAggro = false;

        if (dist <= 4) {
            hasAggro = true;
        } else if (mCol == pCol || mRow == pRow) {
            hasAggro = true;
            if (mCol == pCol) {
                int start = Math.min(mRow, pRow) + 1; int end = Math.max(mRow, pRow);
                for (int r = start; r < end; r++) {
                    if (mapManager.getTileType(mCol, r) != 0) { hasAggro = false; break; }
                }
            } else {
                int start = Math.min(mCol, pCol) + 1; int end = Math.max(mCol, pCol);
                for (int c = start; c < end; c++) {
                    if (mapManager.getTileType(c, mRow) != 0) { hasAggro = false; break; }
                }
            }
        }

        if (hasAggro) {
            int dx = pCol - mCol; int dy = pRow - mRow;
            int dc = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? 1 : -1) : 0;
            int dr = (Math.abs(dx) > Math.abs(dy)) ? 0 : (dy > 0 ? 1 : -1);
            int nc = mCol + dc; int nr = mRow + dr;
            if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 && mapManager.getTileType(nc, nr) == 0 && !monsterPositions.contains(nc + "," + nr)) {
                monster.setCol(nc); monster.setRow(nr);
                return;
            }
        }
        executeSmartRandomAI(monster, mapManager, monsterPositions);
    }

    // BFS 全地圖繞開掩體尋路 (Eye)
    private static void executePathfindingChaserAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        int startC = monster.getCol(); int startR = monster.getRow();
        int targetC = player.getCol(); int targetR = player.getRow();

        if (startC == targetC && startR == targetR) return;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[12][16];
        java.util.Map<String, int[]> parentMap = new HashMap<>();

        queue.add(new int[]{startC, startR});
        visited[startR][startC] = true;

        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        boolean found = false;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            if (curr[0] == targetC && curr[1] == targetR) { found = true; break; }

            for (int[] d : dirs) {
                int nc = curr[0] + d[0];
                int nr = curr[1] + d[1];
                if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                    // 只要基本地形不是邊界牆，BFS 都當作可通行
                    if (!visited[nr][nc] && mapManager.getTileType(nc, nr) != 1) {
                        visited[nr][nc] = true;
                        parentMap.put(nc + "," + nr, new int[]{curr[0], curr[1]});
                        queue.add(new int[]{nc, nr});
                    }
                }
            }
        }

        if (found) {
            int[] step = new int[]{targetC, targetR};
            int[] parent = parentMap.get(step[0] + "," + step[1]);
            while (parent != null && (parent[0] != startC || parent[1] != startR)) {
                step = parent;
                parent = parentMap.get(step[0] + "," + step[1]);
            }
            if (!monsterPositions.contains(step[0] + "," + step[1])) {
                int targetTile = mapManager.getTileType(step[0], step[1]);

                // 拆技能方塊
                if (targetTile > 1) {
                    mapManager.setTileType(step[0], step[1], 0); // 在 gameMap 上抹消障礙物
                    return;
                }

                monster.setCol(step[0]); monster.setRow(step[1]);
                return;
            }
        }
        executeSmartRandomAI(monster, mapManager, monsterPositions);
    }

    // A* 啟發式尋路演算法 (羆哥大王 Bear)
    private static void executeAStarChaserAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        int startC = monster.getCol(); int startR = monster.getRow();
        int targetC = player.getCol(); int targetR = player.getRow();

        if (startC == targetC && startR == targetR) return;

        class AStarNode implements Comparable<AStarNode> {
            int c, r, g, f;
            AStarNode(int c, int r, int g, int tc, int tr) {
                this.c = c; this.r = r; this.g = g;
                this.f = g + (Math.abs(c - tc) + Math.abs(r - tr));
            }
            @Override
            public int compareTo(AStarNode o) { return Integer.compare(this.f, o.f); }
        }

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[12][16];
        java.util.Map<String, int[]> parentMap = new HashMap<>();

        openSet.add(new AStarNode(startC, startR, 0, targetC, targetR));
        boolean found = false;
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        while (!openSet.isEmpty()) {
            AStarNode curr = openSet.poll();
            if (closedSet[curr.r][curr.c]) continue;
            closedSet[curr.r][curr.c] = true;

            if (curr.c == targetC && curr.r == targetR) { found = true; break; }

            for (int[] d : dirs) {
                int nc = curr.c + d[0]; int nr = curr.r + d[1];
                if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                    // 只要不是邊界牆，當成無阻礙路徑算
                    if (!closedSet[nr][nc] && mapManager.getTileType(nc, nr) != 1) {
                        String posKey = nc + "," + nr;
                        if (!parentMap.containsKey(posKey)) {
                            parentMap.put(posKey, new int[]{curr.c, curr.r});
                            openSet.add(new AStarNode(nc, nr, curr.g + 1, targetC, targetR));
                        }
                    }
                }
            }
        }

        if (found) {
            int[] step = new int[]{targetC, targetR};
            int[] parent = parentMap.get(step[0] + "," + step[1]);
            while (parent != null && (parent[0] != startC || parent[1] != startR)) {
                step = parent;
                parent = parentMap.get(step[0] + "," + step[1]);
            }
            if (!monsterPositions.contains(step[0] + "," + step[1])) {
                int targetTile = mapManager.getTileType(step[0], step[1]);

                // 拆技能方塊
                if (targetTile > 1) {
                    mapManager.setTileType(step[0], step[1], 0);
                    return;
                }

                monster.setCol(step[0]); monster.setRow(step[1]);
                return;
            }
        }
        executeSmartRandomAI(monster, mapManager, monsterPositions);
    }
}