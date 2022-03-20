import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Objects;

public class Scenes {

    private final int HOURS=7,DAYS=12;
    final Background HEADER=new Background(new BackgroundFill(Color.BEIGE,CornerRadii.EMPTY,Insets.EMPTY));
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

    public void professorsClassesScene(int professorId, int semester) {

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
        int sqr,multiplier=0;
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
        for (int i=sqr-1;i<sqr+1;i++) {
            if (sqr*i>count) {
                multiplier=i;
                break;
            }
        }
        if (multiplier==0) multiplier=sqr;
        for (int i=count;i<sqr*multiplier;i++) {
            StackPane pane = new StackPane();
            pane.setMinSize(40, 40);
            pane.setAlignment(Pos.CENTER);
            addDropHandlingClasses(pane,professorId);
            classesGrid.add(pane, i % sqr, i / sqr);
        }
        classesGrid.setGridLinesVisible(true);
        classesGrid.setPadding(new Insets(10,10,10,10));
        classesRoot.setContent(classesGrid);
        classesRoot.setMinWidth(300);
        classesRoot.autosize();
        Scene classesScene=new Scene(classesRoot);
        classesStage.setScene(classesScene);
        classesStage.setTitle(professor.getName()+" semestrul "+semester);
        classesStage.show();

    }

    public void professorsScheduleScene(int professorId, int semester) {

        Professor professor = professors.get(professorId);
        Stage scheduleStage=new Stage();
        StackPane scheduleRoot=new StackPane();
        GridPane scheduleGrid=new GridPane();
        StackPane[][] scheduleMatrix=new StackPane[HOURS+1][DAYS+1];
        Scene scheduleScene=new Scene(scheduleRoot);

        for (int i=0;i<HOURS+1;i++)
            for (int j=0;j<DAYS+1;j++){
                scheduleMatrix[i][j]=new StackPane();
                scheduleMatrix[i][j].setPrefSize(80,40);
                scheduleMatrix[i][j].setAlignment(Pos.TOP_CENTER);
                if (j==0 || i==0) scheduleMatrix[i][j].setBackground(HEADER);
                else {
                    addDropHandlingProfSchedule(scheduleMatrix[i][j],professorId);
                    int presentActivityId=professor.getActivityProfesor(semester,i-1,j-1);
                    if (presentActivityId!=-1) {
                        Activity presentActivity=activities.get(presentActivityId);
                        IndexedLabel lbl=Utility.createSmallLabel(presentActivity, professor, groups);
                        scheduleMatrix[i][j].getChildren().add(lbl);
                        dragTextArea(lbl);
                        System.out.println("Add label " + activities.get(professor.getActivityProfesor(semester,i - 1, j - 1)).getSubject() + " " + i + "," + j);
                    }
                }
                scheduleGrid.add(scheduleMatrix[i][j], i, j);
            }

        scheduleGrid.setGridLinesVisible(true);
        scheduleGrid.setAlignment(Pos.CENTER);
        ScrollPane scrollPane=new ScrollPane();
        scrollPane.setContent(scheduleGrid);
        scheduleRoot.getChildren().add(scrollPane);
        scheduleRoot.setPadding(new Insets(10,10,10,10));
        scheduleRoot.autosize();
        StackPane.setAlignment(scheduleGrid,Pos.TOP_CENTER);
        scheduleStage.setScene(scheduleScene);
        scheduleStage.setTitle(professor.getName()+" semestrul "+semester);
        scheduleStage.show();

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
            if (draggingLabel.getProfessorId()==professorId) {
                try {
                    ObservableList<Node> childrens;
                    Pane parentOfLabel = (Pane) draggingLabel.getParent();
                    Pane actualPane;
                    IndexedLabel actualLabel;
                    GridPane gridOfOrigin = (GridPane) parentOfLabel.getParent();
                    childrens = gridOfOrigin.getChildren();
                    for (Node node:childrens){
                        if (node.getClass()==pane.getClass()) {
                            actualPane = (StackPane) node;
                            if (!actualPane.getChildren().isEmpty()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                catch (Exception exception) {

                }
                Activity activity=activities.get(draggingLabel.getActivityId());
                Professor professor=professors.get(professorId);
                int semester=activity.getSemester();
                draggingLabel.setPrefSize(80,40*activity.getTime());
                pane.getChildren().add(draggingLabel);
                e.setDropCompleted(true);
                dragTextArea(draggingLabel);
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfesor(semester,i,j))
                            professor.setActivityProfesor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }
                }
        });
    }

    private void addDropHandlingProfSchedule(StackPane pane, int professorId) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&draggingLabel!=null&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            Activity activity=activities.get(draggingLabel.getActivityId());
            Professor professor=professors.get(professorId);
            int semester=activity.getSemester();
            if (db.hasContent(labelFormat)) {
                int     row= GridPane.getRowIndex(pane),
                        col= GridPane.getColumnIndex(pane),
                        time = activity.getTime();
                if (isMovableToProfSchedule(col,row,time,activity)){
                    Pane parentOfLabel=(Pane) draggingLabel.getParent();
                    parentOfLabel.getChildren().clear();
                    moveToProfSchedule(pane,col,row,activity);
                    draggingLabel = null;
                    e.setDropCompleted(true);
                }
            }
        });
    }

    private boolean isMovableToProfSchedule(int col, int row, int time, Activity activity) {
        int semester = activity.getSemester();
        Professor professor = professors.get(activity.getProfessorId());
        if (row == 0 || col == 0) {
            return false;
        }
        if (col + (time - 1) / 2 > 7) {
            return false;
        }
        int add;
        int X,Y;
        switch (time%2) {
            case 1:
                if (row % 2 == 0)
                    add=-1;
                else
                    add=1;
                for (int t=0;t<time;t++) {
                    X=row+(t%2)*add;
                    Y=col+(t+1)/2;
                    if (professor.getActivityProfesor(semester,Y-1, X-1) != -1) {
                        return false;
                    };
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester,Y-1, X-1) != -1 ) {
                            return false;
                        };
                }
                break;
            case 0:
                if (row % 2 == 0)
                    row--;
                for (int t=0;t<time;t++) {
                    X=row+t%2;
                    Y=col+t/2;
                    if (professor.getActivityProfesor(semester,Y-1, X-1) != -1) {
                        return false;
                    };
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester,Y-1, X-1) != -1 ) {
                            return false;
                        };
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void moveToProfSchedule(StackPane pane,int col, int row,Activity activity) {

        StackPane pane2,actualPane;
        GridPane grid;
        IndexedLabel actualLabel;
        IndexedLabel[] labels;
        int add=1;
        ObservableList<Node> childrens;
        int X,Y,nodeX,nodeY;

        Professor professor=professors.get(activity.getProfessorId());
        int semester=activity.getSemester();
        int time=activity.getTime();
        switch (time%2) {
            case 1:
                grid=(GridPane) pane.getParent();
                labels=new IndexedLabel[time];
                for (int i=0;i<time;i++) {
                    labels[i] = Utility.createLabel(activity, professor, groups);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node:childrens){
                    if (node.getClass()==pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                            if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfesor(semester,i,j))
                            professor.setActivityProfesor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }

                if (row % 2 == 0)
                    add=-1;
                else
                    add=1;
                for (int t=0;t<time;t++) {
                    X=row+(t%2)*add;
                    Y=col+(t+1)/2;
                    System.out.println(t);
                    childrens = grid.getChildren();
                    for (Node node:childrens) {
                        nodeX=GridPane.getRowIndex(node);
                        nodeY=GridPane.getColumnIndex(node);
                        if ( nodeX == X  && nodeY == Y ) {
                            pane2 = (StackPane) node;
                            pane2.getChildren().add(labels[t]);
                        }
                        if ( nodeX >= X && nodeY >= Y ) {
                            break;
                        }
                    }
                    professor.setActivityProfesor(semester,Y-1, X-1, activity.getIdActivity());
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester,Y-1, X-1, activity.getIdActivity());
                }
                break;
            case 0:
                grid=(GridPane) pane.getParent();
                labels=new IndexedLabel[time];
                for (int i=0;i<time;i++) {
                    labels[i] = Utility.createLabel(activity, professor, groups);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node:childrens){
                    if (node.getClass()==pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                            if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfesor(semester,i,j))
                            professor.setActivityProfesor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }
                if (row % 2 == 0)
                    row--;
                for (int t=0;t<time;t++) {
                    X=row+t%2;
                    Y=col+t/2;
                    System.out.println(t);
                    childrens = grid.getChildren();
                    for (Node node:childrens) {
                        nodeX=GridPane.getRowIndex(node);
                        nodeY=GridPane.getColumnIndex(node);
                        if ( nodeX == X  && nodeY == Y ) {
                            pane2 = (StackPane) node;
                            pane2.getChildren().add(labels[t]);
                        }
                        if ( nodeX >= X && nodeY >= Y ) {
                            break;
                        }
                    }
                    professor.setActivityProfesor(semester,Y-1, X-1, activity.getIdActivity());
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester,Y-1, X-1, activity.getIdActivity());
                }
                break;
            default:
                break;
        }
    }
}