import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Scenes {

    private final int HOURS=14,DAYS=6;
    ArrayList<Activity> activities;
    ArrayList<Group> groups;
    ArrayList<Professor> professors;
    public static IndexedLabel draggingLabel;
    private final DataFormat labelFormat;

    public Scenes(ArrayList<Professor> professors, ArrayList<Activity> activities, ArrayList<Group> groups) {
        this.professors = professors;
        this.groups=groups;
        this.activities=activities;
        DataFormat dataFormat = DataFormat.lookupMimeType("Unitbv");
        if (dataFormat==null)
            labelFormat=new DataFormat("Unitbv");
        else
            labelFormat=dataFormat;
    }

    public void professorsClassesScene(int professorId, int semester){

        Professor professor = professors.get(professorId);
        Stage classesStage = new Stage();
        ScrollPane classesRoot=new ScrollPane();
        GridPane classesGrid=new GridPane();
        int nrActivities=0;
        for (int activity: professor.getActivitiesOfProfesor()) {
            if (activities.get(activity).getSemester() == semester) {
                nrActivities++;
            }
        }
        StackPane[] classesArray=new StackPane[nrActivities];
        int sqr;
        if (Math.floor((Math.sqrt(nrActivities)))==Math.sqrt(nrActivities)) {
            sqr = (int) Math.floor(Math.sqrt(nrActivities));
        }
        else {
            sqr = (int) Math.floor(Math.sqrt(nrActivities)) + 1;
        }
        int count=0;
        for (int i = 0; i< professor.getActivitiesOfProfesor().length; i++) {
            Activity currentActivity = activities.get(professor.getActivitiesOfProfesor()[i]);
            if (currentActivity.getSemester() == semester && !onSchedule(professor.getActivitiesOfProfesor()[i],professorId,semester)) {
                IndexedLabel lbl = Utility.createLabel(currentActivity, professor, groups);
                classesArray[count] = new StackPane();
                classesArray[count].setMinSize(40,40);
                classesArray[count].setAlignment(Pos.CENTER);
                addDropHandlingClasses(classesArray[count],professorId);
                classesArray[count].getChildren().add(lbl);
                classesGrid.add(classesArray[count], count % sqr, count / sqr);
                dragTextArea(lbl);
                count++;
            }
        }
        System.out.println("count="+count+" sqr="+sqr);
        if (sqr*sqr-count<sqr) {
            for (int i=count;i<sqr*sqr;i++) {
                StackPane pane = new StackPane();
                pane.setMinSize(40, 40);
                pane.setAlignment(Pos.CENTER);
                addDropHandlingClasses(pane,professorId);
                classesGrid.add(pane, i % sqr, i / sqr);
            }
        }
        classesGrid.setGridLinesVisible(true);
        classesRoot.setContent(classesGrid);
        classesRoot.autosize();
        Scene classesScene=new Scene(classesRoot);
        classesStage.setScene(classesScene);
        classesStage.setTitle(professor.getName()+" semestrul "+semester);
        classesStage.show();

    }

    public boolean onSchedule(int activity, int professorId,int semester) {
        Professor professor = professors.get(professorId);
        for (int i=0;i<HOURS;i++)
            for (int j=0;j<DAYS;j++)
                if (activity== professor.getActivityProfesor(semester,i,j))
                    return true;
        return false;
    }

    private void dragTextArea(IndexedLabel ta) {
        ta.setOnDragDetected(e -> {
            Dragboard db = ta.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(ta.snapshot(null,null));
            ClipboardContent content = new ClipboardContent();
            content.put(labelFormat,"");
            db.setContent(content);
            draggingLabel=ta;
        });
    }

    private void addDropHandlingClasses(StackPane pane,int professorId) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (draggingLabel.getProfessorId()==professorId) {
                ((Pane) draggingLabel.getParent()).getChildren().remove(draggingLabel);
                pane.getChildren().add(draggingLabel);
                e.setDropCompleted(true);
                dragTextArea(draggingLabel);
                e.setDropCompleted(true);
            }
        });
    }
}