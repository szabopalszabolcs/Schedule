import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainMenu {

    ArrayList<Professor> professors = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Activity> activities = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    Scenes scenes;

    public void createMainMenu() {

        String file = "data/MIN 2019 sept 30.xls";
        String faculty = "MI";
        Stage mainStage=new Stage();
        Integer[] years={1,2,3};
        VBox mainBox=new VBox();
        mainBox.setPadding(new Insets(10,10,10,10));
        mainBox.setSpacing(5);
        mainBox.setPrefSize(600,400);
        Scene mainScene=new Scene(mainBox);
        Button readFile=new Button("Read File");
        Label readFileText=new Label("");
        ComboBox<Integer> semesterCombo=new ComboBox<>();
        semesterCombo.getItems().add(1);
        semesterCombo.getItems().add(2);
        semesterCombo.setValue(1);
        ComboBox<String> profCombo=new ComboBox<>();
        Button chooseProfesor=new Button("Choose Prof");
        ComboBox<String> groupCombo=new ComboBox<>();
        Button chooseGroup=new Button("Choose Group");
        Button saveData=new Button("Save Data");
        Button loadData=new Button("Load Data");
        Button exit=new Button("Exit");
        ComboBox<Integer> yearCombo=new ComboBox<>();
        yearCombo.getItems().addAll(years);
        yearCombo.setValue(years[0]);
        Button chooseYear =new Button("Choose Year");

        readFile.setOnAction(event -> {
            professors.clear();
            groups.clear();
            activities = Utility.readXls(file, professors, groups, faculty);
            if (activities!=null) {
                profCombo.getItems().clear();
                groupCombo.getItems().clear();
                Utility.message("Datele au fost citite cu succes");
                for (Professor professor : professors) {
                    profCombo.getItems().add(professor.getName());
                }
                for (Group group:groups){
                    groupCombo.getItems().add(group.getGroupName());
                }
                scenes =new Scenes(professors,activities,groups);
            }
            else Utility.message("Citire date eșuată");
        });

        chooseProfesor.setOnAction(event -> {
            try{
                int indexSelected=profCombo.getSelectionModel().getSelectedIndex();
                int semester=semesterCombo.getSelectionModel().getSelectedItem();
                scenes.professorsScheduleScene(indexSelected,semester);
            }
            catch (Exception ex){
                Utility.message("Generare orar eșuată");
            }
        });

        chooseYear.setOnAction(event -> {
            try{
                int yearSelected=yearCombo.getSelectionModel().getSelectedItem();
                int semester=semesterCombo.getSelectionModel().getSelectedItem();
                scenes.yearScheduleScene(yearSelected,semester);
            }
            catch (Exception ex){
                Utility.message("Generare orar eșuată");
            }
        });

        saveData.setOnAction(event -> {
            try {
                Utility.saveData("data/savedfile", professors,groups,activities);
            }
            catch (Exception ex){
                Utility.message("Salvare date eșuată");
            }
        });

        loadData.setOnAction(event -> {
            try {
                String fileName="data/savedfile";
                activities=Utility.loadActivities(fileName+".act");
                professors=Utility.loadProfessors(fileName+".prf");
                groups=Utility.loadGroups(fileName+".grp");
                profCombo.getItems().clear();
                groupCombo.getItems().clear();
                Utility.message("Datele au fost citite cu succes");
                for (Professor professor : professors) {
                    profCombo.getItems().add(professor.getName());
                }
                for (Group group:groups){
                    groupCombo.getItems().add(group.getGroupName());
                }
                scenes = new Scenes(professors,activities,groups);
            }
            catch (Exception ex){
                Utility.message("Citire date eșuată");
            }
        });

        exit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });


        mainBox.getChildren().addAll(readFile,readFileText,semesterCombo,profCombo,chooseProfesor,yearCombo,chooseYear,groupCombo,chooseGroup,saveData,loadData,exit);
        mainStage.setScene(mainScene);
        mainStage.setOnCloseRequest(Event::consume);
        mainStage.setTitle("Main menu");
        mainStage.show();

    }

}