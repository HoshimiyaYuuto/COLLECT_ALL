package program.intro_to_cs_lab_final_project;

import javafx.scene.layout.GridPane;
import java.util.*;

public class LevelManager {
    private static LevelManager instance;
    private final Random random = new Random();

    private int currentLevel = 1;   // Debug 能改這
    private final int MAX_LEVELS = 10;
    private int totalFoodEatenInLevel = 0;
    private int currentRoundFoodCount = 0;

    private List<FoodNode> backupFoodPool = new ArrayList<>();

    private Map mapManager;
    private GridPane mapGrid;
    private Controller controller;

    public static LevelManager getInstance() {
        if (instance == null) instance = new LevelManager();
        return instance;
    }

    public void init(Controller controller, Map mapManager, GridPane mapGrid) {
        this.controller = controller;
        this.mapManager = mapManager;
        this.mapGrid = mapGrid;
    }

    public int getCurrentLevel() { return currentLevel; }

    private static class FoodNode {
        int col, row, foodId;
        FoodNode(int c, int r, int id) { this.col = c; this.row = r; this.foodId = id; }
    }

    // 地圖 & 食物位置配置
    private void loadCustomLevelData(int level, Map targetMap) {
        int[][] gMap = new int[12][16];
        int[][] fMap = new int[12][16];

        switch (level) {
            case 1 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                        {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 0, 2, 0, 3, 0, 0, 0, 0, 4, 0, 5, 0, 6, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 7, 0, 0, 0, 0, 8, 0, 0, 9, 0, 0, 0, 0, 10, 0},
                        {0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, 0},
                        {0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0},
                        {0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0},
                        {0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0, 0},
                        {0, 19, 0, 0, 0, 0, 20, 0, 0, 21, 0, 0, 0, 0, 22, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 2 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 2, 0, 0, 0, 0, 3, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 2, 3, 0, 0, 0, 0, 2, 3, 0, 0, 0, 1},
                        {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                        {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                        {1, 0, 0, 0, 3, 2, 0, 0, 0, 0, 3, 2, 0, 0, 0, 1},
                        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 3, 0, 0, 0, 0, 2, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 1, 0, 2, 0, 3, 0, 0, 4, 0, 5, 0, 6, 0, 0},
                        {0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 9, 0, 10, 0, 0, 0, 11, 12, 0, 0, 0, 13, 0, 14, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 15, 0, 16, 0, 0, 0, 17, 18, 0, 0, 0, 19, 0, 20, 0},
                        {0, 21, 0, 0, 0, 0, 22, 0, 0, 23, 0, 0, 0, 0, 24, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 3 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 4, 0, 1, 0, 2, 2, 2, 2, 0, 1, 0, 5, 0, 1},
                        {1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1},
                        {1, 0, 2, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 3, 0, 1},
                        {1, 0, 0, 0, 1, 0, 0, 3, 3, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 0, 0, 1, 0, 0, 2, 2, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 3, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 2, 0, 1},
                        {1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1},
                        {1, 0, 5, 0, 1, 0, 3, 3, 3, 3, 0, 1, 0, 4, 0, 1},
                        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 2, 3, 0, 4, 5, 0, 0, 6, 7, 0, 8, 9, 10, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 0},
                        {0, 0, 0, 0, 0, 0, 13, 0, 0, 14, 0, 0, 0, 0, 0, 0},
                        {0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0},
                        {0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0},
                        {0, 0, 0, 0, 0, 0, 19, 0, 0, 20, 0, 0, 0, 0, 0, 0},
                        {0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 4 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 1, 1, 1, 2, 3, 4, 5, 1, 1, 1, 1, 0, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 0, 1, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 1, 0, 1},
                        {1, 0, 2, 0, 4, 0, 0, 1, 1, 0, 0, 5, 0, 3, 0, 1},
                        {1, 0, 2, 0, 5, 0, 0, 1, 1, 0, 0, 4, 0, 3, 0, 1},
                        {1, 0, 1, 0, 3, 3, 0, 0, 0, 0, 2, 2, 0, 1, 0, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 0, 1, 1, 1, 1, 5, 4, 3, 2, 1, 1, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0},
                        {0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 5 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 2, 0, 0, 3, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 2, 0, 0, 3, 0, 1, 0, 1, 0, 1},
                        {1, 0, 0, 4, 0, 0, 2, 0, 0, 3, 0, 0, 5, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                        {1, 0, 0, 5, 0, 0, 3, 0, 0, 2, 0, 0, 4, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 3, 0, 0, 2, 0, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 0, 2, 0, 3, 0, 0, 0, 0, 4, 0, 5, 0, 6, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 7, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 10, 0},
                        {0, 0, 0, 0, 0, 0, 11, 12, 13, 14, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0},
                        {0, 19, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 22, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 6 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1},
                        {1, 0, 1, 0, 2, 0, 1, 1, 1, 1, 0, 3, 0, 1, 0, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 4, 4, 4, 0, 1, 1, 0, 0, 1, 1, 0, 5, 5, 5, 1},
                        {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                        {1, 5, 5, 5, 0, 1, 1, 1, 1, 1, 1, 0, 4, 4, 4, 1},
                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 0, 1, 0, 3, 0, 1, 1, 1, 1, 0, 2, 0, 1, 0, 1},
                        {1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 2, 3, 0, 0, 4, 5, 6, 7, 0, 0, 8, 9, 10, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 11, 0, 0, 12, 13, 0, 0, 0, 0, 14, 15, 0, 0, 16, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 17, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 20, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 7 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 2, 2, 2, 2, 1, 0, 0, 1, 3, 3, 3, 3, 0, 1},
                        {1, 0, 2, 0, 0, 2, 1, 0, 0, 1, 3, 0, 0, 3, 0, 1},
                        {1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1},
                        {1, 0, 1, 1, 0, 0, 4, 4, 5, 5, 0, 0, 1, 1, 0, 1},
                        {1, 0, 1, 1, 0, 0, 5, 5, 4, 4, 0, 0, 1, 1, 0, 1},
                        {1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1},
                        {1, 0, 3, 0, 0, 3, 1, 0, 0, 1, 2, 0, 0, 2, 0, 1},
                        {1, 0, 3, 3, 3, 3, 1, 0, 0, 1, 2, 2, 2, 2, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 2, 3, 4, 0, 5, 6, 7, 8, 0, 9, 10, 11, 12, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 13, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 16, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0},
                        {0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0},
                        {0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0},
                        {0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 8 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 2, 0, 3, 0, 4, 1},
                        {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 1, 6, 1, 1, 4, 1, 1, 0, 1, 0, 1},
                        {1, 0, 5, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 1},
                        {1, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 1},
                        {1, 0, 1, 0, 1, 1, 1, 5, 1, 7, 1, 1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                        {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
                        {1, 0, 4, 0, 3, 0, 2, 0, 5, 0, 4, 0, 3, 0, 2, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 0},
                        {0, 8, 9, 10, 11, 12, 0, 0, 0, 0, 13, 14, 15, 16, 17, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 18, 19, 0, 0, 20, 21, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 22, 23, 0, 0, 24, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 9 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 2, 3, 4, 0, 1, 0, 0, 1, 0, 5, 2, 3, 0, 1},
                        {1, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1},
                        {1, 0, 2, 0, 1, 1, 2, 3, 4, 5, 1, 1, 0, 2, 0, 1},
                        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                        {1, 0, 3, 0, 1, 1, 5, 4, 3, 2, 1, 1, 0, 5, 0, 1},
                        {1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1},
                        {1, 0, 5, 2, 3, 0, 1, 0, 0, 1, 0, 4, 5, 3, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 2, 3, 4, 5, 0, 0, 0, 0, 6, 7, 8, 9, 10, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 11, 0, 0, 0, 0, 12, 13, 14, 15, 0, 0, 0, 0, 16, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0},
                        {0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 21, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            case 10 -> {
                gMap = new int[][]{
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 2, 3, 4, 0, 0, 0, 0, 0, 2, 3, 4, 5, 0, 1},
                        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1},
                        {1, 0, 5, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 2, 0, 1},
                        {1, 0, 4, 0, 2, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 1},
                        {1, 0, 3, 0, 3, 0, 1, 1, 1, 1, 0, 4, 0, 4, 0, 1},
                        {1, 0, 2, 0, 4, 0, 1, 1, 1, 1, 0, 5, 0, 5, 0, 1},
                        {1, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 1},
                        {1, 0, 4, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 3, 0, 1},
                        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1},
                        {1, 0, 3, 2, 5, 4, 0, 0, 0, 0, 3, 2, 5, 4, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                fMap = new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 5, 0},
                        {0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 7, 8, 9, 0, 0, 0, 0, 10, 11, 12, 0, 0, 0},
                        {0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0},
                        {0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0},
                        {0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0},
                        {0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0},
                        {0, 0, 0, 21, 22, 23, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
            }
            default -> {
                gMap = new int[12][16];
                fMap = new int[12][16];
            }
        }
        targetMap.loadLevelData(gMap, fMap);
    }

    private List<Monster> generateLevelMonsters(int level) {
        List<Monster> levelMonsters = new ArrayList<>();
        switch (level) {
            case 1 -> {
                levelMonsters.add(MonsterRegistry.create("Slime", 8, 2));
                levelMonsters.add(MonsterRegistry.create("Slime2", 8, 9));
            }
            case 2 -> {
                levelMonsters.add(MonsterRegistry.create("Slime3", 8, 2));
                levelMonsters.add(MonsterRegistry.create("Mushroom", 14, 10));
            }
            case 3 -> {
                levelMonsters.add(MonsterRegistry.create("Mushroom2", 2, 9));
                levelMonsters.add(MonsterRegistry.create("Beast", 14, 10));
            }
            case 4 -> {
                levelMonsters.add(MonsterRegistry.create("Slime4", 8, 3));
                levelMonsters.add(MonsterRegistry.create("Beast2", 14, 1));
                levelMonsters.add(MonsterRegistry.create("TRex", 14, 10));
            }
            case 5 -> {
                levelMonsters.add(MonsterRegistry.create("Mushroom", 8, 2));
                levelMonsters.add(MonsterRegistry.create("Beast", 2, 9));
                levelMonsters.add(MonsterRegistry.create("Eye", 14, 10));
            }
            case 6 -> {
                levelMonsters.add(MonsterRegistry.create("Mushroom2", 7, 9));
                levelMonsters.add(MonsterRegistry.create("Beast2", 14, 2));
                levelMonsters.add(MonsterRegistry.create("Spirit", 14, 10));
            }
            case 7 -> {
                levelMonsters.add(MonsterRegistry.create("TRex", 14, 1));
                levelMonsters.add(MonsterRegistry.create("Eye2", 1, 10));
                levelMonsters.add(MonsterRegistry.create("Bear", 8, 5));
            }
            case 8 -> {
                levelMonsters.add(MonsterRegistry.create("Slime", 3, 3));
                levelMonsters.add(MonsterRegistry.create("Bear", 14, 10));
                levelMonsters.add(MonsterRegistry.create("Spirit", 14, 1));
            }
            case 9 -> {
                levelMonsters.add(MonsterRegistry.create("TRex", 1, 10));
                levelMonsters.add(MonsterRegistry.create("Spirit", 14, 1));
                levelMonsters.add(MonsterRegistry.create("Beast", 7, 5));
                levelMonsters.add(MonsterRegistry.create("Bear", 8, 6));
            }
            case 10 -> {
                levelMonsters.add(MonsterRegistry.create("Eye", 1, 10));
                levelMonsters.add(MonsterRegistry.create("Eye2", 14, 1));
                levelMonsters.add(MonsterRegistry.create("Beast2", 7, 10));
                levelMonsters.add(MonsterRegistry.create("Bear", 8, 5));
                levelMonsters.add(MonsterRegistry.create("Spirit", 7, 2));
            }
            default -> {
                levelMonsters.add(MonsterRegistry.create("Slime", 8, 2));
            }
        }
        return levelMonsters;
    }

    // 產生一輪食物(每輪 6 種，每關要收集滿 24 種才算通關)
    public void spawnNextRoundOfFood() {
        Collections.shuffle(backupFoodPool);

        int spawnedThisTime = 0;
        List<FoodNode> putBackList = new ArrayList<>();
        List<FoodNode> successfullySpawnedNodes = new ArrayList<>();

        while (spawnedThisTime < 6 && !backupFoodPool.isEmpty()) {
            FoodNode node = backupFoodPool.remove(0);

            if (mapManager.getTileType(node.col, node.row) > 0 || mapManager.getItemType(node.col, node.row) > 0) {
                putBackList.add(node);
            } else {
                mapManager.setItemType(node.col, node.row, node.foodId);
                successfullySpawnedNodes.add(node);
                spawnedThisTime++;
            }
        }

        backupFoodPool.addAll(putBackList);
        this.currentRoundFoodCount = spawnedThisTime;

        javafx.application.Platform.runLater(() -> {
            double tileSize = mapManager.getTileSize();

            for (FoodNode node : successfullySpawnedNodes) {
                javafx.scene.image.ImageView foodView = ItemManager.createItemView(node.foodId, tileSize);

                if (foodView != null) {
                    foodView.setId("food_" + node.col + "_" + node.row);
                    mapGrid.add(foodView, node.col, node.row);
                } else {
                    System.out.println("The item is not found in the folder \"Food\"" + node.foodId);
                }
            }
        });
    }

    // 檢查食物收集狀態
    public void onPlayerEatFood() {
        totalFoodEatenInLevel++;
        currentRoundFoodCount--;

        controller.addScore(100);

        if (currentRoundFoodCount <= 0) {
            if (totalFoodEatenInLevel < 24) {
                spawnNextRoundOfFood();
            }
        }

        if (totalFoodEatenInLevel >= 24) {
            triggerLevelClear();
        }
    }

    // 處理關卡通關
    private void triggerLevelClear() {
        System.out.println("Level " + currentLevel + " is cleared. Congratulations!!!");
        if (currentLevel < MAX_LEVELS) {
            currentLevel++;
            totalFoodEatenInLevel = 0;
            currentRoundFoodCount = 0;
            controller.resetLevelScore();
            startNewLevel();
        } else {
            System.out.println("YOU WIN!!! You are the CHAMPION!!!");
            controller.stopGameLoop();
        }
    }

    // 初始化欲進行之關卡
    public void startNewLevel() {
        backupFoodPool.clear();

        loadCustomLevelData(currentLevel, mapManager);

        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 16; c++) {
                int foodId = mapManager.getItemType(c, r);
                if (foodId > 0) {
                    backupFoodPool.add(new FoodNode(c, r, foodId));
                    mapManager.setItemType(c, r, 0);
                }
            }
        }

        List<Monster> newMonsters = generateLevelMonsters(currentLevel);
        controller.setMonstersForLevel(newMonsters);

        controller.resetPlayerPosition(1, 1);

        spawnNextRoundOfFood();
    }

    // 玩家失敗
    public void triggerGameOver() {
        System.out.println("GG... Game Over... :(");
        controller.stopGameLoop();
    }
}