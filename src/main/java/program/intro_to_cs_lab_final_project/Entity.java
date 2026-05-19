package program.intro_to_cs_lab_final_project;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Entity {
    protected int col, row;          // 網格座標
    protected ImageView imageView;  // 顯示在畫面上的節點
    protected int tileSize = 48;

    public Entity(String imagePath, int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;

        // 加上防爆盾牌
        var inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            System.err.println("❌ 實體載入失敗！找不到圖片，具體路徑為: " + imagePath);
            System.err.println("👉 請檢查：1. 圖片檔案是否存在？ 2. 大小寫有沒有完全拼對？");

            // 隨便塞一張保險圖，不要讓整個遊戲在載入時因為找不到圖而閃退
            // 這裡假設你的 TilesetFloor.png 是一定存在的
            inputStream = getClass().getResourceAsStream("/program/intro_to_cs_lab_final_project/Tilesets/TilesetFloor.png");
        }

        Image sprite = new Image(inputStream);
        this.imageView = new ImageView(sprite);

        // 預設切出第一幀正面靜止的圖 (0, 0, 16, 16)
        this.imageView.setViewport(new Rectangle2D(0, 0, 16, 16));

        this.imageView.setFitWidth(tileSize);
        this.imageView.setFitHeight(tileSize);
        this.imageView.setSmooth(false);
    }

    // 讓生物動態加入到 GridPane 的方法
    public void addToMap(GridPane mapGrid) {
        mapGrid.add(this.imageView, this.col, this.row);
    }

    // 基礎移動邏輯：如果新格子可以走，就搬過去並更新 ImageView 位置
    public void moveTo(int newCol, int newRow, Map mapManager, GridPane mapGrid) {
        if (mapManager.isWalkable(newCol, newRow)) {
            // 從原本的格子移除舊位置
            mapGrid.getChildren().remove(this.imageView);

            this.col = newCol;
            this.row = newRow;

            // 重新塞到新的格子去
            mapGrid.add(this.imageView, this.col, this.row);
        }
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
}