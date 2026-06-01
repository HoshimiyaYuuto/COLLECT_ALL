package program.intro_to_cs_lab_final_project;

import java.util.HashMap;
import java.util.Map;

public class MonsterRegistry {

    private static class MonsterTemplate {
        String imagePath;
        String defaultAiType;
        int defaultSpeed;
        boolean animVersion;

        MonsterTemplate(String imagePath, String defaultAiType, int defaultSpeed, boolean animVersion) {
            this.imagePath = imagePath;
            this.defaultAiType = defaultAiType;
            this.defaultSpeed = defaultSpeed;
            this.animVersion = animVersion;
        }
    }

    private static final Map<String, MonsterTemplate> registry = new HashMap<>();
    private static final String BASE_DIR = "/program/intro_to_cs_lab_final_project/Monster/";

    static {
        // 雜魚組
        registry.put("Slime",     new MonsterTemplate(BASE_DIR + "Slime/Slime.png", "RandomWalk", 3, true));
        registry.put("Slime2",    new MonsterTemplate(BASE_DIR + "Slime2/Slime2.png", "RandomWalk", 3, true));
        registry.put("Slime3",    new MonsterTemplate(BASE_DIR + "Slime3/Slime3.png", "RandomWalk", 3, true));
        registry.put("Slime4",    new MonsterTemplate(BASE_DIR + "Slime4/Slime4.png", "RandomWalk", 3, true));
        registry.put("Mushroom",  new MonsterTemplate(BASE_DIR + "Mushroom/mushroom.png", "SmartRandom", 3, true));
        registry.put("Mushroom2", new MonsterTemplate(BASE_DIR + "Mushroom2/mushroom2.png", "SmartRandom", 3, true));

        // 中階幹部組
        registry.put("Beast",     new MonsterTemplate(BASE_DIR + "Beast/Beast.png", "Chaser", 2, true));
        registry.put("Beast2",    new MonsterTemplate(BASE_DIR + "Beast2/Beast2.png", "Chaser", 2, true));
        registry.put("TRex",      new MonsterTemplate(BASE_DIR + "TRex/SpriteSheet.png", "LineOfSightChaser", 2, true));

        // 羆哥大王 & 內閣高層
        registry.put("Eye",       new MonsterTemplate(BASE_DIR + "Eye/Eye.png", "PathfindingChaser", 2, true));
        registry.put("Eye2",      new MonsterTemplate(BASE_DIR + "Eye2/Eye2.png", "PathfindingChaser", 2, true));
        registry.put("Bear",      new MonsterTemplate(BASE_DIR + "Bear/SpriteSheet.png", "AStarChaser", 2, false));
        registry.put("Spirit",    new MonsterTemplate(BASE_DIR + "Spirit/SpriteSheet.png", "Ghost", 4, true));
    }

    public static Monster create(String monsterName, int col, int row) {
        MonsterTemplate template = registry.get(monsterName);
        if (template == null) {
            System.err.println("The monster:" + monsterName + "does not in the Monster folder");
            template = registry.get("Slime");
        }

        Monster m = new Monster(template.imagePath, col, row, template.defaultAiType);
        m.setSpeed(template.defaultSpeed);
        m.setAnimVersion(template.animVersion); // 生產時將素材排版版本灌進怪物實體
        return m;
    }
}