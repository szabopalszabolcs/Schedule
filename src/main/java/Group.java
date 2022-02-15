public class Group {

    private int idGroup;
    private String groupName;
    private int[][][] scheduleGroup;
    private int[] activitiesOfGroup;

    public Group(int id,String speciality,int year,int groupNumber){
        final int DAYS = 6,HOURS = 14;
        this.idGroup=id;
        this.groupName=speciality+year+groupNumber;
        this.scheduleGroup =new int[2][DAYS][HOURS];
        for(int i=0;i<2;i++)
            for(int j=0;j<DAYS;j++)
                for(int k=0;k<HOURS;k++)
                    scheduleGroup[i][j][k]=-1;
        activitiesOfGroup = new int[0];
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(String name){
        this.groupName=name;
    }

    public int getIdGroup() { return idGroup; }

    public void setIdGroup(int id) { this.idGroup=id; }

    public int getActivityGroup(int semester,int col,int row){
        try {
            return scheduleGroup[semester-1][col][row];
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public boolean setActivityGroup(int semester,int col,int row, int activity) {
        try {
            scheduleGroup[semester-1][col][row]=activity;
            return true;
        }
        catch (Exception ex){
            Utility.errorMessage("Activitatea nu a fost adăugată");
            return false;
        }
    }
    public void addActivity (int activity) {
        int size=activitiesOfGroup.length;
        int[] newActivites=new int[size+1];
        for (int i=0;i<size;i++) {
            newActivites[i] = activitiesOfGroup[i];
        }
        newActivites[size]=activity;
        activitiesOfGroup=new int[size+1];
        for(int i=0;i<size+1;i++) {
            activitiesOfGroup[i]=newActivites[i];
        }
    }

    public boolean removeActivity (int activity) {
        int size=activitiesOfGroup.length;
        int[] newActivities=new int[size-1];
        int j=0;
        for (int i=0;i<size;i++) {
            try {
                if (activitiesOfGroup[i]!=activity) {
                    newActivities[j]=activitiesOfGroup[i];
                    j++;
                }
            }
            catch (Exception ex) {
                Utility.errorMessage("Activitatea nu a fost ștearsă");
                return false;
            }
        }
        activitiesOfGroup = new int[size-1];
        for (int i=0;i<size-1;i++) {
            activitiesOfGroup[i]=newActivities[i];
        }
        return true;
    }

}