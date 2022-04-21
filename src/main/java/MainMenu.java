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
            readFileText.setText("Please wait ... reading data");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            activities = Utility.readXls(file, professors, groups, faculty);
            if (activities!=null) {
                profCombo.getItems().clear();
                groupCombo.getItems().clear();
                readFileText.setText("Data read ok");
                for (Professor professor : professors) {
                    profCombo.getItems().add(professor.getIdProfesor()+" "+ professor.getName());
                }
                for (Group group:groups){
                    groupCombo.getItems().add(group.getIdGroup()+" "+group.getGroupName());
                }
                scenes =new Scenes(professors,activities,groups);
            }
            else readFileText.setText("Data read failure");
        });
        chooseProfesor.setOnAction(event -> {
            try{
                int indexSelected=profCombo.getSelectionModel().getSelectedIndex();
                int semester=semesterCombo.getSelectionModel().getSelectedItem();
                //scenes.professorsClassesScene(indexSelected,semester);
                scenes.professorsScheduleScene(indexSelected,semester);
            }
            catch (Exception ex){
                System.out.println("Can't generate scene");
            }
        });
        chooseYear.setOnAction(event -> {
            try{
                int yearSelected=yearCombo.getSelectionModel().getSelectedItem();
                int semester=semesterCombo.getSelectionModel().getSelectedItem();
                scenes.yearScheduleScene(yearSelected,semester);
            }
            catch (Exception ex){
                System.out.println("Can't generate scene");
            }
        });
/*        chooseGroup.setOnAction(event -> {
            try{
                int indexSelected=groupCombo.getSelectionModel().getSelectedIndex();
                new ScheduleGroup(groups.get(indexSelected),semesterCombo.getSelectionModel().getSelectedItem()).generateScene();
                System.out.println(groups.get(indexSelected)+" "+semesterCombo.getSelectionModel().getSelectedItem());
            }
            catch (Exception ex){
                System.out.println("Can't generate scene");
            }
        });

 */       saveData.setOnAction(event -> {
            try {
                Utility.saveData("data/savedfile", professors,groups,activities);
            }
            catch (Exception ex){
                System.out.println("Saving failed");
            }
        });
 /*       loadData.setOnAction(event -> {
            try {
                boolean ok=Utility.loadData("data/savedfile.prf",profesors,groups);
            }
            catch (Exception ex){
                System.out.println("Load failed");
            }
        });*/

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