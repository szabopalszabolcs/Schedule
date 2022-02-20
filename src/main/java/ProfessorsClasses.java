import javafx.geometry.Insets;
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

public class ProfessorsClasses {

    private final int HOURS=14,DAYS=6;
    ArrayList<Activity> activities;
    ArrayList<Group> groups;
    private IndexedLabel draggingLabel;
    private final Profesor profesor;
    private final int semester;
    private final DataFormat labelFormat;

    public ProfessorsClasses(Profesor profesor, int semester, ArrayList<Activity> activities, ArrayList<Group> groups) {
        this.profesor = profesor;
        this.semester = semester;
        this.groups=groups;
        this.activities=activities;
        DataFormat dataFormat = DataFormat.lookupMimeType(profesor.getName()+semester);
        if (dataFormat==null)
            labelFormat=new DataFormat(profesor.getName()+semester);
        else
            labelFormat=dataFormat;
    }

    public void generateScene(){

        Stage classesStage = new Stage();
        ScrollPane classesRoot=new ScrollPane();
        GridPane classesGrid=new GridPane();
        int nrActivities=0;
        for (int activity:profesor.getActivitiesOfProfesor()) {
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
        for (int i=0;i<profesor.getActivitiesOfProfesor().length;i++) {
            Activity currentActivity = activities.get(profesor.getActivitiesOfProfesor()[i]);
            if (currentActivity.getSemester() == semester && !onSchedule(profesor.getActivitiesOfProfesor()[i])) {
                IndexedLabel lbl = Utility.createLabel(currentActivity, profesor, groups);
                classesArray[count] = new StackPane();
                classesArray[count].setPrefSize(100, currentActivity.getTime()*50);
                classesArray[count].setAlignment(Pos.TOP_CENTER);
                addDropHandlingClasses(classesArray[count]);
                classesArray[count].getChildren().add(lbl);
                classesGrid.add(classesArray[count], count % sqr, count / sqr);
                dragTextArea(lbl);
                count++;
            }
        }
        System.out.println("count="+count+" sqr="+sqr);
        for (int i=count;i<sqr*sqr;i++){
            StackPane pane=new StackPane();
            pane.setPrefSize(100,50);
            pane.setAlignment(Pos.TOP_CENTER);
            addDropHandlingClasses(pane);
            classesGrid.add(pane,i%sqr,i/sqr);
        }
        classesGrid.setGridLinesVisible(true);
        classesRoot.setContent(classesGrid);
        classesRoot.setPadding(new Insets(10,10,10,10));
        classesRoot.autosize();
        Scene classesScene=new Scene(classesRoot);
        classesStage.setScene(classesScene);
        classesStage.setTitle(profesor.getName()+" semestrul "+semester);
        classesStage.show();

    }

    public boolean onSchedule(int activity) {
        for (int i=0;i<HOURS;i++)
            for (int j=0;j<DAYS;j++)
                if (activity==profesor.getActivityProfesor(semester,i,j))
                    return true;
        return false;
    }

    private void dragTextArea(IndexedLabel ta) {
        ta.setOnDragDetected(e -> {
            Dragboard db = ta.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(ta.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(labelFormat, " ");
            db.setContent(cc);
            draggingLabel = ta;
        });
    }

    private void addDropHandlingClasses(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&draggingLabel!=null&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)) {
                ((Pane)draggingLabel.getParent()).getChildren().remove(draggingLabel);
                pane.getChildren().add(draggingLabel);
                Activity activity=activities.get(draggingLabel.getActivity());
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++)
                        try{
                            if (profesor.getActivityProfesor(semester-1,i,j)>-1&&
                                    activity==activities.get(profesor.getActivityProfesor(semester-1,i,j)))
                                profesor.setActivityProfesor(semester-1,i,j,-1);
                        }
                        catch (Exception ex){
                            System.out.println("s="+semester+" i="+i+" j="+j);
                        }
                e.setDropCompleted(true);
                draggingLabel = null;
            }
        });
    }

}