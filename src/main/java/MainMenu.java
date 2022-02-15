import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MainMenu {

    ArrayList<Profesor> profesors = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Activity> activities = new ArrayList<>();

    public void createMainMenu() {

        String file = "data/MIN 2019 sept 30.xls";
        String faculty = "MI";

        Stage mainStage=new Stage();

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
        semesterCombo.setValue(0);
        ComboBox<String> profCombo=new ComboBox<>();
        Button chooseProfesor=new Button("Choose Prof");
        ComboBox<String> groupCombo=new ComboBox<>();
        Button chooseGroup=new Button("Choose Group");
        Button saveData=new Button("Save Data");
        Button loadData=new Button("Load Data");
        readFile.setOnAction(event -> {
            readFileText.setText("Please wait ... reading data");
            activities = Utility.readXls(file, profesors, groups, faculty);
            if (activities!=null) {
                profCombo.getItems().clear();
                groupCombo.getItems().clear();
                readFileText.setText("Data read ok");
                for (Profesor profesor : profesors) {
                    profCombo.getItems().add(profesor.getIdProfesor()+" "+profesor.getName());
                }
                for (Group group:groups){
                    groupCombo.getItems().add(group.getIdGroup()+" "+group.getGroupName());
                }
                //for (Activity a:activities){ System.out.println(a);}
            }
            else readFileText.setText("Data read failure");
        });
        chooseProfesor.setOnAction(event -> {
            try{
                int indexSelected=profCombo.getSelectionModel().getSelectedIndex();
                ScheduleProfesor profSchedule=new ScheduleProfesor(profesors.get(indexSelected),semesterCombo.getSelectionModel().getSelectedItem(),activities,groups);
                profSchedule.generateScene();
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
                boolean ok=Utility.saveData("data/savedfile",profesors,groups,activities);
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

        mainBox.getChildren().addAll(readFile,readFileText,semesterCombo,profCombo,chooseProfesor,groupCombo,chooseGroup,saveData,loadData);
        mainStage.setScene(mainScene);
        mainStage.setTitle("Main menu");
        mainStage.show();

    }

}