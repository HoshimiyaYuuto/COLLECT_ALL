package program.intro_to_cs_lab_final_project;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Map {
    public final int TILE_SIZE = 48;

    // （1=紅磚圍牆, 0=橘沙地, 3=淺藍冰塊）
    private final int[][] gameMap = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 3, 3, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 0, 1},
            {1, 3, 1, 0, 0, 0, 1, 3, 1, 1},
            {1, 0, 0, 0, 3, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private Image floorTileset;
    private Image dungeonTileset;

    public Map() {
        String tilesetDir = "/program/intro_to_cs_lab_final_project/Tilesets/";
        floorTileset = new Image(getClass().getResourceAsStream(tilesetDir + "TilesetFloor.png"));
        // 這裡直接讀取擁有經典冰塊與紅磚牆的 Dungeon 圖檔
        dungeonTileset = new Image(getClass().getResourceAsStream(tilesetDir + "TilesetDungeon.png"));
    }

    // 檢查某一格是不是能走（沒出界、不是牆(1)、不是冰塊(3)）
    public boolean isWalkable(int col, int row) {
        if (row < 0 || row >= gameMap.length || col < 0 || col >= gameMap[0].length) {
            return false;
        }
        int tileType = gameMap[row][col];
        return tileType != 1 && tileType != 3;
    }

    // 壞壞冰淇淋的核心：動態吐冰塊 (設為3) 或 破冰 (設為0)
    public void setTileType(int col, int row, int type) {
        if (row >= 0 && row < gameMap.length && col >= 0 && col < gameMap[0].length) {
            gameMap[row][col] = type;
        }
    }

    // 渲染靜態背景（沙地、圍牆、冰塊）
    public void render(GridPane mapGrid) {
        mapGrid.getChildren().clear();

        for (int row = 0; row < gameMap.length; row++) {
            for (int col = 0; col < gameMap[row].length; col++) {

                // 每一格一律先鋪橘沙地當底色
                ImageView floorView = new ImageView(floorTileset);
                floorView.setViewport(new Rectangle2D(0, 0, 16, 16));
                styleTile(floorView);
                mapGrid.add(floorView, col, row);

                if (gameMap[row][col] == 1) { // 紅磚外牆
                    ImageView wallView = new ImageView(dungeonTileset);
                    wallView.setViewport(new Rectangle2D(0, 48, 16, 16));
                    styleTile(wallView);
                    mapGrid.add(wallView, col, row);
                }
                else if (gameMap[row][col] == 3) { // 淺藍色冰塊
                    ImageView iceView = new ImageView(dungeonTileset);
                    iceView.setViewport(new Rectangle2D(0, 32, 16, 16));
                    styleTile(iceView);
                    mapGrid.add(iceView, col, row);
                }
            }
        }
    }

    private void styleTile(ImageView iv) {
        iv.setFitWidth(TILE_SIZE);
        iv.setFitHeight(TILE_SIZE);
        iv.setSmooth(false);
    }
}