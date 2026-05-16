module program.intro_to_cs_lab_final_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens program.intro_to_cs_lab_final_project to javafx.fxml;
    exports program.intro_to_cs_lab_final_project;
}