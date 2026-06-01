package program.intro_to_cs_lab_final_project;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class Monster extends Entity {

    private String type;
    private int deltaCol = 1; // 狸貓巡邏用
    private int deltaRow = 0;

    private int monsterFrame = 0;

    private int speed = 1;         // 速度限制：1最快，2中等，3慢
    private int speedCounter = 0;  // 速度計數器

    // 原圖移動col表示方向(由index小到大) true: 下上左右  false: 下左上右
    private boolean animVersion = true;

    public Monster(String imagePath, int startCol, int startRow, String type) {
        super(imagePath, startCol, startRow);
        this.type = type;

        if (this.imageView != null && this.imageView.getImage() != null) {
            double singleWidth = this.imageView.getImage().getWidth() / 4.0;
            double singleHeight = this.imageView.getImage().getHeight() / 4.0;
            this.imageView.setViewport(new Rectangle2D(0, 0, singleWidth, singleHeight));
        }
    }

    public String getType() { return type; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setAnimVersion(boolean version) { this.animVersion = version; }

    public boolean shouldMoveThisTick() {
        speedCounter++;
        if (speedCounter >= speed) {
            speedCounter = 0;
            return true;
        }
        return false;
    }

    public int getDeltaCol() { return deltaCol; }
    public void setDeltaCol(int deltaCol) { this.deltaCol = deltaCol; }
    public int getDeltaRow() { return deltaRow; }
    public void setDeltaRow(int deltaRow) { this.deltaRow = deltaRow; }

    @Override
    public void setCol(int newCol) {
        int diff = newCol - this.col;
        super.setCol(newCol);
        if (diff != 0) {
            updateMonsterAnimation(diff, 0);
        }
    }

    @Override
    public void setRow(int newRow) {
        int diff = newRow - this.row;
        super.setRow(newRow);
        if (diff != 0) {
            updateMonsterAnimation(0, diff);
        }
    }

    // 控制怪的移動動畫
    private void updateMonsterAnimation(int dc, int dr) {
        if (this.imageView == null || this.imageView.getImage() == null) return;

        double singleWidth = this.imageView.getImage().getWidth() / 4.0;
        double singleHeight = this.imageView.getImage().getHeight() / 4.0;

        int columnIndex = 0; // 用來記錄最終要切哪一欄

        // dr < 0 : 上   dr > 0 : 下   dc < 0 : 左   dc > 0 : 右
        if (this.animVersion == true) {
            // 下上左右
            if (dr > 0)      columnIndex = 0;
            else if (dr < 0) columnIndex = 1;
            else if (dc < 0) columnIndex = 2;
            else if (dc > 0) columnIndex = 3;
        } else {
            // 下左上右 (for 羆哥)
            if (dr > 0)      columnIndex = 0;
            else if (dc < 0) columnIndex = 1;
            else if (dr < 0) columnIndex = 2;
            else if (dc > 0) columnIndex = 3;
        }

        this.imageView.setViewport(new Rectangle2D(columnIndex * singleWidth, monsterFrame * singleHeight, singleWidth, singleHeight));

        // 踏步動畫計數器向下滾動
        monsterFrame = (monsterFrame + 1) % 4;
    }
}