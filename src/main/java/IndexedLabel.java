import javafx.scene.control.Label;

public class IndexedLabel extends Label {

    private int activityId;
    private int professorId;
    private int[] groupsId;

    public IndexedLabel() { }

    public IndexedLabel(int activityId, int professorId, int[] groupsId) {
        this.activityId = activityId;
        this.professorId = professorId;
        this.groupsId = groupsId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public void setGroupsId(int[] groupsId) {
        this.groupsId = groupsId;
    }

    public int getActivityId() {
        return activityId;
    }

    public int getProfessorId() {
        return professorId;
    }

    public int[] getGroupsId() {
        return groupsId;
    }

}
