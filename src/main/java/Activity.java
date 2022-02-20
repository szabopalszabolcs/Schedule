import java.util.ArrayList;
import java.util.Arrays;

public class Activity {

    private final int idActivity;
    private final String subject;
    private final String codeSubject;
    private final int profesor;
    private final int type;
    private int[] groups;
    private final int semester;
    private final int yearOfStudy;
    private final int time;
    private final boolean weekly;

    public Activity(int idActivity,String subject, String codeSubject, int profesor, int tip, int[] groups, int semester, int yearOfStudy, int time, boolean weekly){
        this.idActivity=idActivity;
        this.subject = subject;
        this.codeSubject = codeSubject;
        this.profesor =profesor;
        this.type =tip;
        this.groups = groups;
        this.semester = semester;
        this.yearOfStudy = yearOfStudy;
        this.time = time;
        this.weekly = weekly;
    }

    @Override
    public String toString() {
        return  idActivity +
                "-" + subject +
                "," + codeSubject +
                "," + type +
                "," + profesor +
                "," + Arrays.toString(groups) +
                ", semestru=" + semester +
                ", an=" + yearOfStudy +
                ", durata=" + time +
                ", saptamanal=" + weekly;
    }

    public void addGroups(Group[] newGroups){

        ArrayList<Integer> groupsToAdd=new ArrayList<>();
        for (int idGroup : groups) {
            groupsToAdd.add(idGroup);
            for (Group newGroup : newGroups) {
                if (newGroup.getIdGroup()==idGroup) {
                    newGroup.setGroupName("");
                }
            }
        }
        for (Group group : newGroups) {
            if (!group.getGroupName().equals("")) {
                groupsToAdd.add(group.getIdGroup());
            }
        }
        int[] newGroup=new int[groupsToAdd.size()];
        for (int i=0;i<newGroup.length;i++)
            newGroup[i] = groupsToAdd.get(i);
        groups= newGroup;

    }

    public String getCodeSubject(){
        return codeSubject;
    }

    public String getSubject() {
        return subject;
    }

    public int getTime() { return time; }

    public int getType(){
        return type;
    }

    public int getProfesor() { return profesor; }

    public int[] getGroups() {return groups; }

    public int getSemester() { return semester; }

    public int getIdActivity() { return idActivity; }

}