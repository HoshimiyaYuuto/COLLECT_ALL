package program.intro_to_cs_lab_final_project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Entity {
    protected int col, row;
    protected ImageView imageView;
    protected int tileSize = 32;

    private boolean isMoving = false;
    private int currentFrame = 0;
    private int lastTextureX = 0;

    public Entity(String imagePath, int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;

        var inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            System.err.println("❌ 實體載入失敗！找不到圖片: " + imagePath);
            inputStream = getClass().getResourceAsStream("/program/intro_to_cs_lab_final_project/Tilesets/TilesetFloor.png");
        }

        Image sprite = new Image(inputStream);
        this.imageView = new ImageView(sprite);

        // 預設面向
        this.imageView.setViewport(new Rectangle2D(0, 0, 16, 16));

        this.imageView.setFitWidth(tileSize);
        this.imageView.setFitHeight(tileSize);
        this.imageView.setSmooth(false);
    }

    public void addToMap(GridPane mapGrid) {
        mapGrid.add(this.imageView, this.col, this.row);
    }

    public void moveSmoothly(int deltaCol, int deltaRow, Map mapManager, GridPane mapGrid, Runnable onFinishedCallback) {
        if (isMoving) return;

        int targetCol = this.col + deltaCol;
        int targetRow = this.row + deltaRow;

        // 以移動方向找尋 SpriteSheet.png 裁切位置
        int textureX = getTextureXByDirection(deltaCol, deltaRow);
        lastTextureX = textureX;

        // 原地轉向
        this.imageView.setViewport(new Rectangle2D(textureX, 0, 16, 16));

        // 碰撞檢查
        if (!mapManager.isWalkable(targetCol, targetRow)) {
            if (onFinishedCallback != null) onFinishedCallback.run();
            return;
        }

        isMoving = true;

        // 輪播角色移動圖像
        Timeline walkAnimation = new Timeline(new KeyFrame(Duration.millis(40), event -> {
            currentFrame = (currentFrame + 1) % 4; // 4 幀輪播
            int textureY = currentFrame * 16;
            this.imageView.setViewport(new Rectangle2D(textureX, textureY, 16, 16));
        }));
        walkAnimation.setCycleCount(Timeline.INDEFINITE);
        walkAnimation.play();

        // 平滑像素位移(移動速度調整區塊)
        TranslateTransition transition = new TranslateTransition(Duration.millis(150), this.imageView);

        double moveDistanceX = deltaCol * tileSize;
        double moveDistanceY = deltaRow * tileSize;

        transition.setByX(moveDistanceX);
        transition.setByY(moveDistanceY);

        // 動畫結束
        transition.setOnFinished(event -> {
            walkAnimation.stop();

            // 歸位網格邏輯座標
            this.col = targetCol;
            this.row = targetRow;

            // 歸零相對位移量，真正重新移入 GridPane 網格
            this.imageView.setTranslateX(0);
            this.imageView.setTranslateY(0);
            mapGrid.getChildren().remove(this.imageView);
            mapGrid.add(this.imageView, this.col, this.row);

            // 靜止時顯示在 SpriteSheet.png 之 row0 樣貌
            this.imageView.setViewport(new Rectangle2D(lastTextureX, 0, 16, 16));

            isMoving = false;

            if (onFinishedCallback != null) {
                onFinishedCallback.run();
            }
        });

        transition.play();
    }

    // SpriteSheet.png 裁切對齊
    private int getTextureXByDirection(int deltaCol, int deltaRow) {
        if (deltaRow > 0) return 0;   // 往下：col 1 (X = 0)
        if (deltaRow < 0) return 16;  // 往上：col 2 (X = 16)
        if (deltaCol < 0) return 32;  // 往左：col 3 (X = 32)
        if (deltaCol > 0) return 48;  // 往右：col 4 (X = 48)
        return lastTextureX;
    }

    public boolean isMoving() { return isMoving; }
    public int getCol() { return col; }
    public int getRow() { return row; }

    public void updateScale(double newTileSize, GridPane mapGrid) {
        this.tileSize = (int) newTileSize;
        this.imageView.setFitWidth(newTileSize);
        this.imageView.setFitHeight(newTileSize);
        GridPane.setColumnIndex(this.imageView, this.col);
        GridPane.setRowIndex(this.imageView, this.row);
    }
}