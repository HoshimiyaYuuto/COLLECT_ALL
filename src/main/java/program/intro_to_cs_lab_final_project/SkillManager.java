package program.intro_to_cs_lab_final_project;

import javafx.scene.layout.GridPane;
import java.util.*;

public class SkillManager {
    private final Map mapManager;
    private final GridPane mapGrid;
    private final Random random = new Random();

    public SkillManager(Map mapManager, GridPane mapGrid) {
        this.mapManager = mapManager;
        this.mapGrid = mapGrid;
    }

    // 釋放生成方塊技能
    public void castCreateSkill(Entity player, List<Monster> monsters, String heroType) {
        int deltaCol = player.getFacingDeltaCol();
        int deltaRow = player.getFacingDeltaRow();

        int targetCol = player.getCol() + deltaCol;
        int targetRow = player.getRow() + deltaRow;

        boolean isMonsterThere = false;
        for (Monster m : monsters) {
            if (targetCol == m.getCol() && targetRow == m.getRow()) {
                isMonsterThere = true;
                break;
            }
        }

        int[] areaInfo = calculateEffectiveArea(player, monsters, heroType, deltaCol, deltaRow);
        int startIdx = areaInfo[0];
        int endIdx = areaInfo[1];
        boolean hasMetTarget = (areaInfo[2] == 1);

        if (startIdx > endIdx && !hasMetTarget) return;

        // 根據不同英雄執行專屬技能
        switch (heroType) {
            case "Bald" -> executeBald(player, deltaCol, deltaRow, startIdx, endIdx, hasMetTarget);
            case "Flameman" -> executeFlameman(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Frozenman" -> executeFrozenman(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Mage" -> executeMage(player, deltaCol, deltaRow);
            case "Robot" -> executeRobot(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Samurai" -> executeSamurai(player, deltaCol, deltaRow, startIdx, endIdx);
        }

        // 刷新畫面
        mapManager.render(mapGrid);
    }

    // 釋放摧毀方塊技能（此處無動到怪物，維持優雅運作）
    public void castDestroySkill(Entity player, String heroType) {
        int deltaCol = player.getFacingDeltaCol();
        int deltaRow = player.getFacingDeltaRow();
        int targetHeroTile = getHeroTileType(heroType);

        // 法巫摧毀：觸發九宮格爆裂魔法
        if (heroType.equals("Mage")) {
            int targetCol = player.getCol() + deltaCol;
            int targetRow = player.getRow() + deltaRow;
            int currentTile = mapManager.getTileType(targetCol, targetRow);

            if (currentTile == targetHeroTile) {
                executeMageExplosionChain(targetCol, targetRow); // 呼叫全新升級的連鎖爆炸演算法
                mapManager.render(mapGrid);
            }
            return;
        }

        // 其他英雄：往面向方向線性連續破壞
        boolean mutated = false;
        for (int i = 1; i < 20; i++) {
            int targetCol = player.getCol() + deltaCol * i;
            int targetRow = player.getRow() + deltaRow * i;
            int currentTile = mapManager.getTileType(targetCol, targetRow);

            if (currentTile == targetHeroTile) {
                mapManager.setTileType(targetCol, targetRow, 0);
                mutated = true;
            } else {
                break;
            }
        }

        if (mutated) {
            mapManager.render(mapGrid);
        }
    }

    // ==================== 各英雄技能具體實作區 ====================

    private void executeBald(Entity player, int dc, int dr, int start, int end, boolean hasGrass) {
        if (hasGrass && random.nextBoolean()) {
            growRandomGrassNearby(player);
        }
        // 生成水方塊 (2)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            mapManager.setTileType(c, r, 2);
        }
    }

    private void executeFlameman(Entity player, int dc, int dr, int start, int end) {
        // 連鎖燒草
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 7) {
                burnConnectedGrass(c, r);
            }
        }

        // 生成火堆 (4)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getItemType(c, r) > 0) continue;
            if (mapManager.getTileType(c, r) == 0) {
                mapManager.setTileType(c, r, 4);
            }
        }
    }

    private void executeFrozenman(Entity player, int dc, int dr, int start, int end) {
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            int currentTile = mapManager.getTileType(c, r);

            if (currentTile == 4) {
                mapManager.setTileType(c, r, 2);
            } else if (currentTile == 2 || currentTile == 0) {
                mapManager.setTileType(c, r, 3);
            }
        }
    }

    private void executeMage(Entity player, int dc, int dr) {
        int c = player.getCol() + dc;
        int r = player.getRow() + dr;
        if (mapManager.getItemType(c, r) > 0) return;
        if (mapManager.getTileType(c, r) == 0) {
            mapManager.setTileType(c, r, 5);
        }
    }

    private void executeRobot(Entity player, int dc, int dr, int start, int end) {
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 0) {
                mapManager.setTileType(c, r, 6);
            }
        }
    }

    private void executeSamurai(Entity player, int dc, int dr, int start, int end) {
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 4) return;
        }
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            int tile = mapManager.getTileType(c, r);
            if (tile == 0 || tile == 2) {
                mapManager.setTileType(c, r, 7);
            }
        }
    }

    // 法巫靈球爆裂魔法演算法 (使用 BFS Queue 防止無窮遞迴)
    private void executeMageExplosionChain(int startCol, int startRow) {
        Queue<int[]> targetsToExplode = new LinkedList<>();
        // 先把引爆起點的手把魔方塊刪除，並加入爆炸佇列
        mapManager.setTileType(startCol, startRow, 0);
        targetsToExplode.add(new int[]{startCol, startRow});

        while (!targetsToExplode.isEmpty()) {
            int[] center = targetsToExplode.poll();
            int centerCol = center[0];
            int centerRow = center[1];

            // 檢查該爆炸點的九宮格範圍
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int c = centerCol + dc;
                    int r = centerRow + dr;

                    // 地圖邊界安全防護
                    if (c >= 0 && c < 16 && r >= 0 && r < 12) {
                        int tile = mapManager.getTileType(c, r);
                        // 法巫靈球爆裂
                        if (tile == 5) {
                            mapManager.setTileType(c, r, 0);
                            targetsToExplode.add(new int[]{c, r});
                        } else {
                            // 普通方塊（空地、紅磚 1、草、火、冰等）直接被衝擊波炸成沙地
                            mapManager.setTileType(c, r, 0);
                        }
                    }
                }
            }
        }
    }

    // ==================== 幾何與碰撞演算法核心 ====================

    private int[] calculateEffectiveArea(Entity player, List<Monster> monsters, String heroType, int dc, int dr) {
        int start = 1;
        int end = 0;
        int pCol = player.getCol();
        int pRow = player.getRow();
        int hasMetTargetTile = 0;

        int targetHeroTile = getHeroTileType(heroType);

        for (int i = 1; i < 20; i++) {
            int c = pCol + dc * i;
            int r = pRow + dr * i;

            int tile = mapManager.getTileType(c, r);
            if (tile == -1) break; // 撞到邊界退出

            // 檢查有沒有撞到任何一隻怪物
            boolean hitMonster = false;
            for (Monster m : monsters) {
                if (c == m.getCol() && r == m.getRow()) {
                    hitMonster = true;
                    break;
                }
            }
            if (hitMonster) break;

            if (tile == targetHeroTile) break;

            // 火忍遇到水(2)或冰(3)，射程當場「砍斷到前一格的前一格」並強制結束
            if (heroType.equals("Flameman") && (tile == 2 || tile == 3)) {
                if (tile == 3) mapManager.setTileType(c, r, 2);
                end = i - 2;
                hasMetTargetTile = 1;
                break;
            }

            // 光哥遇到草(7)，射程當場「砍斷到前一格的前一格」並強制結束
            if (heroType.equals("Bald") && tile == 7) {
                end = i - 2;
                hasMetTargetTile = 1;
                break;
            }

            // 普通阻擋判定
            if (tile == 1 || tile != 0) {
                boolean isInteractableTile = false;
                switch (heroType) {
                    case "Flameman" -> { if (tile == 7) isInteractableTile = true; }
                    case "Bald" -> { if (tile == 4 || tile == 7) isInteractableTile = true; }
                    case "Frozenman" -> { if (tile == 4 || tile == 2) isInteractableTile = true; }
                    case "Samurai" -> { if (tile == 2) isInteractableTile = true; }
                }

                if (isInteractableTile) {
                    end = i;
                    continue;
                }
                break;
            }

            end = i;
            if (heroType.equals("Mage")) break;
        }

        // 如果砍過頭導致 end 比 start 還小，就校正回沒有生成區
        if (end < start) end = start - 1;
        return new int[]{start, end, hasMetTargetTile};
    }

    private void burnConnectedGrass(int c, int r) {
        if (mapManager.getTileType(c, r) != 7) return;
        mapManager.setTileType(c, r, 0);
        burnConnectedGrass(c + 1, r);
        burnConnectedGrass(c - 1, r);
        burnConnectedGrass(c, r + 1);
        burnConnectedGrass(c, r - 1);
    }

    private void growRandomGrassNearby(Entity player) {
        List<int[]> validEmptySpaces = new ArrayList<>();
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                boolean isPlayerStandingHere = (c == player.getCol() && r == player.getRow());
                if (mapManager.getTileType(c, r) == 0 && !isPlayerStandingHere) {
                    if (mapManager.getTileType(c + 1, r) == 7 ||
                            mapManager.getTileType(c - 1, r) == 7 ||
                            mapManager.getTileType(c, r + 1) == 7 ||
                            mapManager.getTileType(c, r - 1) == 7) {
                        validEmptySpaces.add(new int[]{c, r});
                    }
                }
            }
        }
        if (!validEmptySpaces.isEmpty()) {
            int randomIndex = random.nextInt(validEmptySpaces.size());
            int[] chosenSpace = validEmptySpaces.get(randomIndex);
            mapManager.setTileType(chosenSpace[0], chosenSpace[1], 7);
        }
    }

    // 環境動態更新(化學反應)
    public void updateEnvironmentTick(Entity player, List<Monster> monsters) {
        boolean mutated = false;

        int[][] tempMap = new int[12][16];
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                tempMap[r][c] = mapManager.getTileType(c, r);
            }
        }

        // 開始掃描全地圖
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                int currentTile = mapManager.getTileType(c, r);

                // 草堆(7)吸水
                if (currentTile == 7) {
                    if (absorbWaterNearby(c, r, tempMap)) mutated = true;
                }
                // 水(2)或冰(3)熄火
                if (currentTile == 2 || currentTile == 3) {
                    if (extinguishFireNearby(c, r, currentTile, tempMap)) mutated = true;
                }
                // 火堆(4)燒草
                if (currentTile == 4) {
                    if (burnGrassNearby(c, r, tempMap)) mutated = true;
                }
            }
        }

        // 如果地圖發生連鎖化學反應
        if (mutated) {
            for (int r = 0; r < 12; r++) {
                for (int c = 0; c < 16; c++) {
                    mapManager.setTileType(c, r, tempMap[r][c]);
                }
            }

            // 使用 Platform.runLater 來同步畫面
            javafx.application.Platform.runLater(() -> {
                mapManager.render(mapGrid); // 重畫地圖

                // 地圖瓷磚全部重鋪完，立刻把玩家和怪物加回地圖
                if (!mapGrid.getChildren().contains(player.imageView)) player.addToMap(mapGrid);
                if (player.imageView != null) player.imageView.toFront();

                for (Monster m : monsters) {
                    if (!mapGrid.getChildren().contains(m.imageView)) m.addToMap(mapGrid);
                    if (m.imageView != null) m.imageView.toFront();
                }
            });
        }
    }

    // 草堆吸水
    private boolean absorbWaterNearby(int c, int r, int[][] tempMap) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        boolean transformed = false;
        for (int[] d : dirs) {
            int nc = c + d[0]; int nr = r + d[1];
            if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                if (mapManager.getTileType(nc, nr) == 2) {
                    tempMap[nr][nc] = 7; // 水變草
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    private boolean extinguishFireNearby(int c, int r, int myTile, int[][] tempMap) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        boolean transformed = false;
        for (int[] d : dirs) {
            int nc = c + d[0]; int nr = r + d[1];
            if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                if (mapManager.getTileType(nc, nr) == 4) {
                    tempMap[nr][nc] = 0; // 火堆熄滅變空地
                    // 如果原本是冰(3)去碰到火，冰才會融化退化成水(2)
                    if (myTile == 3) tempMap[r][c] = 2;
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    // 火燒草
    private boolean burnGrassNearby(int c, int r, int[][] tempMap) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        boolean transformed = false;
        for (int[] d : dirs) {
            int nc = c + d[0]; int nr = r + d[1];
            if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                // 如果火堆旁邊貼著草堆(7)
                if (mapManager.getTileType(nc, nr) == 7) {
                    // 草堆變成火堆
                    tempMap[nr][nc] = 4;
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    public int getHeroTileType(String heroType) {
        return switch (heroType) {
            case "Bald" -> 2;
            case "Frozenman" -> 3;
            case "Flameman" -> 4;
            case "Mage" -> 5;
            case "Robot" -> 6;
            case "Samurai" -> 7;
            default -> 1;
        };
    }
}