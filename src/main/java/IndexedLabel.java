import javafx.scene.control.Label;


public class IndexedLabel extends Label {

    private int activity;
    private int profesor;
    private int[] groups;

    public IndexedLabel() { }

    public IndexedLabel(int activity,int profesor,int[] groups) {
        this.activity=activity;
        this.profesor=profesor;
        this.groups=groups;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getProfesor() {
        return profesor;
    }

    public void setProfesor(int profesor) {
        this.profesor = profesor;
    }

    public int[] getGroups() {
        return groups;
    }

    public void setGroups(int[] groups) {
        this.groups = groups;
    }

}
