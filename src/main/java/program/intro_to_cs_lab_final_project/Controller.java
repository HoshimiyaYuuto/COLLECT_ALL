package program.intro_to_cs_lab_final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private Label scoreLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private ImageView clockImage;
    @FXML
    private ImageView selectImage;
    @FXML
    private GridPane mapGrid;
    private static String heroImageFile;

    // 宣告專屬的經理物件
    private Map mapManager;
    private Entity player;
    private Entity slime;

    // 處理選單FXML檔
    @FXML
    private void handleCharacterSelect(ActionEvent event) throws Exception {
        Button clickedButton = (Button) event.getSource();
        String btnText = clickedButton.getText().trim();

        // 擷取主角圖像
        switch (btnText) {
            case "Bald":
                heroImageFile = "Bald/SpriteSheet.png";
                break;
            case "Flameman":
                heroImageFile = "Flameman/SpriteSheet.png";
                break;
            case "Frozenman":
                heroImageFile = "Frozenman/SpriteSheet.png";
                break;
            case "Mage":
                heroImageFile = "Mage/SpriteSheet.png";
                break;
            case "Robot":
                heroImageFile = "Robot/SpriteSheet.png";
                break;
            case "Samurai":
                heroImageFile = "Samurai/SpriteSheet.png";
                break;
            default:
                break;
        }

        // 切換到 GameStage.fxml 畫面
        String fxmlPath = "/program/intro_to_cs_lab_final_project/GameStage.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent gameView = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getScene().setRoot(gameView);
    }

    // 遊戲初始化
    @FXML
    public void initialize() {
        if (mapGrid == null) return;

        mapManager = new Map();

        // 初始化地圖與實體
        mapManager.updateTileSize(512.0);
        mapManager.render(mapGrid);

        String baseCharacterDir = "/program/intro_to_cs_lab_final_project/Character/";
        String baseMonsterDir = "/program/intro_to_cs_lab_final_project/Monster/";
        player = new Entity(baseCharacterDir + heroImageFile, 1, 1);
        slime = new Entity(baseMonsterDir + "Slime.png", 14, 10);

        player.updateScale(mapManager.getTileSize(), mapGrid);
        slime.updateScale(mapManager.getTileSize(), mapGrid);
        player.addToMap(mapGrid);
        slime.addToMap(mapGrid);

        // 監聽畫面寬高
        javafx.application.Platform.runLater(() -> {
            if (mapGrid.getScene() != null) {
                javafx.scene.Scene scene = mapGrid.getScene();

                Runnable resizeGrid = () -> {
                    double sceneWidth = scene.getWidth();
                    double sceneHeight = scene.getHeight();

                    // 邊界留白
                    double availableWidth = sceneWidth - 100;   // 左右各留 50
                    double availableHeight = sceneHeight - 180; // 上下扣掉計分板與提示欄

                    if (availableWidth < 200) availableWidth = 200;
                    if (availableHeight < 150) availableHeight = 150;

                    // 依據 16:12比例計算
                    double widthBasedOnWidth = availableWidth;
                    double widthBasedOnHeight = availableHeight * (16.0 / 12.0);
                    // 兩者取小值以確保寬或高任何一方都不會超出邊界
                    double finalGridWidth = Math.min(widthBasedOnWidth, widthBasedOnHeight);

                    // 限制地圖的最終極限，不要無限放大或縮到看不見
                    if (finalGridWidth < 320) finalGridWidth = 320;
                    if (finalGridWidth > 800) finalGridWidth = 800;

                    // 算出對應的最終高度
                    double finalGridHeight = (finalGridWidth / 16.0) * 12.0;

                    // 強制鎖定 GridPane 的四維尺寸，破除變形魔咒，也能自由縮小！
                    mapGrid.setMaxWidth(finalGridWidth);
                    mapGrid.setMaxHeight(finalGridHeight);
                    mapGrid.setMinWidth(finalGridWidth);
                    mapGrid.setMinHeight(finalGridHeight);

                    // 重新刷新地圖與角色
                    mapManager.updateTileSize(finalGridWidth);
                    mapManager.render(mapGrid);

                    player.updateScale(mapManager.getTileSize(), mapGrid);
                    slime.updateScale(mapManager.getTileSize(), mapGrid);

                    // 重新把角色和怪物塞回網格
                    if (!mapGrid.getChildren().contains(player.imageView)) player.addToMap(mapGrid);
                    if (!mapGrid.getChildren().contains(slime.imageView)) slime.addToMap(mapGrid);
                };

                // 觸發寬高計算邏輯
                scene.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid.run());
                scene.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid.run());

                // 視窗第一次打開時，先手動觸發一次對齊
                resizeGrid.run();
            }
        });
    }
}