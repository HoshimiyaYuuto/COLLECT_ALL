package program.intro_to_cs_lab_final_project;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Map {
    private double tileSize = 32.0;

    // 主地圖
    private final int[][] gameMap = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private Image floorTileset;
    private Image dungeonTileset;
    private Image flamemanFire;   // 火堆單圖
    private Image samuraiPlant;   // 草堆單圖

    // 食物地圖
    private final int[][] itemMap = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 12, 0, 0, 0, 24, 0, 0, 0, 0, 23, 0, 0, 0, 22, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 16, 0, 0, 0, 17, 0, 0, 0, 0, 18, 0, 0, 0, 19, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public Map() {
        String tilesetDir = "/program/intro_to_cs_lab_final_project/Tilesets/";
        floorTileset = new Image(getClass().getResourceAsStream(tilesetDir + "TilesetFloor.png"));
        dungeonTileset = new Image(getClass().getResourceAsStream(tilesetDir + "TilesetDungeon.png"));
        // 載入不在dungeonTileset的技能方塊
        flamemanFire = new Image(getClass().getResourceAsStream(tilesetDir + "FlamemanFire.png"));
        samuraiPlant = new Image(getClass().getResourceAsStream(tilesetDir + "SamuraiPlant.png"));
    }

    // 設定地圖單格物件高
    public void updateTileSize(double gridWidth) {
        this.tileSize = gridWidth / 16.0;
    }

    public double getTileSize() {
        return this.tileSize;
    }

    // 回傳方塊型別
    public int getTileType(int col, int row) {
        if (row < 0 || row >= gameMap.length || col < 0 || col >= gameMap[0].length) {
            return -1;
        }
        return gameMap[row][col];
    }

    // 只要格子不是 0 (沙地)，即視為不可穿透的障礙物
    public boolean isWalkable(int col, int row) {
        if (row < 0 || row >= gameMap.length || col < 0 || col >= gameMap[0].length) {
            return false;
        }
        return gameMap[row][col] == 0;
    }

    public void setTileType(int col, int row, int type) {
        if (row >= 0 && row < gameMap.length && col >= 0 && col < gameMap[0].length) {
            gameMap[row][col] = type;
        }
    }

    public void render(GridPane mapGrid) {
        mapGrid.setStyle("-fx-background-color: #fca75d;");
        mapGrid.getChildren().clear();

        for (int row = 0; row < gameMap.length; row++) {
            for (int col = 0; col < gameMap[row].length; col++) {

                // 先鋪底層橘沙地
                ImageView floorView = new ImageView(floorTileset);
                floorView.setViewport(new Rectangle2D(0, 0, 16, 16));
                styleTile(floorView);
                mapGrid.add(floorView, col, row);

                // 先畫食物
                int itemId = itemMap[row][col];
                if (itemId > 0) {
                    // 呼叫 ItemManager 生成掛好免圖動畫(跳跳/呼吸/發光)的 ImageView
                    ImageView foodView = ItemManager.createItemView(itemId, this.tileSize);
                    if (foodView != null) {
                        // 給予精準的itemID
                        foodView.setId("food_" + col + "_" + row);
                        mapGrid.add(foodView, col, row);
                    }
                }

                // 疊加地形/技能方塊元素
                int type = gameMap[row][col];
                ImageView blockView = null;

                if (type == 1) {
                    blockView = new ImageView(dungeonTileset);
                    blockView.setViewport(new Rectangle2D(0, 48, 16, 16));
                }
                else if (type == 2) {
                    blockView = new ImageView(dungeonTileset);
                    blockView.setViewport(new Rectangle2D(0, 32, 16, 16));
                }
                else if (type == 3) {
                    blockView = new ImageView(dungeonTileset);
                    blockView.setViewport(new Rectangle2D(16, 32, 16, 16));
                }
                else if (type == 4) {
                    blockView = new ImageView(flamemanFire);
                }
                else if (type == 5) {
                    blockView = new ImageView(dungeonTileset);
                    blockView.setViewport(new Rectangle2D(112, 32, 16, 16));
                }
                else if (type == 6) {
                    blockView = new ImageView(dungeonTileset);
                    blockView.setViewport(new Rectangle2D(0, 0, 16, 16));
                }
                else if (type == 7) {
                    blockView = new ImageView(samuraiPlant);
                }

                // 如果這格有方塊，將其疊加在食物上方
                if (blockView != null) {
                    styleTile(blockView);

                    // 給予精準的的blockID
                    blockView.setId("block_" + col + "_" + row);

                    // 透明化裝有item的方塊
                    if (itemId > 0) {
                        blockView.setOpacity(0.60); // 透明度60%
                    }

                    mapGrid.add(blockView, col, row);
                }
            }
        }
    }

    private void styleTile(ImageView iv) {
        iv.setFitWidth(tileSize + 0.8);
        iv.setFitHeight(tileSize + 0.8);
        iv.setSmooth(false);
    }

    // item 的 Getter 和 Setter
    public int getItemType(int col, int row) {
        if (row < 0 || row >= itemMap.length || col < 0 || col >= itemMap[0].length) {
            return -1;
        }
        return itemMap[row][col];
    }

    public void setItemType(int col, int row, int type) {
        if (row >= 0 && row < itemMap.length && col >= 0 && col < itemMap[0].length) {
            itemMap[row][col] = type;
        }
    }

    // 切換關卡用
    public void loadLevelData(int[][] newGameMap, int[][] newItemMap) {
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                this.gameMap[r][c] = newGameMap[r][c];
                this.itemMap[r][c] = newItemMap[r][c];
            }
        }
    }
}