import com.sun.javafx.stage.StageHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;

public class Scenes {

    private final int HOURS=7,DAYS=12;
    ArrayList<Activity> activities;
    ArrayList<Group> groups;
    ArrayList<Professor> professors;
    ArrayList<Room> rooms;
    public static IndexedLabel draggingLabel;
    private final DataFormat labelFormat;
    boolean dropped = false;

    public Scenes(ArrayList<Professor> professors, ArrayList<Activity> activities, ArrayList<Group> groups, ArrayList<Room> rooms) {
        this.professors = professors;
        this.groups=groups;
        this.activities=activities;
        this.rooms=rooms;
        DataFormat dataFormat = DataFormat.lookupMimeType("Unitbv");
        if (dataFormat==null)
            labelFormat=new DataFormat("Unitbv");
        else
            labelFormat=dataFormat;
    }

    public boolean onScheduleOfProfessor(int activityId, int professorId, int semester) {
        Professor professor = professors.get(professorId);
        for (int i=0;i<HOURS;i++)
            for (int j=0;j<DAYS;j++)
                if (activityId== professor.getActivityProfessor(semester,i,j))
                    return true;
        return false;
    }

    public boolean onScheduleOfGroup(int activityId, int groupId, int semester) {
        Group group = groups.get(groupId);
        for (int i=0;i<HOURS;i++)
            for (int j=0;j<DAYS;j++)
                if (activityId==group.getActivityGroup(semester,i,j))
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
        ta.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount()==2) {
                    for (Stage stage:StageHelper.getStages()) {
                        if (stage.getTitle().contains(professors.get(ta.getProfessorId()).getName())) {
                            stage.close();
                        }
                    }
                    professorsScheduleScene(ta.getProfessorId(), activities.get(ta.getActivityId()).getSemester());
                }
            }
        });
    }

    public void professorsScheduleScene(int professorId, int semester) {

        Professor professor = professors.get(professorId);
        Stage scheduleStage=new Stage();
        HBox horizontalBox=new HBox();
        GridPane classesGrid=new GridPane();
        GridPane scheduleGrid=new GridPane();
        StackPane[][] scheduleMatrix=new StackPane[HOURS+1][DAYS+1];

        String[] ore={"Zi \\ Ora","8-9,50","10-11,50","12-13,50","14-15,50","16-17,50","18-19,50","20-21,50"};
        String[] zile={"Luni","Marti","Miercuri","Joi","Vineri","Sambata"};

        for (int i=0;i<HOURS+1;i++) {
            scheduleMatrix[i][0]=new StackPane();
            scheduleMatrix[i][0].setPrefSize(80,40);
            scheduleMatrix[i][0].setStyle("-fx-border-color:black; -fx-background-color:beige; -fx-padding:5");
            scheduleMatrix[i][0].getChildren().add(new Label((ore[i])));
            scheduleGrid.add(scheduleMatrix[i][0], i, 0);
        }

        for (int j=1;j<DAYS/2+1;j++) {
            scheduleMatrix[0][j]=new StackPane();
            scheduleMatrix[0][j].setStyle("-fx-border-color:black; -fx-background-color:beige; -fx-padding:5");
            scheduleMatrix[0][j].getChildren().add(new Label((zile[j-1])));
            scheduleGrid.add(scheduleMatrix[0][j], 0, j*2-1,1,2);
            System.out.println(j);
        }


        for (int i=1;i<HOURS+1;i++) {
            for (int j=1;j<DAYS+1;j++){
                scheduleMatrix[i][j]=new StackPane();
                scheduleMatrix[i][j].setStyle("-fx-border-color:black");
                scheduleMatrix[i][j].setPrefSize(80,40);
                scheduleMatrix[i][j].setAlignment(Pos.TOP_CENTER);
                addDropHandlingProfSchedule(scheduleMatrix[i][j]);
                int presentActivityId=professor.getActivityProfessor(semester,i-1,j-1);
                if (presentActivityId!=-1) {
                    Activity presentActivity=activities.get(presentActivityId);
                    IndexedLabel lbl=Utility.createProfLabel(presentActivity, professor, groups,rooms);
                    scheduleMatrix[i][j].getChildren().add(lbl);
                    dragTextArea(lbl);
                    System.out.println("Add label " + activities.get(professor.getActivityProfessor(semester,i - 1, j - 1)).getSubject() + " " + i + "," + j);
                }
                scheduleGrid.add(scheduleMatrix[i][j], i, j);
            }
        }

        int nrActivities=0;
        for (int activity: professor.getActivitiesOfProfessor()) {
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
        for (int i = 0; i< professor.getActivitiesOfProfessor().length; i++) {
            Activity currentActivity = activities.get(professor.getActivitiesOfProfessor()[i]);
            if (currentActivity.getSemester() == semester) {
                classesArray[count] = new StackPane();
                classesArray[count].setPrefWidth(80);
                classesArray[count].setMinHeight(40);
                classesArray[count].setAlignment(Pos.CENTER);
                classesArray[count].setStyle("-fx-border-color:black");
                addDropHandlingClasses(classesArray[count], professorId);
                classesGrid.add(classesArray[count], count % sqr, count / sqr);
                if (!onScheduleOfProfessor(professor.getActivitiesOfProfessor()[i], professorId, semester)) {
                    IndexedLabel lbl = Utility.createProfLabel(currentActivity, professor, groups,rooms);
                    classesArray[count].getChildren().add(lbl);
                    dragTextArea(lbl);
                }
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
            pane.setPrefWidth(80);
            pane.setMinHeight(40);
            pane.setAlignment(Pos.CENTER);
            addDropHandlingClasses(pane,professorId);
            classesGrid.add(pane, i % sqr, i / sqr);
        }

        horizontalBox.getChildren().addAll(scheduleGrid,classesGrid);
        horizontalBox.setPadding(new Insets(20,20,20,20));
        horizontalBox.setAlignment(Pos.CENTER);
        horizontalBox.setSpacing(20);
        Scene scheduleScene=new Scene(horizontalBox);
        scheduleStage.setScene(scheduleScene);
        scheduleStage.setTitle(professor.getName()+" semestrul "+semester);
        scheduleStage.show();

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
                    StackPane parentOfLabel = (StackPane) draggingLabel.getParent();
                    StackPane actualPane;
                    IndexedLabel actualLabel;
                    GridPane gridOfOrigin = (GridPane) parentOfLabel.getParent();
                    childrens = gridOfOrigin.getChildren();
                    for (Node node:childrens){
                        if (node.getClass()==pane.getClass()) {
                            actualPane = (StackPane) node;
                            if (!actualPane.getChildren().isEmpty()) {
                                if (actualPane.getChildren().get(0).getClass()==IndexedLabel.class) {
                                    actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                    if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                        ((StackPane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception exception) {
                    System.out.println("Removing not succeded");
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
                        if (activity.getIdActivity()==professor.getActivityProfessor(semester,i,j))
                            professor.setActivityProfessor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }
                }
        });
    }

    private void addDropHandlingProfSchedule(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&draggingLabel!=null&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            Activity activity=activities.get(draggingLabel.getActivityId());
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
                    if (professor.getActivityProfessor(semester,Y-1, X-1) != -1) {
                        return false;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, Y - 1, X - 1) != -1) {
                            return false;
                        }
                    }
                }
                break;
            case 0:
                if (row % 2 == 0)
                    row--;
                for (int t=0;t<time;t++) {
                    X=row+t%2;
                    Y=col+t/2;
                    if (professor.getActivityProfessor(semester,Y-1, X-1) != -1) {
                        return false;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, Y - 1, X - 1) != -1) {
                            return false;
                        }
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void moveToProfSchedule(StackPane pane,int col, int row,Activity activity) {

        StackPane secondPane, actualPane;
        GridPane grid;
        IndexedLabel actualLabel;
        IndexedLabel[] labels;
        int add;
        ObservableList<Node> childrens;
        int X, Y, nodeX, nodeY;

        Professor professor = professors.get(activity.getProfessorId());
        int semester = activity.getSemester();
        int time = activity.getTime();
        switch (time % 2) {
            case 1:
                grid = (GridPane) pane.getParent();
                labels = new IndexedLabel[time];
                for (int i = 0; i < time; i++) {
                    labels[i] = Utility.createProfLabel(activity, professor, groups, rooms);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node : childrens) {
                    if (node.getClass() == pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass() == draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < HOURS; i++)
                    for (int j = 0; j < DAYS; j++) {
                        if (activity.getIdActivity() == professor.getActivityProfessor(semester, i, j))
                            professor.setActivityProfessor(semester, i, j, -1);
                        for (int k = 0; k < Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity() == groups.get(activity.getGroupsId()[k]).getActivityGroup(semester, i, j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester, i, j, -1);
                    }

                if (row % 2 == 0)
                    add = -1;
                else
                    add = 1;
                for (int t = 0; t < time; t++) {
                    X = row + (t % 2) * add;
                    Y = col + (t + 1) / 2;
                    System.out.println(t);
                    childrens = grid.getChildren();
                    for (Node node : childrens) {
                        nodeX = GridPane.getRowIndex(node);
                        nodeY = GridPane.getColumnIndex(node);
                        if (nodeX == X && nodeY == Y) {
                            secondPane = (StackPane) node;
                            secondPane.getChildren().add(labels[t]);
                        }
                        if (nodeX >= X && nodeY >= Y) {
                            break;
                        }
                    }
                    professor.setActivityProfessor(semester, Y - 1, X - 1, activity.getIdActivity());
                    for (int j = 0; j < activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester, Y - 1, X - 1, activity.getIdActivity());
                }
                break;
            case 0:
                grid = (GridPane) pane.getParent();
                labels = new IndexedLabel[time];
                for (int i = 0; i < time; i++) {
                    labels[i] = Utility.createProfLabel(activity, professor, groups, rooms);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node : childrens) {
                    if (node.getClass() == pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass() == draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < HOURS; i++)
                    for (int j = 0; j < DAYS; j++) {
                        if (activity.getIdActivity() == professor.getActivityProfessor(semester, i, j))
                            professor.setActivityProfessor(semester, i, j, -1);
                        for (int k = 0; k < Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity() == groups.get(activity.getGroupsId()[k]).getActivityGroup(semester, i, j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester, i, j, -1);
                    }
                if (row % 2 == 0)
                    row--;
                for (int t = 0; t < time; t++) {
                    X = row + t % 2;
                    Y = col + t / 2;
                    System.out.println(t);
                    childrens = grid.getChildren();
                    for (Node node : childrens) {
                        nodeX = GridPane.getRowIndex(node);
                        nodeY = GridPane.getColumnIndex(node);
                        if (nodeX == X && nodeY == Y) {
                            secondPane = (StackPane) node;
                            secondPane.getChildren().add(labels[t]);
                        }
                        if (nodeX >= X && nodeY >= Y) {
                            break;
                        }
                    }
                    professor.setActivityProfessor(semester, Y - 1, X - 1, activity.getIdActivity());
                    for (int j = 0; j < activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester, Y - 1, X - 1, activity.getIdActivity());
                }
                break;
            default:
                break;
        }

        for (Stage stage : StageHelper.getStages()) {
            if (stage.getTitle().contains("Orar anul " + activity.getYearOfStudy())) {
                stage.close();
                yearScheduleScene(activity.getYearOfStudy(), semester);
            }
            for (int group : activity.getGroupsId()) {
                if (stage.getTitle().contains(groups.get(group).getGroupName())) {
                    stage.close();
                    groupsScheduleScene(group, semester);
                }
            }
        }
        for (Stage stage : StageHelper.getStages()) {
            if (stage.getTitle().contains((professor.getName()))) {
                stage.toFront();
            }
        }
    }

    public void groupsScheduleScene(int groupId, int semester) {

        Group group = groups.get(groupId);
        Stage scheduleStage=new Stage();
        HBox horizontalBox=new HBox();
        GridPane classesGrid=new GridPane();
        GridPane scheduleGrid=new GridPane();
        StackPane[][] scheduleMatrix=new StackPane[HOURS+1][DAYS+1];

        String[] ore={"Zi \\ Ora","8-9,50","10-11,50","12-13,50","14-15,50","16-17,50","18-19,50","20-21,50"};
        String[] zile={"Luni","Marti","Miercuri","Joi","Vineri","Sambata"};

        for (int i=0;i<HOURS+1;i++) {
            scheduleMatrix[i][0]=new StackPane();
            scheduleMatrix[i][0].setPrefSize(80,40);
            scheduleMatrix[i][0].setStyle("-fx-border-color:black; -fx-background-color:beige; -fx-padding:5");
            scheduleMatrix[i][0].getChildren().add(new Label((ore[i])));
            scheduleGrid.add(scheduleMatrix[i][0], i, 0);
        }

        for (int j=1;j<DAYS/2+1;j++) {
            scheduleMatrix[0][j]=new StackPane();
            scheduleMatrix[0][j].setStyle("-fx-border-color:black; -fx-background-color:beige; -fx-padding:5");
            scheduleMatrix[0][j].getChildren().add(new Label((zile[j-1])));
            scheduleGrid.add(scheduleMatrix[0][j], 0, j*2-1,1,2);
            System.out.println(j);
        }


        for (int i=1;i<HOURS+1;i++) {
            for (int j=1;j<DAYS+1;j++){
                scheduleMatrix[i][j]=new StackPane();
                scheduleMatrix[i][j].setStyle("-fx-border-color:black");
                scheduleMatrix[i][j].setPrefSize(80,40);
                scheduleMatrix[i][j].setAlignment(Pos.TOP_CENTER);
                addDropHandlingGroupSchedule(scheduleMatrix[i][j],groupId);
                int presentActivityId=group.getActivityGroup(semester,i-1,j-1);
                if (presentActivityId!=-1) {
                    Activity presentActivity=activities.get(presentActivityId);
                    IndexedLabel lbl=Utility.createGroupLabel(presentActivity,professors.get(presentActivity.getProfessorId()),groups,rooms);
                    scheduleMatrix[i][j].getChildren().add(lbl);
                    dragTextArea(lbl);
                    System.out.println("Add label " + activities.get(group.getActivityGroup(semester,i - 1, j - 1)).getSubject() + " " + i + "," + j);
                }
                scheduleGrid.add(scheduleMatrix[i][j], i, j);
            }
        }

        int nrActivities=0;
        for (int activity: group.getActivitiesOfGroup()) {
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
        for (int i = 0; i<group.getActivitiesOfGroup().length; i++) {
            Activity currentActivity = activities.get(group.getActivitiesOfGroup()[i]);
            Professor professor = professors.get(currentActivity.getProfessorId());
            if (currentActivity.getSemester() == semester) {
                classesArray[count] = new StackPane();
                classesArray[count].setPrefWidth(80);
                classesArray[count].setMinHeight(40);
                classesArray[count].setAlignment(Pos.CENTER);
                classesArray[count].setStyle("-fx-border-color:black");
                addDropHandlingClassesGroup(classesArray[count], groupId);
                classesGrid.add(classesArray[count], count % sqr, count / sqr);
                if (!onScheduleOfGroup(group.getActivitiesOfGroup()[i], groupId, semester)) {
                    IndexedLabel lbl = Utility.createGroupLabel(currentActivity, professor, groups,rooms);
                    classesArray[count].getChildren().add(lbl);
                    dragTextArea(lbl);
                }
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
            pane.setPrefWidth(80);
            pane.setMinHeight(40);
            pane.setAlignment(Pos.CENTER);
            addDropHandlingClassesGroup(pane,groupId);
            classesGrid.add(pane, i % sqr, i / sqr);
        }

        horizontalBox.getChildren().addAll(scheduleGrid,classesGrid);
        horizontalBox.setPadding(new Insets(20,20,20,20));
        horizontalBox.setAlignment(Pos.CENTER);
        horizontalBox.setSpacing(20);
        Scene scheduleScene=new Scene(horizontalBox);
        scheduleStage.setScene(scheduleScene);
        scheduleStage.setTitle("Orar grupa "+group.getGroupName()+" semestrul "+semester);
        scheduleStage.show();

    }

    private void addDropHandlingClassesGroup(StackPane pane,int groupId) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            boolean ok=false;
            for (int groupOfLabel:draggingLabel.getGroupsId()) {
                if (groupOfLabel==groupId) {
                    ok=true;
                    break;
                }
            }
            if (ok) {
                try {
                    ObservableList<Node> childrens;
                    StackPane parentOfLabel = (StackPane) draggingLabel.getParent();
                    StackPane actualPane;
                    IndexedLabel actualLabel;
                    GridPane gridOfOrigin = (GridPane) parentOfLabel.getParent();
                    childrens = gridOfOrigin.getChildren();
                    for (Node node:childrens){
                        if (node.getClass()==pane.getClass()) {
                            actualPane = (StackPane) node;
                            if (!actualPane.getChildren().isEmpty()) {
                                if (actualPane.getChildren().get(0).getClass()==IndexedLabel.class) {
                                    actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                    if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                        ((StackPane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception exception) {
                    System.out.println("Removing not succeded");
                }
                Activity activity=activities.get(draggingLabel.getActivityId());
                Professor professor=professors.get(activity.getProfessorId());
                int semester=activity.getSemester();
                draggingLabel.setPrefSize(80,40*activity.getTime());
                pane.getChildren().add(draggingLabel);
                e.setDropCompleted(true);
                dragTextArea(draggingLabel);
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfessor(semester,i,j))
                            professor.setActivityProfessor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }
            }
        });
    }

    private void addDropHandlingGroupSchedule(StackPane pane,int groupId) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&draggingLabel!=null&&pane.getChildren().isEmpty()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            Activity activity=activities.get(draggingLabel.getActivityId());
            if (db.hasContent(labelFormat)) {
                int     row= GridPane.getRowIndex(pane),
                        col= GridPane.getColumnIndex(pane),
                        time = activity.getTime();
                if (isMovableToGroupSchedule(col,row,time,activity)){
                    Pane parentOfLabel=(Pane) draggingLabel.getParent();
                    parentOfLabel.getChildren().clear();
                    moveToGroupSchedule(pane,col,row,activity,groupId);
                    draggingLabel = null;
                    e.setDropCompleted(true);
                }
            }
        });
    }

    private boolean isMovableToGroupSchedule(int col, int row, int time, Activity activity) {

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
                    if (professor.getActivityProfessor(semester,Y-1, X-1) != -1) {
                        return false;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, Y - 1, X - 1) != -1) {
                            return false;
                        }
                    }
                }
                break;
            case 0:
                if (row % 2 == 0)
                    row--;
                for (int t=0;t<time;t++) {
                    X=row+t%2;
                    Y=col+t/2;
                    if (professor.getActivityProfessor(semester,Y-1, X-1) != -1) {
                        return false;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, Y - 1, X - 1) != -1) {
                            return false;
                        }
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void moveToGroupSchedule(StackPane pane,int col, int row,Activity activity,int groupId) {

        StackPane secondPane,actualPane;
        GridPane grid;
        IndexedLabel actualLabel;
        IndexedLabel[] labels;
        int add;
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
                    labels[i] = Utility.createProfLabel(activity, professor, groups,rooms);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node:childrens){
                    if (node.getClass()==pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass()==draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfessor(semester,i,j))
                            professor.setActivityProfessor(semester,i,j,-1);
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
                            secondPane = (StackPane) node;
                            secondPane.getChildren().add(labels[t]);
                        }
                        if ( nodeX >= X && nodeY >= Y ) {
                            break;
                        }
                    }
                    professor.setActivityProfessor(semester,Y-1, X-1, activity.getIdActivity());
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester,Y-1, X-1, activity.getIdActivity());
                }
                break;
            case 0:
                grid=(GridPane) pane.getParent();
                labels=new IndexedLabel[time];
                for (int i=0;i<time;i++) {
                    labels[i] = Utility.createProfLabel(activity, professor, groups,rooms);
                    dragTextArea(labels[i]);
                }
                childrens = grid.getChildren();
                for (Node node:childrens){
                    if (node.getClass()==pane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass()==draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfessor(semester,i,j))
                            professor.setActivityProfessor(semester,i,j,-1);
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
                            secondPane = (StackPane) node;
                            secondPane.getChildren().add(labels[t]);
                        }
                        if ( nodeX >= X && nodeY >= Y ) {
                            break;
                        }
                    }
                    professor.setActivityProfessor(semester,Y-1, X-1, activity.getIdActivity());
                    for (int j = 0; j<activity.getGroupsId().length; j++)
                        groups.get(activity.getGroupsId()[j]).setActivityGroup(semester,Y-1, X-1, activity.getIdActivity());
                }
                break;
            default:
                break;
        }

        for (Stage stage:StageHelper.getStages()) {
            if (stage.getTitle().contains("Orar anul "+activity.getYearOfStudy())) {
                stage.close();
                yearScheduleScene(activity.getYearOfStudy(),semester);
            }
            if (stage.getTitle().contains(professor.getName())) {
                stage.close();
                professorsScheduleScene(professor.getIdProfessor(),semester);
            }
        }
        for (Stage stage:StageHelper.getStages()) {
            if (stage.getTitle().contains(groups.get(groupId).getGroupName())) {
                stage.toFront();
            }
        }
    }

    public void yearScheduleScene(int year, int semester) {

        Activity presentActivity;
        Professor professor;
        int presentActivityId;
        ArrayList<Group> groupsOfYear=new ArrayList<>();
        IndexedLabel lbl;

        for (Group g:groups) {
            if (g.getYear()==year) {
                groupsOfYear.add(g);
            }
        }

        int numberOfGroups = groupsOfYear.size();

        String[] legenda={"Anul","Specializarea","Grupa","Subgrupa"};
        String[] ore={"8-9,50","10-11,50","12-13,50","14-15,50","16-17,50","18-19,50","20-21,50"};
        String[] zile={"Luni","Marti","Miercuri","Joi","Vineri","Sambata"};
        Label textLabel;

        Stage scheduleStage=new Stage();

        GridPane windowGrid=new GridPane();
        windowGrid.setAlignment(Pos.CENTER);
        windowGrid.setPadding(new Insets(10,10,10,10));
        windowGrid.getColumnConstraints().add(new ColumnConstraints(240));
        windowGrid.getRowConstraints().add(new RowConstraints(50));

        GridPane legendGrid=new GridPane();
        legendGrid.setAlignment(Pos.CENTER);
        StackPane[] legendList=new StackPane[legenda.length];

        for (int i=0;i<legendList.length;i++) {
            legendList[i]=new StackPane();
            legendList[i].setStyle("-fx-border-color:black; -fx-background-color:beige; -fx-padding:5");
            legendList[i].setMinSize(60,50);
            textLabel=new Label((legenda[i]));
            textLabel.setFont(new Font(10));
            legendList[i].getChildren().add(textLabel);
            legendGrid.add(legendList[i],i,0);
        }

        ScrollPane legendPane=new ScrollPane();
        legendPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        legendPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        legendPane.setContent(legendGrid);
        windowGrid.add(legendPane,0,0);

        GridPane headerGrid=new GridPane();
        headerGrid.setAlignment(Pos.CENTER);
        StackPane[][] headerMatrix=new StackPane[6*HOURS][2];

        for (int i=0;i<DAYS/2;i++) {
            headerMatrix[i][0]=new StackPane();
            headerMatrix[i][0].setStyle("-fx-border-color:black; -fx-background-color:beige;");
            headerMatrix[i][0].setPrefSize(60,25);
            textLabel=new Label((zile[i]));
            textLabel.setFont(new Font(10));
            headerMatrix[i][0].getChildren().add(textLabel);
            headerGrid.add(headerMatrix[i][0],i*7,1,7,1);
        }

        for (int i=0;i<DAYS/2*HOURS;i++) {
            headerMatrix[i][1]=new StackPane();
            headerMatrix[i][1].setStyle("-fx-border-color:black; -fx-background-color:beige");
            headerMatrix[i][1].setPrefSize(60,25);
            textLabel=new Label(ore[i%HOURS]);
            textLabel.setFont(new Font(10));
            headerMatrix[i][1].getChildren().add(textLabel);
            headerGrid.add(headerMatrix[i][1], i, 2);
        }

        ScrollPane headerScroll=new ScrollPane();
        headerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        headerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        headerScroll.setContent(headerGrid);
        windowGrid.add(headerScroll,1,0);

        GridPane leftGrid=new GridPane();
        headerGrid.setAlignment(Pos.CENTER);
        StackPane[][] leftMatrix=new StackPane[legenda.length][numberOfGroups*4];

        for (int i=0;i<legenda.length;i++) {
            for (int j=0;j<numberOfGroups*2;j++) {
                leftMatrix[i][j]=new StackPane();
                leftMatrix[i][j].setStyle("-fx-border-color:black; -fx-background-color:beige");
                switch (i) {
                    case 0:
                        if(j%2==0) {
                            leftMatrix[i][j].setPrefSize(60,100);
                            textLabel=new Label(Integer.toString(groupsOfYear.get(j/2).getYear()));
                            textLabel.setFont(new Font(10));
                            leftMatrix[i][j].getChildren().add(textLabel);
                            leftGrid.add(leftMatrix[i][j], i, j,1,2);
                        }
                        break;
                    case 1:
                        if(j%2==0) {
                            leftMatrix[i][j].setPrefSize(60,100);
                            textLabel=new Label(groupsOfYear.get(j/2).getSpeciality());
                            textLabel.setFont(new Font(10));
                            leftMatrix[i][j].getChildren().add(textLabel);
                            leftGrid.add(leftMatrix[i][j], i, j,1,2);
                        }
                        break;
                    case 2:
                        if(j%2==0) {
                            leftMatrix[i][j].setPrefSize(60,100);
                            textLabel=new Label(groupsOfYear.get(j/2).getGroupName());
                            textLabel.setFont(new Font(10));
                            leftMatrix[i][j].getChildren().add(textLabel);
                            leftGrid.add(leftMatrix[i][j], i, j,1,2);
                        }
                        break;
                    case 3:
                        if(j%2==0) {
                            leftMatrix[i][j].setPrefSize(60,50);
                            textLabel=new Label("A");
                            textLabel.setFont(new Font(10));
                            leftMatrix[i][j].getChildren().add(textLabel);
                            leftGrid.add(leftMatrix[i][j], i, j,1,1);
                        }
                        else {
                            leftMatrix[i][j].setPrefSize(60,50);
                            textLabel=new Label("B");
                            textLabel.setFont(new Font(10));
                            leftMatrix[i][j].getChildren().add(textLabel);
                            leftGrid.add(leftMatrix[i][j], i, j,1,1);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        ScrollPane leftScroll=new ScrollPane();
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setContent(leftGrid);
        windowGrid.add(leftScroll,0,1);

        GridPane scheduleGrid=new GridPane();
        scheduleGrid.setAlignment(Pos.CENTER);
        StackPane[][] scheduleMatrix=new StackPane[DAYS/2*HOURS][numberOfGroups*4];

        for (int i=0;i<DAYS/2*HOURS;i++)
            for (int j=0;j<numberOfGroups*4;j++){

                int day=i/HOURS;
                Group presentGroup = groupsOfYear.get(j/4);

                scheduleMatrix[i][j]=new StackPane();
                scheduleMatrix[i][j].setPrefSize(60,25);
                scheduleMatrix[i][j].setStyle("-fx-border-color:black");
                scheduleMatrix[i][j].setAlignment(Pos.TOP_CENTER);

                presentActivityId = presentGroup.getActivityGroup(semester,i%HOURS,day*2);

                addDropHandlingYearSchedule(scheduleMatrix[i][j],groupsOfYear);

                if (presentActivityId!=-1) {
                    presentActivity=activities.get(presentActivityId);
                    professor=professors.get(presentActivity.getProfessorId());
                    addDropHandlingYearSchedule(scheduleMatrix[i][j],groupsOfYear);
                    lbl=Utility.createYearLabel(presentActivity, professor, groups,rooms);
                    scheduleMatrix[i][j].getChildren().add(lbl);
                    dragTextArea(lbl);
                    System.out.println("Add label " + activities.get(professor.getActivityProfessor(semester,i%HOURS, day*2)).getSubject() + " " + i + "," + j);
                }
                scheduleGrid.add(scheduleMatrix[i][j], i, j);
                System.out.println(i+","+j);

                j++;

                scheduleMatrix[i][j]=new StackPane();
                scheduleMatrix[i][j].setPrefSize(60,25);
                scheduleMatrix[i][j].setStyle("-fx-border-color:black");
                scheduleMatrix[i][j].setAlignment(Pos.TOP_CENTER);

                presentActivityId = presentGroup.getActivityGroup(semester,i%HOURS,day*2+1);

                addDropHandlingYearSchedule(scheduleMatrix[i][j],groupsOfYear);

                if (presentActivityId!=-1) {
                    presentActivity=activities.get(presentActivityId);
                    professor=professors.get(presentActivity.getProfessorId());
                    addDropHandlingYearSchedule(scheduleMatrix[i][j],groupsOfYear);
                    lbl=Utility.createYearLabel(presentActivity, professor, groups,rooms);
                    scheduleMatrix[i][j].getChildren().add(lbl);
                    dragTextArea(lbl);
                    System.out.println("Add label " + activities.get(professor.getActivityProfessor(semester,i%HOURS, day*2+1)).getSubject() + " " + i + "," + j);
                }
                scheduleGrid.add(scheduleMatrix[i][j], i, j);
                System.out.println(i+","+j);
            }

        ScrollPane scheduleScroll=new ScrollPane();
        scheduleScroll.pannableProperty().set(true);
        scheduleScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scheduleScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scheduleScroll.setContent(scheduleGrid);

        setupScrolling(scheduleScroll);

        windowGrid.add(scheduleScroll,1,1);

        headerScroll.hvalueProperty().bindBidirectional(scheduleScroll.hvalueProperty());
        leftScroll.vvalueProperty().bindBidirectional(scheduleScroll.vvalueProperty());

        Scene scheduleScene=new Scene(windowGrid);
        scheduleScroll.autosize();
        scheduleStage.setScene(scheduleScene);
        scheduleStage.setTitle("Orar anul "+year+" semestrul "+semester);
        scheduleStage.show();

    }

    private void addDropHandlingYearSchedule(StackPane pane, ArrayList<Group> groupsOfThisGrid) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(labelFormat)&&draggingLabel!=null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            Activity activity=activities.get(draggingLabel.getActivityId());
            if (db.hasContent(labelFormat)) {
                int     row = GridPane.getRowIndex(pane),
                        col = GridPane.getColumnIndex(pane),
                        time = activity.getTime();
                ArrayList<Integer> rows= isMovableToYearSchedule(col,row,time,activity,groupsOfThisGrid);
                if (rows!=null){
                    Pane parentOfLabel=(Pane) draggingLabel.getParent();
                    parentOfLabel.getChildren().clear();
                    moveToYearSchedule(pane,col,rows,activity,groupsOfThisGrid);
                    draggingLabel = null;
                    e.setDropCompleted(true);
                }
            }
        });
    }

    private void setupScrolling(ScrollPane scheduleScroll) {
        final double[] xDirection = {0};
        final double[] yDirection = {0};
        Timeline scrolltimeline = new Timeline();
        scrolltimeline.setCycleCount(Timeline.INDEFINITE);
        scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scroll", (ActionEvent) -> dragScroll(scheduleScroll, xDirection[0], yDirection[0])));
        scheduleScroll.setOnDragExited(event -> {
            Bounds scrollBound=scheduleScroll.getLayoutBounds();
            Point2D leftUpper = scheduleScroll.localToScene(scrollBound.getMinX(), scrollBound.getMinY());
            Point2D rightDowner = scheduleScroll.localToScene(scrollBound.getMaxX(), scrollBound.getMaxY());
            if (event.getSceneX() >rightDowner.getX()) {
                xDirection[0] =1.0 / 200;
            }
            else if (event.getSceneX() < leftUpper.getX()) {
                xDirection[0] =-1.0 / 200;
            }
            else {
                xDirection[0] =0;
            }
            if (event.getSceneY() > rightDowner.getY() ) {
                yDirection[0] = 1.0 / 200;
            }
            else if (event.getSceneY() < leftUpper.getY() ){
                yDirection[0] =-1.0 / 200;
            }
            else {
                yDirection[0] = 0;
            }
            if (!dropped) {
                scrolltimeline.play();
            }
        });
        scheduleScroll.setOnDragEntered(event -> {
            scrolltimeline.stop();
            dropped = false;
        });
        scheduleScroll.setOnDragDone(event -> scrolltimeline.stop());
        scheduleScroll.setOnDragDropped(event -> {
            scrolltimeline.stop();
            dropped=true;
        });
    }

    private void dragScroll(ScrollPane scheduleScroll,double xDirection,double yDirection) {
        ScrollBar verticalScrollBar = getVerticalScrollbar(scheduleScroll);
        if (verticalScrollBar != null) {
            double newValue = verticalScrollBar.getValue() + yDirection;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            verticalScrollBar.setValue(newValue);
        }
        ScrollBar horizontalScrollBar = getHorizontalScrollbar(scheduleScroll);
        if (horizontalScrollBar != null) {
            double newValue = horizontalScrollBar.getValue() + xDirection;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            horizontalScrollBar.setValue(newValue);
        }
    }

    private ScrollBar getVerticalScrollbar(ScrollPane scheduleScroll) {
        ScrollBar result = null;
        for (Node n : scheduleScroll.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    private ScrollBar getHorizontalScrollbar(ScrollPane scheduleScroll) {
        ScrollBar result = null;
        for (Node n : scheduleScroll.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    private ArrayList<Integer> isMovableToYearSchedule(int col, int row, int time, Activity activity,ArrayList<Group> groupsOfThisGrid) {

        ArrayList<Integer> rows=new ArrayList<>();
        int semester = activity.getSemester();
        Professor professor = professors.get(activity.getProfessorId());
        boolean rowOk=false;
        int groupOfThisRow=groupsOfThisGrid.get(row/4).getIdGroup();
        for (int g:activity.getGroupsId()) {
            if (g==groupOfThisRow) {
                rowOk=true;
                break;
            }
        }
        if (!rowOk) {
            Utility.message("Grupa gresita");
            return null;
        }
        if ((col + (time-1)/2) / HOURS > col / HOURS) {
            return null;
        }
        int dir,add;
        int X,Y,colProfSchedule,rowProfSchedule;
        switch (time%2) {
            case 1:
                if (row % 2 == 1) {
                    dir=-1;
                    add=1;
                }
                else {
                    dir = 1;
                    add=0;
                }
                for (int t=0;t<time;t++) {
                    X=row+(t%2)*dir+add;
                    Y=col+(t+1)/2;
                    colProfSchedule=Y%HOURS;
                    rowProfSchedule=Y/HOURS*2+X-row;
                    if (professor.getActivityProfessor(semester, colProfSchedule, rowProfSchedule) != -1
                    && professor.getActivityProfessor(semester, colProfSchedule, rowProfSchedule) != activity.getIdActivity()) {
                        Activity act=activities.get(professor.getActivityProfessor(semester,colProfSchedule,rowProfSchedule));
                        Utility.message(professor.getName()+" are alta activitate\n\n"+act.getSubject());
                        return null;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, colProfSchedule, rowProfSchedule) != -1
                        && groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, colProfSchedule, rowProfSchedule) != activity.getIdActivity()) {
                            Activity act=activities.get(groups.get(activity.getGroupsId()[j]).getActivityGroup(semester,colProfSchedule,rowProfSchedule));
                            Utility.message("Grupa "+groups.get(activity.getGroupsId()[j]).getGroupName()+" are alta activitate\n\n"+act.getSubject());
                            return null;
                        }
                    }
                }
                break;
            case 0:
                if (row % 2 == 1)
                    row--;
                for (int t=0;t<time;t++) {
                    X=row+t%2;
                    Y=col+t/2;
                    colProfSchedule=Y%HOURS;
                    rowProfSchedule=Y/HOURS*2+X-row;
                    if (professor.getActivityProfessor(semester,colProfSchedule, rowProfSchedule) != -1
                    && professor.getActivityProfessor(semester,colProfSchedule, rowProfSchedule) != activity.getIdActivity()) {
                        Activity act=activities.get(professor.getActivityProfessor(semester,colProfSchedule,rowProfSchedule));
                        Utility.message(professor.getName()+" are alta activitate\n\n"+act.getSubject());
                        return null;
                    }
                    for (int j = 0; j<activity.getGroupsId().length; j++) {
                        if (groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, colProfSchedule, rowProfSchedule) != -1
                        && groups.get(activity.getGroupsId()[j]).getActivityGroup(semester, colProfSchedule, rowProfSchedule) != activity.getIdActivity()) {
                            Activity act=activities.get(groups.get(activity.getGroupsId()[j]).getActivityGroup(semester,colProfSchedule,rowProfSchedule));
                            Utility.message("Grupa "+groups.get(activity.getGroupsId()[j]).getGroupName()+" are alta activitate\n\n"+act.getSubject());
                            return null;
                        }
                    }
                }
                break;
            default:
                break;
        }

        int semigroup;
        if (row%4<2)
            semigroup=2;
        else
            semigroup=-2;
        //rows.add(row);
        //rows.add(row+semigroup);
        for (int i=0;i<groupsOfThisGrid.size();i++) {
            for (int g:activity.getGroupsId()) {
                if (g==groupsOfThisGrid.get(i).getIdGroup()) {
                    rows.add(row+(i-row/4)*4);
                    rows.add(row+(i-row/4)*4+semigroup);
                }
            }
        }
        return rows;
    }

    private void moveToYearSchedule(StackPane thisPane,int col, ArrayList<Integer> rows,Activity activity,ArrayList<Group> groupsOfThisGrid) {

        StackPane stackPane,actualPane;
        GridPane grid;
        IndexedLabel actualLabel;
        IndexedLabel[] labels;
        int dir;
        ObservableList<Node> childes;
        int X,Y,nodeX,nodeY;

        Professor professor=professors.get(activity.getProfessorId());
        int semester=activity.getSemester();
        int time=activity.getTime();

        switch (time%2) {

            case 1:

                if (thisPane.getParent().getClass().getName().equals("javafx.scene.layout.GridPane"))
                    grid=(GridPane) thisPane.getParent();
                else {
                    System.out.println(thisPane.getParent().getClass().getName());
                    return;
                }
                labels=new IndexedLabel[time*rows.size()];
                for (int i=0;i<(time*rows.size());i++) {
                    labels[i] = Utility.createYearLabel(activity, professor, groups,rooms);
                    dragTextArea(labels[i]);
                }
                childes = grid.getChildren();
                for (Node node:childes){
                    if (node.getClass()==thisPane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass()==draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++) {
                    for (int j=0;j<DAYS;j++) {
                        if (activity.getIdActivity() == professor.getActivityProfessor(semester, i, j))
                            professor.setActivityProfessor(semester, i, j, -1);
                        for (int k = 0; k < Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity() == groups.get(activity.getGroupsId()[k]).getActivityGroup(semester, i, j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester, i, j, -1);
                    }
                }

                for (int i=0; i<rows.size();i++) {
                    int row=rows.get(i);
                    int add;
                    if (row % 2 == 1) {
                        dir = -1;
                        add=1;
                    }
                    else {
                        dir = 1;
                        add=0;
                    }
                    for (int t = 0; t < time; t++) {
                        X = row + (t % 2) * dir;
                        Y = col + (t + 1) / 2;
                        System.out.println(t);
                        childes = grid.getChildren();
                        for (Node node : childes) {
                            nodeX = GridPane.getRowIndex(node);
                            nodeY = GridPane.getColumnIndex(node);
                            if (nodeX == X && nodeY == Y) {
                                stackPane = (StackPane) node;
                                stackPane.getChildren().add(labels[t+i*time]);
                            }
                            if (nodeX >= X && nodeY >= Y) {
                                break;
                            }
                        }
                        professor.setActivityProfessor(semester, Y % HOURS, Y / HOURS * 2 + X - row+add, activity.getIdActivity());
                        for (int j = 0; j < activity.getGroupsId().length; j++)
                            groups.get(activity.getGroupsId()[j]).setActivityGroup(semester, Y % HOURS, Y / HOURS * 2 + X - row+add, activity.getIdActivity());
                    }
                }
                break;

            case 0:

                grid=(GridPane) thisPane.getParent();
                labels=new IndexedLabel[time*rows.size()];
                for (int i=0;i<time*rows.size();i++) {
                    labels[i] = Utility.createYearLabel(activity, professor, groups,rooms);
                    dragTextArea(labels[i]);
                }
                childes = grid.getChildren();
                for (Node node:childes){
                    if (node.getClass()==thisPane.getClass()) {
                        actualPane = (StackPane) node;
                        if (!actualPane.getChildren().isEmpty()) {
                            if (actualPane.getChildren().get(0).getClass()==draggingLabel.getClass()) {
                                actualLabel = (IndexedLabel) actualPane.getChildren().get(0);
                                if (actualLabel.getActivityId() == draggingLabel.getActivityId()) {
                                    ((Pane) actualLabel.getParent()).getChildren().remove(actualLabel);
                                }
                            }
                        }
                    }
                }
                for (int i=0;i<HOURS;i++)
                    for (int j=0;j<DAYS;j++){
                        if (activity.getIdActivity()==professor.getActivityProfessor(semester,i,j))
                            professor.setActivityProfessor(semester,i,j,-1);
                        for (int k = 0; k< Objects.requireNonNull(activity).getGroupsId().length; k++)
                            if (activity.getIdActivity()==groups.get(activity.getGroupsId()[k]).getActivityGroup(semester,i,j))
                                groups.get(activity.getGroupsId()[k]).setActivityGroup(semester,i,j,-1);
                    }

                for (int i=0; i<rows.size();i++) {
                    int row=rows.get(i);
                    if (row % 2 == 1)
                        row--;
                    for (int t = 0; t < time; t++) {
                        X = row + t % 2;
                        Y = col + t / 2;
                        System.out.println(t);
                        childes = grid.getChildren();
                        for (Node node : childes) {
                            nodeX = GridPane.getRowIndex(node);
                            nodeY = GridPane.getColumnIndex(node);
                            if (nodeX == X && nodeY == Y) {
                                stackPane = (StackPane) node;
                                stackPane.getChildren().add(labels[t+i*time]);
                            }
                            if (nodeX >= X && nodeY >= Y) {
                                break;
                            }
                        }
                        professor.setActivityProfessor(semester, Y % HOURS, Y / HOURS * 2 + X - row, activity.getIdActivity());
                        for (int j = 0; j < activity.getGroupsId().length; j++)
                            groups.get(activity.getGroupsId()[j]).setActivityGroup(semester, Y % HOURS, Y / HOURS * 2 + X - row, activity.getIdActivity());
                    }
                }
                break;
            default:
                break;
        }

        for (Stage stage:StageHelper.getStages()) {
            if (stage.getTitle().contains(professor.getName())) {
                stage.close();
                professorsScheduleScene(professor.getIdProfessor(),semester);
            }
            for (int group:activity.getGroupsId()) {
                if (stage.getTitle().contains(groups.get(group).getGroupName())) {
                    stage.close();
                    groupsScheduleScene(group,semester);
                }
            }
        }
        for (Stage stage:StageHelper.getStages()) {
            if (stage.getTitle().contains("Orar anul "+activity.getYearOfStudy())) {
                stage.toFront();
            }
        }
    }

    public ArrayList<Room> editRooms(ArrayList<Room> rooms) {

        Stage stage=new Stage();

        return rooms;
    }
}