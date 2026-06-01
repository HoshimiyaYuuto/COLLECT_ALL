package program.intro_to_cs_lab_final_project;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.util.*;

public class ItemManager {

    private static final String[] FOOD_NAMES = {
            "", // 0 留空
            // 1~6: 一般食物 (Type 1)
            "Onigiri", "Sushi", "Sushi2", "Noodle", "FortuneCookie", "TeaLeaf",
            // 7~12: 會跳動甚至移動的食物 (Type 2)
            "Octopus", "Calamari", "Fish", "Shrimp", "Yakitori", "Meat",
            // 13~18: 會縮放的食物 (Type 3)
            "Honey", "Beaf", "Nut", "Nut2", "SeedLarge", "SeedLargeWhite",
            // 19~24: 會發光的食物 (Type 4)
            "Seed1", "Seed2", "Seed3", "SeedBig1", "SeedBig2", "SeedBig3"
    };

    private static final Random random = new Random();
    private static int foodTickCount = 0; // 食物計數器

    // 食物的 Type getter
    public static int getItemTypeByID(int itemId) {
        if (itemId >= 1 && itemId <= 6) return 1;
        if (itemId >= 7 && itemId <= 12) return 2;
        if (itemId >= 13 && itemId <= 18) return 3;
        if (itemId >= 19 && itemId <= 24) return 4;
        return 0; // 沒這東西或空地
    }

    // 會移動的食物速度調配面板
    private static int getFoodSpeedInterval(int itemId) {
        return switch (itemId) {
            case 7, 8   -> 1; // 章魚, 魷魚
            case 9, 10  -> 2; // 魚, 蝦子
            default     -> 0;
        };
    }

    public static void updateActiveFoodMove(Map mapManager, GridPane mapGrid, Entity player, List<Monster> monsters) {
        foodTickCount++;
        boolean mutated = false;

        // 記錄已經移動過的新座標
        List<String> movedPositions = new ArrayList<>();

        // 開始掃描全地圖找食物
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                int currentFood = mapManager.getItemType(c, r);

                // 篩出 Type 2 的食物
                if (getItemTypeByID(currentFood) == 2) {

                    // 檢查食物當下的地圖方塊
                    int currentTile = mapManager.getTileType(c, r);
                    if (currentTile != 0) continue;

                    // 防重複移動防線
                    if (movedPositions.contains(c + "," + r)) {
                        continue;
                    }

                    int moveIntervalSeconds = getFoodSpeedInterval(currentFood);

                    // 如果輪到牠動了
                    if (moveIntervalSeconds > 0 && (foodTickCount % moveIntervalSeconds == 0)) {

                        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
                        List<int[]> validMoves = new ArrayList<>();

                        for (int[] d : dirs) {
                            int nc = c + d[0];
                            int nr = r + d[1];

                            if (nc >= 0 && nc < 16 && nr >= 0 && nr < 12) {
                                boolean isTileEmpty = (mapManager.getTileType(nc, nr) == 0);
                                boolean isItemEmpty = (mapManager.getItemType(nc, nr) == 0);
                                boolean isPlayerThere = (nc == player.getCol() && nr == player.getRow());

                                // 掃描有沒有撞到任何一隻怪
                                boolean isMonsterThere = false;
                                for (Monster m : monsters) {
                                    if (nc == m.getCol() && nr == m.getRow()) {
                                        isMonsterThere = true;
                                        break;
                                    }
                                }

                                // 只有在完全空置、且沒有玩家、沒有任何怪物的地方，活體食物才敢跳過去
                                if (isTileEmpty && isItemEmpty && !isPlayerThere && !isMonsterThere) {
                                    validMoves.add(new int[]{nc, nr});
                                }
                            }
                        }

                        // 找到空地，立刻跳過去
                        if (!validMoves.isEmpty()) {
                            int[] nextPos = validMoves.get(random.nextInt(validMoves.size()));
                            int nextC = nextPos[0];
                            int nextR = nextPos[1];

                            // 修改陣列數據
                            mapManager.setItemType(c, r, 0);
                            mapManager.setItemType(nextC, nextR, currentFood);

                            movedPositions.add(nextC + "," + nextR);
                            mutated = true;
                        }
                    }
                }
            }
        }

        // 立刻重繪畫面
        if (mutated) {
            javafx.application.Platform.runLater(() -> {
                mapManager.render(mapGrid);
                player.addToMap(mapGrid);

                for (Monster m : monsters) {
                    m.addToMap(mapGrid);
                    if (m.imageView != null) m.imageView.toFront();
                }

                if (player.imageView != null) player.imageView.toFront();
            });
        }
    }

    // 傳入食物編號，回傳對應特殊動畫的 ImageView
    public static ImageView createItemView(int itemId, double tileSize) {
        if (itemId < 1 || itemId >= FOOD_NAMES.length) return null;

        String itemName = FOOD_NAMES[itemId];
        String foodPath = "/program/intro_to_cs_lab_final_project/Food/" + itemName + ".png";

        var stream = ItemManager.class.getResourceAsStream(foodPath);
        if (stream == null) return null;

        ImageView itemView = new ImageView(new Image(stream));

        itemView.setFitWidth(tileSize + 0.8);
        itemView.setFitHeight(tileSize + 0.8);
        itemView.setSmooth(false);

        int type = getItemTypeByID(itemId);
        switch (type) {
            case 2 -> {
                TranslateTransition bounce = new TranslateTransition(Duration.millis(350), itemView);
                bounce.setByY(-8);
                bounce.setAutoReverse(true);
                bounce.setCycleCount(Animation.INDEFINITE);
                bounce.play();
            }
            case 3 -> {
                ScaleTransition breathe = new ScaleTransition(Duration.millis(500), itemView);
                breathe.setFromX(1.0); breathe.setFromY(1.0);
                breathe.setToX(0.85);  breathe.setToY(0.85);
                breathe.setAutoReverse(true);
                breathe.setCycleCount(Animation.INDEFINITE);
                breathe.play();
            }
            case 4 -> {
                Glow glow = new Glow(0.1);
                itemView.setEffect(glow);

                javafx.animation.Timeline flash = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(Duration.ZERO, event -> glow.setLevel(0.1)),
                        new javafx.animation.KeyFrame(Duration.millis(400), event -> glow.setLevel(0.7))
                );
                flash.setAutoReverse(true);
                flash.setCycleCount(Animation.INDEFINITE);
                flash.play();
            }
            default -> {}
        }

        return itemView;
    }
}