package program.intro_to_cs_lab_final_project;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

import java.util.*;

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
    @FXML
    private VBox MainMenu;
    @FXML
    private Button StartGame;
    @FXML
    private Button ExitGame;
    @FXML
    private VBox SelectHero;
    @FXML
    private StackPane ResultContainer;
    @FXML
    private VBox Victory;
    @FXML
    private VBox Failure;
    @FXML
    private ImageView pauseImageView;
    @FXML
    private ImageView bgmImageView;
    @FXML
    private ImageView soundEffectImageView;
    @FXML
    private VBox SelectLevel; // 關卡選單的容器
    @FXML
    private ImageView levelBackground;
    @FXML
    private VBox Victory10;

    @FXML private Button btnLevel1;
    @FXML private Button btnLevel2;
    @FXML private Button btnLevel3;
    @FXML private Button btnLevel4;
    @FXML private Button btnLevel5;
    @FXML private Button btnLevel6;
    @FXML private Button btnLevel7;
    @FXML private Button btnLevel8;
    @FXML private Button btnLevel9;
    @FXML private Button btnLevel10;

    private javafx.animation.Timeline gameLoop;

    private static MediaPlayer MainMenuBGM; // 主選班背景音樂
    private AudioClip clickSound; // 進入遊戲音效
    private AudioClip closeSound; // Exit 音效
    private AudioClip selectHeroSound; // 選角色音效
    private static MediaPlayer GameBGM; // 遊戲畫面背景音樂
    private AudioClip createBlockSound; // 生成方塊音效
    private AudioClip destroyBlockSound; // 摧毀方塊音效
    private static AudioClip victoryBGM; // 勝利音樂
    private static AudioClip failureBGM; // 失敗音樂

    // 玩家移動邏輯變數
    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean isKeyProcessing = false;
    private boolean isPaused = false;
    private boolean isBGMOn = true;
    private boolean isSoundEffectOn = true;

    // 宣告專屬的經理物件
    private Map mapManager;
    private Entity player;
    private SkillManager skillManager;

    private javafx.animation.Timeline countdownTimeline;
    private int remainingSeconds = 180;
    private boolean isSpacePressed = false; // 防 SPACE 鬼畜連發鎖
    private long keyPressStartTime = 0;      // 紀錄 WASD 按下的時間點
    private KeyCode currentMovingKey = null; // 目前主導移動的按鍵
    private long lastSkillCastTime = 0;      // 紀錄上一次成功放招的時間戳記（毫秒）
    private final long SKILL_COOLDOWN = 250;  // 技能冷卻時間(ms)
    private long lastEnvTickTime = 0;         // 紀錄上一次環境動態更新的時間點
    private long lastFoodMoveTime = 0;        // 記錄上一次活體食物移動的時間
    private int currentScore = 0;

    private final long ENV_TICK_INTERVAL = 3000;    // 環境更新時間
    private final long ITM_TICK_INTERVAL = 500;    // 食物移動更新時間
    private final long MON_TICK_INTERVAL = 270;    // 怪物移動更新時間

    private List<Monster> monsters = new ArrayList<>(); // 儲存當前關卡的所有怪物
    private long lastMonsterMoveTime = 0;               // 記錄上一次怪物移動的時間戳

    // 主選單：開始遊戲
    @FXML
    private void handleStartGame(ActionEvent event) {
        if (clickSound != null && isSoundEffectOn) {
            clickSound.play();
        }
        MainMenu.setVisible(false);
        SelectHero.setVisible(true);
    }

    // 從選角畫面返回主選單
    @FXML
    private void handleBackToMenu(javafx.scene.input.MouseEvent event) {
        SelectHero.setVisible(false);
        MainMenu.setVisible(true);
    }

    // 主選單：離開遊戲
    @FXML
    private void handleExitGame(ActionEvent event) {
        if (closeSound != null) {
            closeSound.play();
        }
        Stage stage = (Stage) ExitGame.getScene().getWindow();
        stage.close();
    }

    // 處理選單FXML檔
    @FXML
    private void handleCharacterSelect(ActionEvent event) throws Exception {
        if (selectHeroSound != null) {
            selectHeroSound.play();
        }

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

        refreshLevelButtons();

        if (SelectHero != null) SelectHero.setVisible(false);
        if (levelBackground != null) levelBackground.setVisible(true);
        if (SelectLevel != null) SelectLevel.setVisible(true);
    }

    @FXML
    private void handleLevelSelect(ActionEvent event) throws Exception {
        if (clickSound != null && isSoundEffectOn) {
            clickSound.play();
        }

        // 關閉主選單的背景音樂
        if (MainMenuBGM != null) {
            MainMenuBGM.stop();
        }

        // 透過按鈕的 id 取得對應的關卡數字
        Button clickedButton = (Button) event.getSource();
        String levelId = clickedButton.getId(); // 得到 "1", "2", "3" 等字串
        int selectedLevel = Integer.parseInt(levelId);

        System.out.println("🚀 玩家選擇了第 " + selectedLevel + " 關，正式進入遊戲！");

        // 🌟 將選好的關卡數字指派給單例 LevelManager
        LevelManager.getInstance().setCurrentLevel(selectedLevel);

        // 切換到 GameStage.fxml 畫面
        String fxmlPath = "/program/intro_to_cs_lab_final_project/GameStage.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent gameView = loader.load();

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getScene().setRoot(gameView);
    }

    @FXML
    private void handleBackToMenuFromLevels(ActionEvent event) {
        if (clickSound != null && isSoundEffectOn) {
            clickSound.play();
        }
        if (SelectLevel != null) SelectLevel.setVisible(false);
        if (levelBackground != null) levelBackground.setVisible(false);
        if (MainMenu != null) MainMenu.setVisible(true);
    }

    @FXML
    private void handleBackToHeroSelectFromLevels(ActionEvent event) {
        if (clickSound != null && isSoundEffectOn) {
            clickSound.play();
        }
        // 隱藏關卡選單與背景，顯示選角畫面
        if (SelectLevel != null) SelectLevel.setVisible(false);
        if (levelBackground != null) levelBackground.setVisible(false);
        if (SelectHero != null) SelectHero.setVisible(true);
    }

    @FXML
    private void handleBackToMenuFromGame(ActionEvent event) throws Exception {
        if (countdownTimeline != null) countdownTimeline.stop();
        if (GameBGM != null) GameBGM.stop();
        if (victoryBGM != null) victoryBGM.stop();
        if (failureBGM != null) failureBGM.stop();

        String menuPath = "/program/intro_to_cs_lab_final_project/Menu.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(menuPath));
        Parent root = loader.load();

        Controller menuController = loader.getController();

        if (menuController.MainMenu != null) menuController.MainMenu.setVisible(false);
        if (menuController.SelectHero != null) menuController.SelectHero.setVisible(false);
        if (menuController.levelBackground != null) menuController.levelBackground.setVisible(true);
        if (menuController.SelectLevel != null) menuController.SelectLevel.setVisible(true);

        menuController.refreshLevelButtons();

        if (Controller.MainMenuBGM != null) {
            Controller.MainMenuBGM.stop();
            Controller.MainMenuBGM.setVolume(0.4);
            Controller.MainMenuBGM.setCycleCount(MediaPlayer.INDEFINITE);
            Controller.MainMenuBGM.play(); //
        }

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getScene().setRoot(root);
    }

    @FXML
    private void handleRestartGame(ActionEvent event) {
//        if (clickSound != null) clickSound.play();
        if (victoryBGM != null) victoryBGM.stop();
        if (failureBGM != null) failureBGM.stop();

        if (ResultContainer != null) ResultContainer.setVisible(false);
        if (Victory != null) Victory.setVisible(false);
        if (Failure != null) Failure.setVisible(false);
        if (Victory10 != null) Victory10.setVisible(false);

        if (gameLoop != null) gameLoop.play();

        resetLevelScore();
        LevelManager.getInstance().startNewLevel();

        startCountdownTimer();
    }

    @FXML
    private void handleNextLevel(ActionEvent event) {
//        if (clickSound != null) clickSound.play();
        if (victoryBGM != null) victoryBGM.stop();

        if (ResultContainer != null) ResultContainer.setVisible(false);
        if (Victory != null) Victory.setVisible(false);
        int currentLvl = LevelManager.getInstance().getCurrentLevel();

        if (currentLvl < 10) {
            // 重新啟動遊戲主迴圈
            if (gameLoop != null) gameLoop.play();

        System.out.println("⏩ 觸發進入下一關！");
          LevelManager.getInstance().advanceToNextLevel();
          startCountdownTimer();
        }
    }

    @FXML
    private void handlePauseGame(ActionEvent event) {
        if (ResultContainer != null && ResultContainer.isVisible()) {
            return;
        }

        isPaused = !isPaused; // 切換暫停狀態

        if (isPaused) {
            System.out.println("⏸️ 遊戲暫停");

            if (gameLoop != null) gameLoop.stop();
            if (countdownTimeline != null) countdownTimeline.stop();

            String playImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/start.png").toExternalForm();
            pauseImageView.setImage(new Image(playImgPath));

        } else {
            if (gameLoop != null) gameLoop.play();
            if (countdownTimeline != null) countdownTimeline.play();

            // if (GameBGM != null) GameBGM.play();

            try {
                String pauseImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/pause.png").toExternalForm();
                pauseImageView.setImage(new Image(pauseImgPath));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void handleToggleBGM(ActionEvent event) {
        if (GameBGM == null) return;

        isBGMOn = !isBGMOn;

        if (isBGMOn) {
            GameBGM.play();

            if (bgmImageView != null) {
                try {
                    String bgmImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/NoBGM.png").toExternalForm();
                    bgmImageView.setImage(new Image(bgmImgPath));
                } catch (Exception e) {
                    System.err.println("找不到 BGM.png 圖片");
                }
            }
        } else {
            GameBGM.pause();

            if (bgmImageView != null) {
                try {
                    String noBgmImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/BGM.png").toExternalForm();
                    bgmImageView.setImage(new Image(noBgmImgPath));
                } catch (Exception e) {
                    System.err.println("找不到 NoBGM.png 圖片");
                }
            }
        }
    }

    @FXML
    private void handleToggleSoundEffect(ActionEvent event) {
        isSoundEffectOn = !isSoundEffectOn; // 切換音效開關狀態

        if (isSoundEffectOn) {
            System.out.println("🔊 遊戲音效：開啟");

            if (soundEffectImageView != null) {
                try {
                    String effectImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/NoEffect.png").toExternalForm();
                    soundEffectImageView.setImage(new Image(effectImgPath));
                } catch (Exception e) {
                    System.err.println("找不到 Effect.png 圖片");
                }
            }
        } else {
            System.out.println("🔇 遊戲音效：靜音");

            if (soundEffectImageView != null) {
                try {
                    String noEffectImgPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Icon/Effect.png").toExternalForm();
                    soundEffectImageView.setImage(new Image(noEffectImgPath));
                } catch (Exception e) {
                    System.err.println("找不到 NoEffect.png 圖片");
                }
            }
        }
    }

    // 遊戲初始化
    @FXML
    public void initialize() {
        if (mapGrid == null) {
            // 主選單背景音樂
            String BGMPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Musics/4 - Village.mp3").toExternalForm();
            Media BGMMedia = new Media(BGMPath);
            MainMenuBGM = new MediaPlayer(BGMMedia);
            MainMenuBGM.setVolume(0.4);
            MainMenuBGM.setCycleCount(MediaPlayer.INDEFINITE);
            MainMenuBGM.play();

            // 進入遊戲音效
            String clickPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Sounds/Alert/Alert4.wav").toExternalForm();
            clickSound = new AudioClip(clickPath);

            // Exit 音效
            String closePath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Sounds/Menu/Menu12.wav").toExternalForm();
            closeSound = new AudioClip(closePath);

            // 選角色音效
            String selectHeroPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Sounds/Menu/Accept5.wav").toExternalForm();
            selectHeroSound = new AudioClip(selectHeroPath);
            closeSound.setVolume(0.6);

            // 勝利音樂
            String victoryPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Musics/20 - Good Time.mp3").toExternalForm();
            victoryBGM = new AudioClip(victoryPath);
            victoryBGM.setVolume(0.4);
            victoryBGM.setCycleCount(AudioClip.INDEFINITE);

            // 失敗音樂
            String failurePath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Musics/SadHamster.mp3").toExternalForm();
            failureBGM = new AudioClip(failurePath);
            failureBGM.setVolume(0.2);
            failureBGM.setCycleCount(AudioClip.INDEFINITE);

            if (MainMenu != null) MainMenu.setVisible(true);
            if (SelectHero != null) SelectHero.setVisible(false);
            return;
        }

        // 遊戲背景音樂
        String GameBGMPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Musics/16 - Melancholia.mp3").toExternalForm();
        Media GameBGMMedia = new Media(GameBGMPath);
        GameBGM = new MediaPlayer(GameBGMMedia);
        GameBGM.setVolume(0.05);
        GameBGM.setCycleCount(MediaPlayer.INDEFINITE);
        GameBGM.play();

        // 生成方塊音效
        String createPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Sounds/Whoosh & Slash/Slash.wav").toExternalForm();
        createBlockSound = new AudioClip(createPath);
        createBlockSound.setVolume(0.6);
        createBlockSound.play(0.0);

        // 摧毀方塊音效
        String destroyPath = getClass().getResource("/program/intro_to_cs_lab_final_project/Audio/Sounds/Whoosh & Slash/Sword2.wav").toExternalForm();
        destroyBlockSound = new AudioClip(destroyPath);
        destroyBlockSound.setVolume(0.6);
        destroyBlockSound.play(0.0);

        mapManager = new Map();
        skillManager = new SkillManager(mapManager, mapGrid);

        // 初始化地圖與實體尺寸
        mapManager.updateTileSize(512.0);

        String baseCharacterDir = "/program/intro_to_cs_lab_final_project/Character/";

        // ======遊戲內部邏輯功能驗證用，開發完成前不要刪=======
        //if (heroImageFile == null || heroImageFile.isEmpty()) {
        //    heroImageFile = "Samurai/SpriteSheet.png";
        //}
        // ===============================================

        player = new Entity(baseCharacterDir + heroImageFile, 1, 1);

        // 清空舊怪物
        monsters.clear();

        LevelManager.getInstance().init(this, mapManager, mapGrid);
        LevelManager.getInstance().startNewLevel(); // 內部會觸發 resetPlayerPosition, setMonstersForLevel, spawnNextRoundOfFood

        // 監聽畫面寬高
        javafx.application.Platform.runLater(() -> {
            if (mapGrid.getScene() != null) {
                javafx.scene.Scene scene = mapGrid.getScene();

                // 排版計算邏輯 (把原本少掉的排版精華原封不動補回來！)
                Runnable resizeGrid = () -> {
                    double sceneWidth = scene.getWidth();
                    double sceneHeight = scene.getHeight();

                    // 邊界留白
                    double availableWidth = sceneWidth - 100;   // 左右各留 50
                    double availableHeight = sceneHeight - 180; // 上下扣掉計分板與提示欄

                    if (availableWidth < 200) availableWidth = 200;
                    if (availableHeight < 150) availableHeight = 150;

                    // 依據 16:12 比例計算
                    double widthBasedOnWidth = availableWidth;
                    double widthBasedOnHeight = availableHeight * (16.0 / 12.0);
                    // 兩者取小值以確保寬或高任何一方都不會超出邊界
                    double finalGridWidth = Math.min(widthBasedOnWidth, widthBasedOnHeight);

                    // 限制地圖的最終極限
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

                    // 重新把角色和全場怪物塞回網格，確保最上層渲染
                    if (!mapGrid.getChildren().contains(player.imageView)) player.addToMap(mapGrid);

                    for (Monster m : monsters) {
                        m.updateScale(mapManager.getTileSize(), mapGrid);
                        if (!mapGrid.getChildren().contains(m.imageView)) m.addToMap(mapGrid);
                    }
                };

                // 觸發寬高計算邏輯
                scene.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid.run());
                scene.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid.run());

                // 視窗第一次打開時，先手動觸發一次對齊
                resizeGrid.run();

                // 監聽按下按鍵：打開方向，或者觸發單次施法
                scene.setOnKeyPressed(event -> {
                    if (isPaused) {
                        return;
                    }

                    KeyCode code = event.getCode();

                    // 轉向優先邏輯
                    if (code == KeyCode.W || code == KeyCode.S || code == KeyCode.A || code == KeyCode.D) {
                        if (currentMovingKey != code) {
                            currentMovingKey = code;
                            keyPressStartTime = System.currentTimeMillis();
                        }

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
                            // 防連發、防移動中施法、防施法中移動
                            if (!isSpacePressed && !player.isMoving() && !isKeyProcessing) {

                                long currentTime = System.currentTimeMillis();
                                if (currentTime - lastSkillCastTime < SKILL_COOLDOWN) {
                                    break;
                                }

                                isSpacePressed = true;
                                lastSkillCastTime = currentTime;

                                isKeyProcessing = true; // 鎖定移動狀態
                                player.setFacingBattlePose(); // 設定出招姿勢

                                String heroName = heroImageFile.split("/")[0];
                                int targetCol = player.getCol() + player.getFacingDeltaCol();
                                int targetRow = player.getRow() + player.getFacingDeltaRow();
                                int frontTile = mapManager.getTileType(targetCol, targetRow);
                                int myHeroTile = skillManager.getHeroTileType(heroName);

                                // 遍歷怪獸軍團，檢查技能施放目標點是否有怪
                                boolean isMonsterStandingThere = false;
                                for (Monster m : monsters) {
                                    if (targetCol == m.getCol() && targetRow == m.getRow()) {
                                        isMonsterStandingThere = true;
                                        break;
                                    }
                                }

                                // 判斷生成/摧毀技能方塊
                                if (frontTile == myHeroTile && !isMonsterStandingThere) {
                                    if (destroyBlockSound != null && isSoundEffectOn) destroyBlockSound.play();
                                    skillManager.castDestroySkill(player, heroName);
                                } else {
                                    if (createBlockSound != null && isSoundEffectOn) createBlockSound.play();
                                    skillManager.castCreateSkill(player, monsters, heroName);
                                }

                                // 確保重新繪製後，角色和怪物不會不小心被地圖蓋過去
                                mapManager.render(mapGrid);
                                if (!mapGrid.getChildren().contains(player.imageView)) player.addToMap(mapGrid);
                                for (Monster m : monsters) {
                                    if (!mapGrid.getChildren().contains(m.imageView)) m.addToMap(mapGrid);
                                }

                                // 200 毫秒後收招
                                javafx.animation.Timeline castAnimation = new javafx.animation.Timeline(
                                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), e -> {
                                            player.setFacing(player.getFacingDeltaCol(), player.getFacingDeltaRow());
                                            isKeyProcessing = false;
                                        })
                                );
                                castAnimation.setCycleCount(1);
                                castAnimation.play();
                            }
                        }
                        default -> {}
                    }
                });

                // 監聽放開按鍵
                scene.setOnKeyReleased(event -> {
                    KeyCode code = event.getCode();

                    if (code == currentMovingKey) {
                        currentMovingKey = null;
                        keyPressStartTime = 0;
                    }

                    switch (code) {
                        case W -> keyUp = false;
                        case S -> keyDown = false;
                        case A -> keyLeft = false;
                        case D -> keyRight = false;
                        case SPACE -> isSpacePressed = false;
                        default -> {}
                    }
                });

                // 建立連續移動主迴圈
                gameLoop = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(10), e -> {

                            long currentTime = System.currentTimeMillis();

                            // 怪獸接觸玩家判定 Game Over
                            int playerCol = player.getCol();
                            int playerRow = player.getRow();
                            for (Monster m : monsters) {
                                if (m.getCol() == playerCol && m.getRow() == playerRow) {
                                    System.out.println("You are died");
                                    if (countdownTimeline != null) countdownTimeline.stop();
                                    if (GameBGM != null) GameBGM.stop();

                                    if (ResultContainer != null) ResultContainer.setVisible(true);
                                    if (Failure != null) Failure.setVisible(true);

                                    if (failureBGM != null) failureBGM.play();

                                    LevelManager.getInstance().triggerGameOver();
                                    return; // 直接中斷迴圈，不再執行後續移動
                                }
                            }

                            // 環境屬性互剋更新
                            if (currentTime - lastEnvTickTime >= ENV_TICK_INTERVAL) {
                                skillManager.updateEnvironmentTick(player, monsters);
                                lastEnvTickTime = currentTime;
                            }

                            // 活體食物更新
                            if (currentTime - lastFoodMoveTime >= ITM_TICK_INTERVAL) {
                                ItemManager.updateActiveFoodMove(mapManager, mapGrid, player, monsters);
                                lastFoodMoveTime = currentTime;
                            }

                            // 怪物 AI 移動更新
                            if (currentTime - lastMonsterMoveTime >= MON_TICK_INTERVAL) {
                                MonsterManager.updateMonsterAI(mapManager, mapGrid, player, monsters);
                                lastMonsterMoveTime = currentTime;
                            }

                            if (isKeyProcessing || player.isMoving()) return;

                            // 按鍵時間是否超過 120 毫秒才可移動
                            if (currentMovingKey != null && keyPressStartTime > 0) {
                                long duration = System.currentTimeMillis() - keyPressStartTime;
                                if (duration < 120) return;
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

                                // 傳送門安全防護線檢查
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

                                    int finalPlayerCol = player.getCol();
                                    int finalPlayerRow = player.getRow();

                                    // 檢查最終座標有沒有踩到食物
                                    int foodId = mapManager.getItemType(finalPlayerCol, finalPlayerRow);

                                    if (foodId > 0) {
                                        // 清空食物陣列
                                        mapManager.setItemType(finalPlayerCol, finalPlayerRow, 0);

                                        // 用當下正確的網格坐標移除 ImageView
                                        String targetFoodId = "food_" + finalPlayerCol + "_" + finalPlayerRow;
                                        javafx.application.Platform.runLater(() -> {
                                            mapGrid.getChildren().removeIf(node -> targetFoodId.equals(node.getId()));
                                        });

                                        // 串接 LevelManager ，由其負責判定
                                        LevelManager.getInstance().onPlayerEatFood();
                                    }
                                });
                            }
                        })
                );
                gameLoop.setCycleCount(javafx.animation.Timeline.INDEFINITE);
                gameLoop.play();

                startCountdownTimer();
            }
        });
    }

    public void addScore(int points) {
        currentScore += points;
        if (currentScore < 0) currentScore = 0;
        if (scoreLabel != null) {
            javafx.application.Platform.runLater(() -> {
                String formattedScore = String.format("%05d", currentScore);
                scoreLabel.setText("SCORE: " + formattedScore);
            });
        }
    }

    public void setMonstersForLevel(List<Monster> newMonsters) {
        // 清除舊怪，防止影像殘留
        for (Monster m : this.monsters) {
            if (m.imageView != null) mapGrid.getChildren().remove(m.imageView);
        }
        this.monsters = newMonsters;
        for (Monster m : this.monsters) {
            m.updateScale(mapManager.getTileSize(), mapGrid);
            m.addToMap(mapGrid);
        }
    }

    public void resetPlayerPosition(int col, int row) {
        player.setCol(col);
        player.setRow(row);
        javafx.application.Platform.runLater(() -> {
            mapGrid.getChildren().remove(player.imageView);
            player.addToMap(mapGrid);
        });
    }

    public void resetLevelScore() {
        this.currentScore = 0;
        addScore(0);
    }

    public void stopGameLoop() {
        if (gameLoop != null) gameLoop.stop();
    }

    public void showVictoryPanel() {
        javafx.application.Platform.runLater(() -> {
            if (countdownTimeline != null) countdownTimeline.stop();
            if (GameBGM != null) GameBGM.stop();

            // 顯示最外層的透明黑底容器
            if (ResultContainer != null) ResultContainer.setVisible(true);

            int currentLvl = LevelManager.getInstance().getCurrentLevel();

            if (currentLvl >= 10) {
                // 如果是第 10 關（最後一關）通關
                if (Victory != null) Victory.setVisible(false);
                if (Victory10 != null) Victory10.setVisible(true);
            } else {
                // 1 ~ 9 關通關
                if (Victory10 != null) Victory10.setVisible(false);
                if (Victory != null) Victory.setVisible(true);
            }

            if (victoryBGM != null) {
                victoryBGM.play();
            }

            stopGameLoop(); // 停止遊戲世界的 Timeline 更新
        });
    }

    public void playGameBGM() {
        if (GameBGM != null) {
            GameBGM.stop();
            GameBGM.setVolume(0.1);
            GameBGM.setCycleCount(MediaPlayer.INDEFINITE);
            GameBGM.play();
        }
    }

    private void startCountdownTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        remainingSeconds = 180;
        updateClockLabel();

        countdownTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    remainingSeconds--;
                    updateClockLabel();

                    if (remainingSeconds <= 0) {
                        countdownTimeline.stop();
                        triggerTimeOutFailure();
                    }
                })
        );
        countdownTimeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void updateClockLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        javafx.application.Platform.runLater(() -> {
            if (clockLabel != null) {
                clockLabel.setText(timeString);
            }
        });
    }

    private void triggerTimeOutFailure() {
        javafx.application.Platform.runLater(() -> {
            stopGameLoop(); // 停止原本的主迴圈
            if (GameBGM != null) GameBGM.stop();
            if (ResultContainer != null) ResultContainer.setVisible(true);
            if (Failure != null) Failure.setVisible(true);
            if (failureBGM != null) failureBGM.play();
        });

        LevelManager.getInstance().triggerGameOver();
    }

    private void refreshLevelButtons() {
        List<Button> levelButtons = Arrays.asList(
                btnLevel1, btnLevel2, btnLevel3, btnLevel4, btnLevel5,
                btnLevel6, btnLevel7, btnLevel8, btnLevel9, btnLevel10
        );

        int maxUnlocked = LevelManager.getInstance().getMaxUnlockedLevel();

        javafx.scene.effect.ColorAdjust grayscaleEffect = new javafx.scene.effect.ColorAdjust();
        grayscaleEffect.setSaturation(-0.5);

        for (int i = 0; i < levelButtons.size(); i++) {
            Button btn = levelButtons.get(i);
            if (btn == null) continue;

            int levelNum = i + 1;

            if (levelNum <= maxUnlocked) {
                btn.setDisable(false);
                if (btn.getGraphic() != null) {
                    btn.getGraphic().setEffect(null);
                }
            } else {
                btn.setDisable(true);
                if (btn.getGraphic() != null) {
                    btn.getGraphic().setEffect(grayscaleEffect); // 附加灰色濾鏡
                }
            }
        }
    }
}