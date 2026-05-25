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
            {1, 0, 1, 1, 2, 0, 3, 5, 6, 0, 4, 0, 1, 1, 0, 1},   //方塊測試
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
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

    // 只要格子不是 0 (沙地)，就通通視為不可穿透的障礙物
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

                // 疊加動態元素
                int type = gameMap[row][col];

                if (type == 1) {
                    // 1: 地圖紅磚
                    ImageView wallView = new ImageView(dungeonTileset);
                    wallView.setViewport(new Rectangle2D(0, 48, 16, 16));
                    styleTile(wallView);
                    mapGrid.add(wallView, col, row);
                }
                else if (type == 2) {
                    // 2: 光哥技能: 水牆
                    ImageView waterView = new ImageView(dungeonTileset);
                    waterView.setViewport(new Rectangle2D(0, 32, 16, 16));
                    styleTile(waterView);
                    mapGrid.add(waterView, col, row);
                }
                else if (type == 3) {
                    // 3: 冰結忍技能: 冰牆
                    ImageView iceView = new ImageView(dungeonTileset);
                    iceView.setViewport(new Rectangle2D(16, 32, 16, 16));
                    styleTile(iceView);
                    mapGrid.add(iceView, col, row);
                }
                else if (type == 4) {
                    // 4: 火燄忍技能: 火焰
                    ImageView fireView = new ImageView(flamemanFire);
                    styleTile(fireView);
                    mapGrid.add(fireView, col, row);
                }
                else if (type == 5) {
                    // 5: 法巫技能: 大靈球
                    ImageView mageView = new ImageView(dungeonTileset);
                    mageView.setViewport(new Rectangle2D(112, 32, 16, 16));
                    styleTile(mageView);
                    mapGrid.add(mageView, col, row);
                }
                else if (type == 6) {
                    // 6: 機鉨鈦鎂: 金屬鎖頭牆
                    ImageView lockView = new ImageView(dungeonTileset);
                    lockView.setViewport(new Rectangle2D(0, 0, 16, 16));
                    styleTile(lockView);
                    mapGrid.add(lockView, col, row);
                }
                else if (type == 7) {
                    // 7. 信長: 草牆
                    ImageView grassView = new ImageView(samuraiPlant);
                    styleTile(grassView);
                    mapGrid.add(grassView, col, row);
                }
            }
        }
    }

    private void styleTile(ImageView iv) {
        iv.setFitWidth(tileSize + 0.8);
        iv.setFitHeight(tileSize + 0.8);
        iv.setSmooth(false);
    }
}