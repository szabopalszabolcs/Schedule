public class Professor {

    private int idProfesor;
    private final String name;
    private String shortName;
    private int[][][] scheduleProfesor;
    private int[] activitiesOfProfesor;

    public Professor(int id, String name){
        final int DAYS=12,HOURS=7;
        this.idProfesor = id;
        this.name = name;
        String[] names=name.split(" ");
        this.shortName = names[0].substring(0,1)+names[0].substring(1).toLowerCase();
        for (int i=1;i<names.length;i++){
            this.shortName=this.shortName+"_"+names[i].substring(0,1);
        }
        this.scheduleProfesor = new int[2][DAYS][HOURS];
        for(int i=0;i<2;i++)
            for(int j=0;j<DAYS;j++)
                for(int k=0;k<HOURS;k++)
                    scheduleProfesor[i][j][k]=-1;
        activitiesOfProfesor = new int[0];
    }

    public int getIdProfesor() { return idProfesor; }

    public void setIdProfesor(int id) { this.idProfesor=id; }

    public String getName() { return name; }

    public String getShortName() { return shortName; }

    public int[] getActivitiesOfProfesor() { return activitiesOfProfesor; }

    public int getActivityProfesor(int semester,int day,int hour) {
        try {
            return scheduleProfesor[semester-1][day][hour];
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public boolean setActivityProfesor(int semester,int col,int row,int activity) {
        try {
            scheduleProfesor[semester-1][col][row]=activity;
            return true;
        }
        catch (Exception ex){
            Utility.errorMessage("Activitatea nu a fost adăugată");
            return false;
        }
    }

    public void addActivity (int activity) {
        int size=activitiesOfProfesor.length;
        int[] newActivites=new int[size+1];
        for (int i=0;i<size;i++) {
            newActivites[i] = activitiesOfProfesor[i];
        }
        newActivites[size]=activity;
        activitiesOfProfesor=new int[size+1];
        for(int i=0;i<size+1;i++) {
            activitiesOfProfesor[i]=newActivites[i];
        }
    }

    public boolean removeActivity (int activity) {
        int size=activitiesOfProfesor.length;
        int[] newActivities=new int[size-1];
        int j=0;
        for (int i=0;i<size;i++) {
            try {
                if (activitiesOfProfesor[i]!=activity) {
                    newActivities[j]=activitiesOfProfesor[i];
                    j++;
                }
            }
            catch (Exception ex) {
                Utility.errorMessage("Activitatea nu a fost ștearsă");
                return false;
            }
        }
        activitiesOfProfesor = new int[size-1];
        for (int i=0;i<size-1;i++) {
            activitiesOfProfesor[i]=newActivities[i];
        }
        return true;
    }

}