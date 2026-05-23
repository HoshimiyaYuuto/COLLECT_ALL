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
    public void castCreateSkill(Entity player, Entity slime, String heroType) {
        int deltaCol = player.getFacingDeltaCol();
        int deltaRow = player.getFacingDeltaRow();

        // 找出生成有效區域（包含怪獸位置阻擋判定）
        int[] areaInfo = calculateEffectiveArea(player, slime, heroType, deltaCol, deltaRow);
        int startIdx = areaInfo[0]; // 起始格（鄰近玩家第一格）
        int endIdx = areaInfo[1];   // 結束格（遇到邊界前最後一格）

        if (startIdx > endIdx) return; // 沒有有效區域，無法生成

        // 根據不同英雄執行專屬技能與對應反應
        switch (heroType) {
            case "Bald" -> executeBald(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Flameman" -> executeFlameman(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Frozenman" -> executeFrozenman(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Mage" -> executeMage(player, deltaCol, deltaRow);
            case "Robot" -> executeRobot(player, deltaCol, deltaRow, startIdx, endIdx);
            case "Samurai" -> executeSamurai(player, deltaCol, deltaRow, startIdx, endIdx);
        }

        // 刷新畫面
        mapManager.render(mapGrid);
    }

    // 釋放摧毀方塊技能
    public void castDestroySkill(Entity player, String heroType) {
        int deltaCol = player.getFacingDeltaCol();
        int deltaRow = player.getFacingDeltaRow();

        // 必須與方塊相鄰（即前方第一格）
        int targetCol = player.getCol() + deltaCol;
        int targetRow = player.getRow() + deltaRow;

        int currentTile = mapManager.getTileType(targetCol, targetRow);
        int targetHeroTile = getHeroTileType(heroType);

        // 只能摧毀屬於自己屬性的方塊（法巫除外，因為魔方塊被摧毀會引爆）
        if (currentTile == targetHeroTile) {
            if (heroType.equals("Mage")) {
                // 魔方塊被摧毀：觸發九宮格大爆炸
                executeMageExplosion(targetCol, targetRow);
            } else {
                // 普通消除，變回空地 (0)
                mapManager.setTileType(targetCol, targetRow, 0);
            }
            mapManager.render(mapGrid);
        }
    }

    // ==================== 各英雄技能具體實作區 ====================

    private void executeBald(Entity player, int dc, int dr, int start, int end) {
        boolean hasGrass = false;
        // 先檢查有效區域內有沒有草（7）
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 7) {
                hasGrass = true;
                break;
            }
        }

        // 如果有草，觸發 50% 隨機蔓延，且水方塊縮水（與草堆留一格空區域）
        if (hasGrass) {
            if (random.nextBoolean()) {
                growRandomGrassNearby(); // 50% 機率讓地圖上的隨機草堆多長一格
            }
            // 水方塊僅生成到目標方向至首個草堆的前一格區域，並再刻意留空一格
            end = Math.max(start, end - 2);
        }

        // 生成水方塊 (2)，並熄滅所有火堆 (4)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            mapManager.setTileType(c, r, 2);
        }
    }

    private void executeFlameman(Entity player, int dc, int dr, int start, int end) {
        // 檢查有效區域內是否有連續草堆（有重疊到就燒毀）
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 7) {
                burnConnectedGrass(c, r); // 連鎖燒毀相連的所有草
            }
        }

        // 再次檢查，若遇到水(2)或冰(3)，火堆(4)僅生成到其前一格(留一格空區域)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            int tile = mapManager.getTileType(c, r);
            if (tile == 2 || tile == 3) {
                end = Math.max(start, i - 2); // 縮減火堆生成範圍
                break;
            }
        }

        // 生成火堆 (4)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            // 如果原本是空地或被燒毀的區域，就蓋上火堆
            if (mapManager.getTileType(c, r) == 0 || mapManager.getTileType(c, r) == 7) {
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
                // 熄滅火堆 ➡️ 變水方塊
                mapManager.setTileType(c, r, 2);
            } else if (currentTile == 2) {
                // 有水方塊 ➡️ 凝結成冰方塊
                mapManager.setTileType(c, r, 3);
            } else if (currentTile == 0) {
                // 空區域 ➡️ 正常生成冰方塊
                mapManager.setTileType(c, r, 3);
            }
        }
    }

    private void executeMage(Entity player, int dc, int dr) {
        // 僅目標方向第一格，且必須是空區域才生成魔方塊 (5)
        int c = player.getCol() + dc;
        int r = player.getRow() + dr;
        if (mapManager.getTileType(c, r) == 0) {
            mapManager.setTileType(c, r, 5);
        }
    }

    private void executeRobot(Entity player, int dc, int dr, int start, int end) {
        // 無屬性相剋，生成有效區域填滿金屬鎖頭 (6)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 0) {
                mapManager.setTileType(c, r, 6);
            }
        }
    }

    private void executeSamurai(Entity player, int dc, int dr, int start, int end) {
        // 檢查有沒有火堆，有的話無法生成任何草
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            if (mapManager.getTileType(c, r) == 4) return;
        }

        // 吸收所有水方塊 (2)，生成連續草堆 (7)
        for (int i = start; i <= end; i++) {
            int c = player.getCol() + dc * i;
            int r = player.getRow() + dr * i;
            int tile = mapManager.getTileType(c, r);
            if (tile == 0 || tile == 2) {
                mapManager.setTileType(c, r, 7);
            }
        }
    }

    // 巫魔方塊九宮格連鎖大爆炸邏輯（可破壞外牆紅磚 1）
    private void executeMageExplosion(int centerCol, int centerRow) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int c = centerCol + dc;
                int r = centerRow + dr;

                // 邊界安全防範
                if (r >= 0 && r < 12 && c >= 0 && c < 16) {
                    mapManager.setTileType(c, r, 0); // 刪除任何方塊變回沙地
                }
            }
        }
    }

    // ==================== 幾何與碰撞演算法核心 ====================

    // 計算生成有效區域的起點與終點格數步數
    private int[] calculateEffectiveArea(Entity player, Entity slime, String heroType, int dc, int dr) {
        int start = 1;
        int end = 0;
        int pCol = player.getCol();
        int pRow = player.getRow();

        int targetHeroTile = getHeroTileType(heroType);

        // 往目標方向沿路搜查
        for (int i = 1; i < 20; i++) {
            int c = pCol + dc * i;
            int r = pRow + dr * i;

            int tile = mapManager.getTileType(c, r);
            if (tile == -1) break; // 超出地圖邊界

            // 怪獸位置也視為阻擋邊界
            if (c == slime.getCol() && r == slime.getRow()) {
                break;
            }

            // 判斷是否遇到邊界方塊（紅磚、無關或同屬性方塊）
            if (tile == 1 || (tile != 0 && tile != targetHeroTile)) {

                // 根據屬性互剋規則判斷生成有效區域
                boolean isInteractableTile = false;

                switch (heroType) {
                    case "Flameman" -> {
                        // 火燄忍可以穿透連續草堆 (7) 進行燒毀
                        if (tile == 7) isInteractableTile = true;
                    }
                    case "Bald" -> {
                        // 光哥可以穿透火堆 (4) 進行熄滅，也可以穿透草 (7) 觸發蔓延判定
                        if (tile == 4 || tile == 7) isInteractableTile = true;
                    }
                    case "Frozenman" -> {
                        // 冰結忍可以穿透火堆 (4) 進行熄滅，也可以穿透水方塊 (2) 進行凝結
                        if (tile == 4 || tile == 2) isInteractableTile = true;
                    }
                    case "Samurai" -> {
                        // 信長可以穿透水方塊 (2) 進行吸收轉化
                        if (tile == 2) isInteractableTile = true;
                    }
                }

                // 如果是可互動的特殊方塊，將其納入有效生成區域，並允許迴圈繼續穿透前進
                if (isInteractableTile) {
                    end = i;
                    continue;
                }

                break; // 撞到真正的硬邊界（如紅磚、金屬鎖頭、無效的其他方塊），停止搜查
            }

            end = i;
            // 法巫生成有效區域僅限第一格
            if (heroType.equals("Mage")) break;
        }
        return new int[]{start, end};
    }

    // 連鎖燒毀相鄰的所有草（遞迴氾濫演算法 Flood Fill）
    private void burnConnectedGrass(int c, int r) {
        if (mapManager.getTileType(c, r) != 7) return;
        mapManager.setTileType(c, r, 0); // 燒成空地
        burnConnectedGrass(c + 1, r);
        burnConnectedGrass(c - 1, r);
        burnConnectedGrass(c, r + 1);
        burnConnectedGrass(c, r - 1);
    }

    // 光哥 50% 機率讓隨機一個草堆往四周空地蔓延一格
    private void growRandomGrassNearby() {
        // 候選空地容器
        List<int[]> validEmptySpaces = new ArrayList<>();

        // 遍歷地圖，搜集所有合法的生長點
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                // 判斷空地
                if (mapManager.getTileType(c, r) == 0) {
                    // 檢查四周有沒有任何一格是草
                    if (mapManager.getTileType(c + 1, r) == 7 ||
                            mapManager.getTileType(c - 1, r) == 7 ||
                            mapManager.getTileType(c, r + 1) == 7 ||
                            mapManager.getTileType(c, r - 1) == 7) {

                        // 有就存入候選空地容器
                        validEmptySpaces.add(new int[]{c, r});
                    }
                }
            }
        }
        // 如果地圖上真的有合法的空地可以長草
        if (!validEmptySpaces.isEmpty()) {
            // 隨機挑個候選空地長草
            int randomIndex = random.nextInt(validEmptySpaces.size());
            int[] chosenSpace = validEmptySpaces.get(randomIndex);

            int targetCol = chosenSpace[0];
            int targetRow = chosenSpace[1];

            mapManager.setTileType(targetCol, targetRow, 7);
        }
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