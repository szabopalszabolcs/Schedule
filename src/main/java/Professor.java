public class Professor {

    private int idProfesor;
    private final String name;
    private String shortName;
    private int[][][] scheduleProfesor;
    private int[] activitiesOfProfesor;

    public Professor(int id, String name){
        final int HOURS=7,DAYS=12;
        this.idProfesor = id;
        this.name = name;
        String[] names=name.split(" ");
        this.shortName = names[0].substring(0,1)+names[0].substring(1).toLowerCase();
        for (int i=1;i<names.length;i++){
            this.shortName += "_" + names[i].substring(0, 1);
        }
        this.scheduleProfesor = new int[2][HOURS][DAYS];
        for(int i=0;i<2;i++)
            for(int j=0;j<HOURS;j++)
                for(int k=0;k<DAYS;k++)
                    scheduleProfesor[i][j][k]=-1;
        activitiesOfProfesor = new int[0];
    }

    public int getIdProfesor() { return idProfesor; }

    public void setIdProfesor(int id) { this.idProfesor=id; }

    public String getName() { return name; }

    public String getShortName() { return shortName; }

    public int[] getActivitiesOfProfesor() { return activitiesOfProfesor; }

    public int getActivityProfesor(int semester,int hour,int day) {
        try {
            return scheduleProfesor[semester-1][hour][day];
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public boolean setActivityProfesor(int semester,int hour,int day,int activity) {
        try {
            scheduleProfesor[semester-1][hour][day]=activity;
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
        System.arraycopy(activitiesOfProfesor, 0, newActivites, 0, size);
        newActivites[size]=activity;
        activitiesOfProfesor=new int[size+1];
        System.arraycopy(newActivites, 0, activitiesOfProfesor, 0, size + 1);
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
        if (size - 1 >= 0) System.arraycopy(newActivities, 0, activitiesOfProfesor, 0, size - 1);
        return true;
    }

}