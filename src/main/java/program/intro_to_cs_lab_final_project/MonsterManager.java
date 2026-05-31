package program.intro_to_cs_lab_final_project;

import javafx.scene.layout.GridPane;
import java.util.*;

public class MonsterManager {

    private static final Random random = new Random();

    public static void updateMonsterAI(Map mapManager, GridPane mapGrid, Entity player, List<Monster> monsters) {
        Set<String> movedPositions = new HashSet<>();
        Set<String> currentMonsterPositions = new HashSet<>();

        // 收集全場怪物當前座標，防止怪物重疊
        for (Monster m : monsters) {
            currentMonsterPositions.add(m.getCol() + "," + m.getRow());
        }

        for (Monster monster : monsters) {
            String monsterType = monster.getType();
            String currentPosKey = monster.getCol() + "," + monster.getRow();

            if (movedPositions.contains(currentPosKey)) continue;
            currentMonsterPositions.remove(currentPosKey);

            switch (monsterType) {
                case "Mushroom" -> executeMushroomAI(monster, mapManager, currentMonsterPositions);
                case "Racoon"   -> executeRacoonAI(monster, mapManager, currentMonsterPositions);
                case "Beast"    -> executeBeastAI(monster, player, mapManager, currentMonsterPositions);
                case "Spirit"   -> executeSpiritAI(monster, player, mapManager, currentMonsterPositions);
                default -> {}
            }

            String newPosKey = monster.getCol() + "," + monster.getRow();
            movedPositions.add(newPosKey);
            currentMonsterPositions.add(newPosKey);
        }

        javafx.application.Platform.runLater(() -> {
            mapManager.render(mapGrid);
            player.addToMap(mapGrid);

            for (Monster monster : monsters) {
                monster.addToMap(mapGrid);
                if (monster.imageView != null) monster.imageView.toFront();
            }
        });
    }

    // 隨機菇AI
    private static void executeMushroomAI(Monster monster, Map mapManager, Set<String> monsterPositions) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int[] d = dirs[random.nextInt(4)];
        int nc = monster.getCol() + d[0];
        int nr = monster.getRow() + d[1];

        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
            if (mapManager.getTileType(nc, nr) == 0 && mapManager.getItemType(nc, nr) == 0 && !monsterPositions.contains(nc + "," + nr)) {
                monster.setCol(nc); monster.setRow(nr);
            }
        }
    }

    // 狸貓巡邏AI
    private static void executeRacoonAI(Monster monster, Map mapManager, Set<String> monsterPositions) {
        int dc = monster.getDeltaCol();
        int dr = monster.getDeltaRow();
        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;

        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
            if (mapManager.getTileType(nc, nr) == 0 && mapManager.getItemType(nc, nr) == 0 && !monsterPositions.contains(nc + "," + nr)) {
                monster.setCol(nc); monster.setRow(nr); return;
            }
        }
        monster.setDeltaCol(-dc); monster.setDeltaRow(-dr);
    }

    // 瘋狗熊AI
    private static void executeBeastAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        // 直接讓 target 等於玩家
        Entity target = player;

        int dx = target.getCol() - monster.getCol(); int dy = target.getRow() - monster.getRow();
        int dc = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? 1 : -1) : 0;
        int dr = (Math.abs(dx) > Math.abs(dy)) ? 0 : (dy > 0 ? 1 : -1);

        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;
        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 && !monsterPositions.contains(nc + "," + nr)) {
            int targetTile = mapManager.getTileType(nc, nr);
            if (targetTile == 0) {
                monster.setCol(nc); monster.setRow(nr);
            } else if (targetTile > 1) {
                // 拍碎玩家蓋的技能方塊（大於 1 的方塊）
                mapManager.setTileType(nc, nr, 0);
            }
        }
    }

    // 幽靈AI
    private static void executeSpiritAI(Monster monster, Entity player, Map mapManager, Set<String> monsterPositions) {
        // 直接讓 target 等於玩家
        Entity target = player;

        int dx = target.getCol() - monster.getCol(); int dy = target.getRow() - monster.getRow();
        int dc = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? 1 : -1) : 0;
        int dr = (Math.abs(dx) > Math.abs(dy)) ? 0 : (dy > 0 ? 1 : -1);

        int nc = monster.getCol() + dc; int nr = monster.getRow() + dr;
        if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12 && !monsterPositions.contains(nc + "," + nr)) {
            monster.setCol(nc); monster.setRow(nr); // 無視地形，直接穿過去
        }
    }
}