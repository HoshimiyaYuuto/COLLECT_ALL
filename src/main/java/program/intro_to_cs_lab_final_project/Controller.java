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
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;

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

    // 玩家移動邏輯變數
    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean isKeyProcessing = false;

    // 宣告專屬的經理物件
    private Map mapManager;
    private Entity player;
    private Entity slime;
    private SkillManager skillManager;

    private boolean isSpacePressed = false; // 防 SPACE 鬼畜連發鎖
    private long keyPressStartTime = 0;      // 紀錄 WASD 按下的時間點
    private KeyCode currentMovingKey = null; // 目前主導移動的按鍵
    private long lastSkillCastTime = 0;      // 紀錄上一次成功放招的時間戳記（毫秒）
    private final long SKILL_COOLDOWN = 250;  // 技能冷卻時間(ms)
    private long lastEnvTickTime = 0;         // 紀錄上一次環境動態更新的時間點
    private final long ENV_TICK_INTERVAL = 3000;

    // 處理選單FXML檔
    @FXML
    private void handleCharacterSelect(ActionEvent event) throws Exception {
        Button clickedButton = (Button) event.getSource();
        String btnText = clickedButton.getText().trim();

        // 擷取主角圖像
        switch (btnText) {
            case "Bald" -> heroImageFile = "Bald/SpriteSheet.png";
            case "Flameman" -> heroImageFile = "Flameman/SpriteSheet.png";
            case "Frozenman" -> heroImageFile = "Frozenman/SpriteSheet.png";
            case "Mage" -> heroImageFile = "Mage/SpriteSheet.png";
            case "Robot" -> heroImageFile = "Robot/SpriteSheet.png";
            case "Samurai" -> heroImageFile = "Samurai/SpriteSheet.png";
            default -> {}
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
        skillManager = new SkillManager(mapManager, mapGrid);

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

                // 排版計算邏輯
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

                    // 強制鎖定 GridPane 的四維尺寸
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

                // 監聽按下按鍵：打開方向，或者觸發單次施法
                scene.setOnKeyPressed(event -> {
                    KeyCode code = event.getCode();

                    // 轉向優先邏輯：一按下 WASD，立刻強迫角色轉向，並記錄時間
                    if (code == KeyCode.W || code == KeyCode.S || code == KeyCode.A || code == KeyCode.D) {
                        if (currentMovingKey != code) {
                            currentMovingKey = code;
                            keyPressStartTime = System.currentTimeMillis(); // 記錄按下起點
                        }

                        // 根據按鍵直接先改角色的面向，這樣玩家輕點就能原地轉身！
                        switch (code) {
                            case W -> { keyUp = true;    player.setFacing(0, -1); }
                            case S -> { keyDown = true;  player.setFacing(0, 1); }
                            case A -> { keyLeft = true;  player.setFacing(-1, 0); }
                            case D -> { keyRight = true; player.setFacing(1, 0); }
                            default -> {}
                        }
                    }

                    // 讀取空白鍵反應(生成/摧毀技能方塊)
                    switch (code) {
                        case SPACE -> {
                            // 雙重鎖：除了防長按連發（!isSpacePressed），還要檢查冷卻時間有沒有到！
                            if (!isSpacePressed && !player.isMoving()) {

                                // 時間檢查：如果距離上次放招還不到 250 毫秒，直接無情攔截，不准放！
                                long currentTime = System.currentTimeMillis();
                                if (currentTime - lastSkillCastTime < SKILL_COOLDOWN) {
                                    break;
                                }

                                isSpacePressed = true; // 鎖定長按
                                lastSkillCastTime = currentTime; // 蓋章！更新本次成功放招的時間點

                                String heroName = heroImageFile.split("/")[0];
                                int targetCol = player.getCol() + player.getFacingDeltaCol();
                                int targetRow = player.getRow() + player.getFacingDeltaRow();
                                int frontTile = mapManager.getTileType(targetCol, targetRow);
                                int myHeroTile = skillManager.getHeroTileType(heroName);

                                boolean isSlimeStandingThere = (targetCol == slime.getCol() && targetRow == slime.getRow());
                                if (frontTile == myHeroTile && !isSlimeStandingThere) {
                                    skillManager.castDestroySkill(player, heroName);
                                } else {
                                    skillManager.castCreateSkill(player, slime, heroName);
                                }

                                // 施法完畢後補畫角色和怪物
                                if (!mapGrid.getChildren().contains(player.imageView)) player.addToMap(mapGrid);
                                if (!mapGrid.getChildren().contains(slime.imageView)) slime.addToMap(mapGrid);
                            }
                        }
                        default -> {}
                    }
                });

                // 監聽放開按鍵
                scene.setOnKeyReleased(event -> {
                    KeyCode code = event.getCode();

                    if (code == currentMovingKey) {
                        currentMovingKey = null; // 清空移動主導鍵
                        keyPressStartTime = 0;
                    }

                    switch (code) {
                        case W -> keyUp = false;
                        case S -> keyDown = false;
                        case A -> keyLeft = false;
                        case D -> keyRight = false;
                        case SPACE -> isSpacePressed = false; // 👑 放開空白鍵時，解除冷卻鎖！
                        default -> {}
                    }
                });

                // 建立連續移動主迴圈（加入長按判定）
                javafx.animation.Timeline gameLoop = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(10), e -> {

                            // 🌍 核心新增：每 500 毫秒叫醒 skillManager 檢查全地圖的「草吸水」與「水/冰滅火」
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastEnvTickTime >= ENV_TICK_INTERVAL) {
                                skillManager.updateEnvironmentTick(player, slime); // 呼叫我們之前寫好的環境大巡邏
                                lastEnvTickTime = currentTime;        // 更新時間戳記
                            }

                            // ----------------- 以下是妳原本完好無動的 WASD 移動邏輯 -----------------
                            if (isKeyProcessing || player.isMoving()) return;

                            // 長按起跑安全閥：檢查按鍵時間是否超過 120 毫秒。
                            if (currentMovingKey != null && keyPressStartTime > 0) {
                                long duration = System.currentTimeMillis() - keyPressStartTime;
                                if (duration < 120) return; // 蓄力時間不夠，攔截，不准走！
                            }

                            int deltaCol = 0;
                            int deltaRow = 0;

                            // 依照目前的按鍵決定移動方向
                            if (keyUp) deltaRow = -1;
                            else if (keyDown) deltaRow = 1;
                            else if (keyLeft) deltaCol = -1;
                            else if (keyRight) deltaCol = 1;

                            if (deltaCol != 0 || deltaRow != 0) {
                                int currentCol = player.getCol();
                                int currentRow = player.getRow();
                                int targetCol = currentCol + deltaCol;
                                int targetRow = currentRow + deltaRow;

                                // 傳送安全性檢查區
                                if (targetCol < 0) {
                                    if (mapManager.getTileType(0, currentRow) != 0 || mapManager.getTileType(15, currentRow) != 0) return;
                                } else if (targetCol > 15) {
                                    if (mapManager.getTileType(15, currentRow) != 0 || mapManager.getTileType(0, currentRow) != 0) return;
                                } else if (targetRow < 0) {
                                    if (mapManager.getTileType(currentCol, 0) != 0 || mapManager.getTileType(currentCol, 11) != 0) return;
                                } else if (targetRow > 11) {
                                    if (mapManager.getTileType(currentCol, 11) != 0 || mapManager.getTileType(currentCol, 0) != 0) return;
                                }

                                isKeyProcessing = true; // 上鎖

                                player.moveSmoothly(deltaCol, deltaRow, mapManager, mapGrid, () -> {
                                    isKeyProcessing = false; // 解鎖

                                    int pCol = player.getCol();
                                    int pRow = player.getRow();
                                    boolean teleported = false;

                                    if (pCol < 0) { pCol = 15; teleported = true; }
                                    else if (pCol > 15) { pCol = 0; teleported = true; }

                                    if (pRow < 0) { pRow = 11; teleported = true; }
                                    else if (pRow > 11) { pRow = 0; teleported = true; }

                                    if (teleported) {
                                        player.setCol(pCol);
                                        player.setRow(pRow);

                                        final int finalCol = pCol;
                                        final int finalRow = pRow;

                                        javafx.application.Platform.runLater(() -> {
                                            mapGrid.getChildren().remove(player.imageView);
                                            mapGrid.add(player.imageView, finalCol, finalRow);
                                        });
                                    }
                                });
                            }
                        })
                );
                gameLoop.setCycleCount(javafx.animation.Timeline.INDEFINITE);
                gameLoop.play();
            }
        });
    }
}