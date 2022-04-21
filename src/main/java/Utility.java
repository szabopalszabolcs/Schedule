import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import java.io.*;
import java.util.ArrayList;

class NoSheetException extends Exception{
    NoSheetException(String ex){
        super(ex);
    }
}

public class Utility {

    static int profIndex=0,groupIndex=0,activityIndex=0;

    static int max(int a,int b,int c,int d){
        if (a>b) b=a;
        if (b>c) c=b;
        if (c>d) d=c;
        return d;
    }

    static Group addIfNotInGroup(Group group,ArrayList<Group> groups){
        for (Group nextGroup:groups){
            if (group.getGroupName().equals(nextGroup.getGroupName())){
                return nextGroup;
            }
        }
        group.setIdGroup(groupIndex);
        groups.add(group);
        groupIndex++;
        return group;
    }

    static Professor addIfNotInProfs(Professor professor, ArrayList<Professor> professors) {
        for (Professor nextProfessor : professors) {
            if (professor.getName().equals(nextProfessor.getName())) {
                return nextProfessor;
            }
        }
        professor.setIdProfesor(profIndex);
        professors.add(professor);
        profIndex++;
        return professor;
    }

    static void errorMessage(String message){


        System.out.println(message);
    }

    static void createActivities(ArrayList<Activity> activities, ArrayList<Professor> professors, ArrayList<Group> groups, int profNumber, int numberOfGroups, int fAct, Group[][] actualGroups, String subject, String codeSubject, Professor[] actualProfs, int type, int semester, int year, int actTotal, int[] actTime, int numberOfCourses) {

        float nrAct=(float) fAct*actTime[profNumber]/actTotal;

        for (int j = 0; j < nrAct ; j++) {
            int groupTeam = (numberOfGroups/fAct);
            Group[] groupsToAdd = new Group[groupTeam];
            int[] groupIdToAdd=new int[groupTeam];
            int k = 0, l = 0;
            while (k < groupTeam) {
                if (actualGroups[type-1][j + k + l]!=null) {
                    groupsToAdd[k] = actualGroups[type-1][j + k + l];
                    groupIdToAdd[k] = actualGroups[type-1][j + k + l].getIdGroup();
                    actualGroups[type-1][j + k + l]=null;
                    k++;
                }
                else {
                    l++;
                }
            }
            float activityTime;
            if (nrAct<1)
                activityTime=(float) fAct/actTotal;
            else
                activityTime=(float) actTotal/fAct/2;
            activityTime*=2;
            if (activityTime<1) activityTime=1;
            Activity newActivity = new Activity(activityIndex, subject, codeSubject, actualProfs[profNumber].getIdProfesor(), type, groupIdToAdd, semester, year, (int) activityTime, (activityTime >=2));
            if(numberOfCourses==-1){
                for (Activity nextActivity:activities){
                    if((newActivity.getSubject().equals(nextActivity.getSubject())&&(nextActivity.getType()==1))){
                        nextActivity.addGroups(groupsToAdd);
                    }
                }
            }
            activities.add(newActivity);
            professors.get(newActivity.getProfessorId()).addActivity(activities.indexOf(newActivity));
            activityIndex++;
            for (int group : newActivity.getGroupsId()) {
                groups.get(group).addActivity(activities.indexOf(newActivity));
            }
            System.out.println(newActivity);
        }

    }

    public static ArrayList<Activity> readXls(String fileName, ArrayList<Professor> professors, ArrayList<Group> groups, String faculty){

        ArrayList<Activity> activities=new ArrayList<>();
        File file=new File(fileName);
        professors.clear();
        groups.clear();
        profIndex=0;
        groupIndex=0;
        activityIndex=0;

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
            int numberOfSheets=hssfWorkbook.getNumberOfSheets();
            int sheetToRead=-1;

            for (int i=0;i<numberOfSheets;i++){
                if (hssfWorkbook.getSheetName(i).equals("Centr")) {
                    sheetToRead=i;
                    i=numberOfSheets;
                }
            }
            if (sheetToRead==-1) {
                throw new NoSheetException("Sheet inexistent");
            }
            HSSFSheet hssfSheet=hssfWorkbook.getSheetAt(sheetToRead);

    //start row

            int r=10;
            while (!hssfSheet.getRow(r).getCell(1).toString().equals("")/*&&(r<346)*/){
                Row firstRow=hssfSheet.getRow(r);

                if (faculty.equals(firstRow.getCell(2).toString())){
                    String ok=firstRow.getCell(17).toString();
                    if (ok.equals("ok")) {
                        Row secondRow = hssfSheet.getRow(r + 1);
                        String subject = firstRow.getCell(1).toString().trim();
                        String departament = firstRow.getCell(4).toString().trim();
                        String[] speciality = firstRow.getCell(5).toString().split("\\+");
                        for (String spec : speciality)
                            System.out.println(spec);
                        int year = (int) firstRow.getCell(6).getNumericCellValue();
                        String[] codeFormation = secondRow.getCell(1).toString().split(",");
                        String codeSubject = codeFormation[0];
                        int fCrs = Integer.parseInt(codeFormation[1]);
                        int fSem = Integer.parseInt(codeFormation[2]);
                        int fLab = Integer.parseInt(codeFormation[3]);
                        int fPrc = Integer.parseInt(codeFormation[4]);
                        int s1C, s1S, s1L, s1P, s2C, s2S, s2L, s2P;
                        if (secondRow.getCell(8, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s1C = (int) secondRow.getCell(8).getNumericCellValue();
                        else s1C = 0;
                        if (secondRow.getCell(9, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s1S = (int) secondRow.getCell(9).getNumericCellValue();
                        else s1S = 0;
                        if (secondRow.getCell(10, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s1L = (int) secondRow.getCell(10).getNumericCellValue();
                        else s1L = 0;
                        if (secondRow.getCell(11, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s1P = (int) secondRow.getCell(11).getNumericCellValue();
                        else s1P = 0;
                        if (secondRow.getCell(13, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s2C = (int) secondRow.getCell(13).getNumericCellValue();
                        else s2C = 0;
                        if (secondRow.getCell(14, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s2S = (int) secondRow.getCell(14).getNumericCellValue();
                        else s2S = 0;
                        if (secondRow.getCell(15, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s2L = (int) secondRow.getCell(15).getNumericCellValue();
                        else s2L = 0;
                        if (secondRow.getCell(16, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null)
                            s2P = (int) secondRow.getCell(16).getNumericCellValue();
                        else s2P = 0;
                        int semester = 0;
                        if (s1C + s1S + s1L + s1P > 0) semester = 1;
                        else if (s2C + s2S + s2L + s2P > 0) semester = 2;
//profesori care predau la aceasta disciplina si grupe
                        if (semester > 0) {
                            int c = 27;
                            while (!secondRow.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().equals("")) {
                                c++;
                            }
                            int nrProfs = c - 27;
                            if (nrProfs > 0) {
                                Professor[] actualProfs = new Professor[nrProfs];
                                String[] profNames = new String[nrProfs];
                                int[] crsTime = new int[nrProfs];
                                int[] semTime = new int[nrProfs];
                                int[] labTime = new int[nrProfs];
                                int[] prcTime = new int[nrProfs];
                                int crsTotal = 0;
                                int semTotal = 0;
                                int labTotal = 0;
                                int prcTotal = 0;

                                for (int i = 0; i < nrProfs; i++) {
                                    String[] profData = secondRow.getCell(27 + i).toString().split(";");
                                    profNames[i] = profData[0];
                                    crsTime[i] = Integer.parseInt(profData[1]);
                                    crsTotal += crsTime[i];
                                    semTime[i] = Integer.parseInt(profData[2]);
                                    semTotal += semTime[i];
                                    labTime[i] = Integer.parseInt(profData[3]);
                                    labTotal += labTime[i];
                                    prcTime[i] = Integer.parseInt(profData[4]);
                                    prcTotal += prcTime[i];
                                    actualProfs[i] = new Professor(0, profNames[i]);
                                    actualProfs[i] = addIfNotInProfs(actualProfs[i], professors);
                                }

                                for (int i = 0; i < nrProfs; i++) {
                                    for (int j = i + 1; j < nrProfs; j++)
                                        if (actualProfs[i] != null && actualProfs[j] != null && actualProfs[i].equals(actualProfs[j])) {
                                            crsTime[i] += crsTime[j];
                                            crsTime[j] = 0;
                                            semTime[i] += semTime[j];
                                            semTime[j] = 0;
                                            labTime[i] += labTime[j];
                                            labTime[j] = 0;
                                            prcTime[i] += prcTime[j];
                                            prcTime[j] = 0;
                                            actualProfs[j] = null;
                                        }
                                }

                                int numberOfGroups = max(fCrs, fSem, fLab, fPrc);
                                Group[][] actualGroups = new Group[4][numberOfGroups * nrProfs];

                                for (int i = 0; i < numberOfGroups; i++) {
                                    actualGroups[0][i] = new Group(0, speciality[0], year, i + 1);
                                    actualGroups[0][i] = addIfNotInGroup(actualGroups[0][i], groups);
                                    for (int j = 1; j < 4; j++) {
                                        actualGroups[j][i] = actualGroups[0][i];
                                    }
                                }
                                for (int i = 1; i < nrProfs; i++) {
                                    for (int k = 0; k < numberOfGroups; k++) {
                                        for (int j = 0; j < 4; j++) {
                                            actualGroups[j][i * numberOfGroups + k] = actualGroups[j][k];
                                        }
                                    }
                                }

                                for (int i = 0; i < nrProfs; i++) {
                                    if (crsTime[i] > 0) {
                                        int type = 1;
                                        createActivities(activities, professors, groups, i, numberOfGroups, fCrs, actualGroups, subject, codeSubject, actualProfs, type, semester, year, crsTotal, crsTime, s1C + s2C);
                                    }
                                    if (semTime[i] > 0) {
                                        int type = 2;
                                        createActivities(activities, professors, groups, i, numberOfGroups, fSem, actualGroups, subject, codeSubject, actualProfs, type, semester, year, semTotal, semTime, s1C + s2C);
                                    }
                                    if (labTime[i] > 0) {
                                        int type = 3;
                                        createActivities(activities, professors, groups, i, numberOfGroups, fLab, actualGroups, subject, codeSubject, actualProfs, type, semester, year, labTotal, labTime, s1C + s2C + 1);
                                    }
                                    if (prcTime[i] > 0) {
                                        int type = 4;
                                        createActivities(activities, professors, groups, i, numberOfGroups, fPrc, actualGroups, subject, codeSubject, actualProfs, type, semester, year, prcTotal, prcTime, s1C + s2C + 1);
                                    }
                                }
                                if (s1C + s2C == 0) {
                                    System.out.println("No course here " + subject);
                                }
                            }
                        }
                    }
                }
                r=r+2;
            }
        }

        catch (Exception ex){
            System.out.println(ex.toString());
            return null;
        }
        return activities;
    }

    /*public static boolean loadData(String file, ArrayList<Profesor> profesors, ArrayList<Group> groups) throws IOException {

        profesors.clear();
        groups.clear();
        try {
            XStream xStream=new XStream();
            xStream.alias("profList", ProfList.class);
            xStream.alias("profesor",Profesor.class);
            File profFile=new File(file);
            ProfList profList=(ProfList) xStream.fromXML(file);
            profesors=profList.profesors;
        }
        catch (Exception ex){
            out.println(ex.toString());
        }
        if (profesors.isEmpty()) return false;
        return true;
    }*/

    public static boolean saveData(String file, ArrayList<Professor> professors, ArrayList<Group> groups, ArrayList<Activity> activities) throws IOException {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        FileWriter fileWriter=new FileWriter(file);

        try {
            gson.toJson(activities,fileWriter);
            gson.toJson(professors,fileWriter);
            gson.toJson(groups,fileWriter);
            System.out.println("Date salvate");
            fileWriter.close();
        }
        catch (Exception ex) {
            Utility.errorMessage("Clasa profesorilor nu a fost salavata");
            return false;
        }
        return true;
    }

    public static IndexedLabel createProfLabel(Activity currentActivity, Professor professor, ArrayList<Group> groups) {

        int[] groupId=new int[groups.size()];

        for (int i=0;i<groups.size();i++) {
            groupId[i]=groups.get(i).getIdGroup();
        }

        IndexedLabel lbl = new IndexedLabel(currentActivity.getIdActivity(), professor.getIdProfesor(),groupId);

        int time=currentActivity.getTime();
        lbl.setPrefSize(80,40*time);
        lbl.setFont(Font.font(8));
        lbl.setTextAlignment(TextAlignment.CENTER);
        lbl.setAlignment(Pos.CENTER);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-border:black;");
        StringBuilder groupsNames= new StringBuilder();
        for (int g = 0; g<currentActivity.getGroupsId().length; g++){
            groupsNames.append(groups.get(currentActivity.getGroupsId()[g]).getGroupName()).append(" ");
        }
        lbl.setText(groupsNames+"\n"+currentActivity.getCodeSubject()+","+currentActivity.getTypeChar());
        switch (currentActivity.getType()) {
            case 1:
                lbl.setStyle("-fx-background-color:LIGHTSALMON;"); break;
            case 2:
                lbl.setStyle("-fx-background-color:LIGHTBLUE;"); break;
            case 3:
                lbl.setStyle("-fx-background-color:LIGHTGREEN;"); break;
            case 4:
                lbl.setStyle("-fx-background-color:LIGHTORANGE;"); break;
            default:
                lbl.setStyle("-fx-background-color:BLACK;");
        }
        return lbl;
    }

    public static IndexedLabel createYearLabel(Activity currentActivity, Professor professor, ArrayList<Group> groups) {
        int[] groupId=new int[groups.size()];
        for (int i=0;i<groups.size();i++) {
            groupId[i]=groups.get(i).getIdGroup();
        }
        IndexedLabel lbl = new IndexedLabel(currentActivity.getIdActivity(), professor.getIdProfesor(),groupId);
        lbl.setPrefSize(60, 25);
        lbl.setFont(Font.font(8));
        lbl.setTextAlignment(TextAlignment.CENTER);
        lbl.setAlignment(Pos.CENTER);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-border:black;");
        StringBuilder groupsNames= new StringBuilder();
        lbl.setText(currentActivity.getCodeSubject()+","+currentActivity.getTypeChar()+"\n"+professor.getShortName());
        switch (currentActivity.getType()) {
            case 1:
                lbl.setStyle("-fx-background-color:LIGHTSALMON;"); break;
            case 2:
                lbl.setStyle("-fx-background-color:LIGHTBLUE;"); break;
            case 3:
                lbl.setStyle("-fx-background-color:LIGHTGREEN;"); break;
            case 4:
                lbl.setStyle("-fx-background-color:LIGHTORANGE;"); break;
            default:
                lbl.setStyle("-fx-background-color:BLACK;");
        }
        return lbl;
    }




}