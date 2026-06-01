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

    private int facingDeltaCol = 0;
    private int facingDeltaRow = 1; // 預設朝下

    public Entity(String imagePath, int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;

        var inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream == null) {
            System.err.println("The path:" + imagePath + "fails to load the entity");
            inputStream = getClass().getResourceAsStream("/program/intro_to_cs_lab_final_project/Tilesets/TilesetFloor.png");
        }

        Image sprite = new Image(inputStream);
        this.imageView = new ImageView(sprite);

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

        // 記住施法朝向
        this.facingDeltaCol = deltaCol;
        this.facingDeltaRow = deltaRow;

        int textureX = getTextureXByDirection(deltaCol, deltaRow);
        lastTextureX = textureX;

        this.imageView.setViewport(new Rectangle2D(textureX, 0, 16, 16));

        // 只要對接點是通的，就允許前進
        boolean isAttemptingTeleport = (targetCol < 0 || targetCol > 15 || targetRow < 0 || targetRow > 11);

        if (!isAttemptingTeleport && !mapManager.isWalkable(targetCol, targetRow)) {
            if (onFinishedCallback != null) onFinishedCallback.run();
            return;
        }

        isMoving = true;

        Timeline walkAnimation = new Timeline(new KeyFrame(Duration.millis(40), event -> {
            currentFrame = (currentFrame + 1) % 4;
            int textureY = currentFrame * 16;
            this.imageView.setViewport(new Rectangle2D(textureX, textureY, 16, 16));
        }));
        walkAnimation.setCycleCount(Timeline.INDEFINITE);
        walkAnimation.play();

        TranslateTransition transition = new TranslateTransition(Duration.millis(150), this.imageView);

        double moveDistanceX = deltaCol * tileSize;
        double moveDistanceY = deltaRow * tileSize;

        transition.setByX(moveDistanceX);
        transition.setByY(moveDistanceY);

        transition.setOnFinished(event -> {
            walkAnimation.stop();

            this.col = targetCol;
            this.row = targetRow;

            this.imageView.setTranslateX(0);
            this.imageView.setTranslateY(0);
            mapGrid.getChildren().remove(this.imageView);

            // 如果還沒觸發傳送門（還在普通地圖內），就正常塞回網格
            if (this.col >= 0 && this.col <= 15 && this.row >= 0 && this.row <= 11) {
                mapGrid.add(this.imageView, this.col, this.row);
            }

            this.imageView.setViewport(new Rectangle2D(lastTextureX, 0, 16, 16));

            isMoving = false;

            if (onFinishedCallback != null) {
                onFinishedCallback.run();
            }
        });

        transition.play();
    }

    private int getTextureXByDirection(int deltaCol, int deltaRow) {
        if (deltaRow > 0) return 0;
        if (deltaRow < 0) return 16;
        if (deltaCol < 0) return 32;
        if (deltaCol > 0) return 48;
        return lastTextureX;
    }

    public boolean isMoving() { return isMoving; }
    public int getCol() { return col; }
    public int getRow() { return row; }

    // 新增的 Setter，專門用來讓傳送門重置座標
    public void setCol(int col) { this.col = col; }
    public void setRow(int row) { this.row = row; }

    public int getFacingDeltaCol() { return facingDeltaCol; }
    public int getFacingDeltaRow() { return facingDeltaRow; }

    public void updateScale(double newTileSize, GridPane mapGrid) {
        this.tileSize = (int) newTileSize;
        this.imageView.setFitWidth(newTileSize);
        this.imageView.setFitHeight(newTileSize);
        GridPane.setColumnIndex(this.imageView, this.col);
        GridPane.setRowIndex(this.imageView, this.row);
    }

    // 按方向鍵先轉彎
    public void setFacing(int deltaCol, int deltaRow) {
        // 只有在角色沒有正在移動時，才允許原地轉向
        if (isMoving) return;

        // 更新施法與朝向的紀錄
        this.facingDeltaCol = deltaCol;
        this.facingDeltaRow = deltaRow;

        // 根據方向計算角色圖的 X 軸切片位置
        int textureX = getTextureXByDirection(deltaCol, deltaRow);
        lastTextureX = textureX;

        // 瞬間切換角色的圖片外觀（回到該方向的第 0 幀靜止畫面）
        this.imageView.setViewport(new Rectangle2D(textureX, 0, 16, 16));
    }

    // 外部直接取得目前動態計算後的單格尺寸（供 Controller 或 SkillManager 參考）
    public int getTileSize() {
        return this.tileSize;
    }

    // 顯示出招動畫
    public void setFacingBattlePose() {
        if (this.imageView == null || this.imageView.getImage() == null) return;

        // SpriteSheets 是 7 * 4
        double singleWidth = this.imageView.getImage().getWidth() / 4.0;
        double singleHeight = this.imageView.getImage().getHeight() / 7.0;

        // 固定在第五排 (Row 4)
        double y = 4 * singleHeight;
        double x = 0;

        // 根據目前面相切圖
        if (this.facingDeltaRow > 0) {          // 向下
            x = 0 * singleWidth;
        } else if (this.facingDeltaRow < 0) {   // 向上
            x = 1 * singleWidth;
        } else if (this.facingDeltaCol < 0) {   // 向左
            x = 2 * singleWidth;
        } else if (this.facingDeltaCol > 0) {   // 向右
            x = 3 * singleWidth;
        } else {
            // 防呆
            x = 0 * singleWidth;
        }

        this.imageView.setViewport(new Rectangle2D(x, y, singleWidth, singleHeight));
    }
}