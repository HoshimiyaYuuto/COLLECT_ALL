package program.intro_to_cs_lab_final_project;
//遊戲主入口
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String menuPath = "/program/intro_to_cs_lab_final_project/GameStage.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(menuPath));

        primaryStage.setTitle("COLLECT ALL - PLAIN EDITION");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }
}
