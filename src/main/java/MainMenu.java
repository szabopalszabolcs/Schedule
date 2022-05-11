import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MainMenu {

    ArrayList<Professor> professors = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Activity> activities = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    int years;
    Scenes scenes;

    public void createMainMenu() {

        String file = "data/MIN 2019 sept 30.xls";
        String faculty = "MI";

        Stage mainStage=new Stage();

        GridPane mainGrid=new GridPane();
        mainGrid.setPadding(new Insets(20,20,20,20));
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setVgap(10);
        mainGrid.setHgap(10);

        Scene mainScene=new Scene(mainGrid);

        Button readFile=new Button("Citește fișierul stat de funcțiuni");
        readFile.setPrefSize(300,30);

        ComboBox<Integer> semesterCombo=new ComboBox<>();
        semesterCombo.getItems().add(1);
        semesterCombo.getItems().add(2);
        semesterCombo.setPrefSize(300,30);

        ComboBox<String> profCombo=new ComboBox<>();
        profCombo.setPrefSize(300,30);

        Button chooseProfesor=new Button("Alege profesorul");
        chooseProfesor.setPrefSize(300,30);

        ComboBox<String> groupCombo=new ComboBox<>();
        groupCombo.setPrefSize(300,30);

        Button chooseGroup=new Button("Alege grupa");
        chooseGroup.setPrefSize(300,30);

        Button saveData=new Button("Salvare date");
        saveData.setPrefSize(300,30);

        Button loadData=new Button("Încărcare date");
        loadData.setPrefSize(300,30);

        Button exit=new Button("Închidere");
        exit.setPrefSize(300,30);

        ComboBox<Integer> yearCombo=new ComboBox<>();
        yearCombo.getItems().add(1);
        yearCombo.setPrefSize(300,30);

        Button chooseYear =new Button("Alege anul de studiu");
        chooseYear.setPrefSize(300,30);

        Button semestru=new Button("Semestru");
        semestru.setPrefSize(300,30);

        readFile.setOnAction(event -> {
            professors.clear();
            groups.clear();
            activities = Utility.readXls(file, professors, groups, faculty);
            if (activities!=null)
                years = Utility.maxYear(activities);
            else
                years=0;
            if (activities!=null) {
                profCombo.getItems().clear();
                groupCombo.getItems().clear();
                yearCombo.getItems().clear();

                for (Professor professor : professors) {
                    profCombo.getItems().add(professor.getName());
                }
                if (professors.get(0)!=null)
                    profCombo.setValue(professors.get(0).getName());
                for (Group group:groups){
                    groupCombo.getItems().add(group.getGroupName());
                }
                if (groups.get(0)!=null)
                    groupCombo.setValue(groups.get(0).getGroupName());
                for (int i=0;i<years;i++) {
                    yearCombo.getItems().add(i+1);
                }
                if (years>0)
                    yearCombo.setValue(1);
                semesterCombo.setValue(1);
                scenes =new Scenes(professors,activities,groups,rooms);
                Utility.message("Datele au fost citite cu succes");
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
            if (Utility.saveData("data/savedfile", professors,groups,activities))
                Utility.message("Salvare reușită");
        });

        loadData.setOnAction(event -> {
                String fileName="data/savedfile";
                activities=Utility.loadActivities(fileName+".act");
                professors=Utility.loadProfessors(fileName+".prf");
                groups=Utility.loadGroups(fileName+".grp");
                rooms=Utility.loadRooms(fileName+".rm");
                years = Utility.maxYear(activities);
                if (activities!=null) {
                    profCombo.getItems().clear();
                    groupCombo.getItems().clear();
                    yearCombo.getItems().clear();

                    for (Professor professor : professors) {
                        profCombo.getItems().add(professor.getName());
                    }
                    if (professors.get(0)!=null)
                        profCombo.setValue(professors.get(0).getName());
                    for (Group group:groups){
                        groupCombo.getItems().add(group.getGroupName());
                    }
                    if (groups.get(0)!=null)
                        groupCombo.setValue(groups.get(0).getGroupName());
                    for (int i=0;i<years;i++) {
                        yearCombo.getItems().add(i+1);
                    }
                    if (years>0)
                        yearCombo.setValue(1);
                    semesterCombo.setValue(1);
                    scenes =new Scenes(professors,activities,groups,rooms);
                    Utility.message("Datele au fost încărcate cu succes");
                }
                else Utility.message("Încărcare date eșuată");
        });

        exit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        mainGrid.add(readFile,1,1);
        mainGrid.add(saveData,2,2);
        mainGrid.add(loadData,1,2);
        mainGrid.add(semesterCombo,1,3);
        mainGrid.add(semestru,2,3);
        mainGrid.add(profCombo,1,4);
        mainGrid.add(chooseProfesor,2,4);
        mainGrid.add(yearCombo,1,5);
        mainGrid.add(chooseYear,2,5);
        mainGrid.add(groupCombo,1,6);
        mainGrid.add(chooseGroup,2,6);
        mainGrid.add(exit,2,7);

        mainStage.setScene(mainScene);
        mainStage.setOnCloseRequest(Event::consume);
        mainStage.setTitle("Meniu principal");
        mainStage.show();

    }

}