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

    // ==========================================
    // FXML 畫面元件
    // ==========================================
    @FXML private Label scoreLabel;
    @FXML private Label clockLabel;
    @FXML private ImageView clockImage;
    @FXML private ImageView selectImage;
    @FXML private GridPane mapGrid;

    // 用 static 確保換幕後資料不會丟失（預設為光頭哥 SpriteSheet.png）
    private static String heroImageFile = "SpriteSheet.png";

    // 宣告專屬的經理物件
    private Map mapManager;
    private Entity player;
    private Entity slime;

    // ==========================================
    // 處理選單（menu.fxml）按鈕點擊
    // ==========================================
    @FXML
    private void handleCharacterSelect(ActionEvent event) throws Exception {
        Button clickedButton = (Button) event.getSource();
        String btnText = clickedButton.getText().trim();

        // Controller.java 裡面的暫時防呆設定：
        switch (btnText) {
            case "Bald":      heroImageFile = "SpriteSheet.png"; break;
            case "Flameman":  heroImageFile = "SpriteSheet.png"; break; // 先用光頭哥頂著
            case "Frozenman": heroImageFile = "SpriteSheet.png"; break;
            case "Mage":      heroImageFile = "SpriteSheet.png"; break;
            case "Robot":     heroImageFile = "SpriteSheet.png"; break;
            case "Samurai":   heroImageFile = "SpriteSheet.png"; break;
            default:          heroImageFile = "SpriteSheet.png"; break;
        }

        // 切換到 GameStage.fxml 畫面
        String fxmlPath = "/program/intro_to_cs_lab_final_project/GameStage.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent gameView = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getScene().setRoot(gameView);
    }

    // ==========================================
    // 遊戲畫面初始化（GameStage.fxml 被載入時自動觸發）
    // ==========================================
    @FXML
    public void initialize() {
        // 如果是在選單畫面，mapGrid 是 null，就直接跳出不執行
        if (mapGrid == null) {
            return;
        }

        System.out.println("🎮 遊戲大腦啟動！目前選擇主角: " + heroImageFile);

        mapManager = new Map();
        mapManager.render(mapGrid);

        String baseCharacterDir = "/program/intro_to_cs_lab_final_project/Character/";
        String baseMonsterDir = "/program/intro_to_cs_lab_final_project/Monster/";


        player = new Entity(baseCharacterDir + heroImageFile, 1, 1);
        player.addToMap(mapGrid);


        slime = new Entity(baseMonsterDir + "Slime.png", 7, 1);
        slime.addToMap(mapGrid);

        System.out.println("🎉 靜態地圖鋪設完畢，且角色與怪物已成功生成！");
    }
}