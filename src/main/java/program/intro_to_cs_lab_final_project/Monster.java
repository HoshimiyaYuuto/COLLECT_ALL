package program.intro_to_cs_lab_final_project;

public class Monster extends Entity {
    private String type; // 怪物種類："Mushroom", "Racoon", "Beast", "Spirit"
    private int deltaCol = 1; // 狸貓巡邏專用的方向
    private int deltaRow = 0;

    public Monster(String imagePath, int startCol, int startRow, String type) {
        super(imagePath, startCol, startRow); // 調用父類別 Entity 的構造函數
        this.type = type;

        // 如果是狸貓，可以根據需要調整預設前進方向
        if ("Racoon".equals(type)) {
            this.deltaCol = 1;
            this.deltaRow = 0;
        }
    }

    // 怪物的 Getter / Setter
    public String getType() { return type; }
    public int getDeltaCol() { return deltaCol; }
    public int getDeltaRow() { return deltaRow; }
    public void setDeltaCol(int dc) { this.deltaCol = dc; }
    public void setDeltaRow(int dr) { this.deltaRow = dr; }
}