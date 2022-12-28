package runIt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the core functionality of the demographics program, utilizing the readstateFile class to
 * sort through and comprehend census data.
 * @author max
 *
 */
public class DemographicsMain {
    final static String[] STATE_NAMES = { "usa", "alabama", "alaska", "arkansas", "arizona", "california", "colorado",
            "connecticut", "delaware", "florida", "georgia", "hawaii", "iowa", "idaho", "illinois", "indiana", "kansas",
            "kentucky", "louisiana", "massachusetts", "maryland", "maine", "minnesota", "michigan", "missouri",
            "mississippi", "montana", "nebraska", "nevada", "new mexico", "new hampshire", "north carolina",
            "north dakota", "new york", "new jersey", "oklahoma", "oregon", "ohio", "pennsylvania", "rhode island",
            "south carolina", "south dakota", "tennessee", "texas", "utah", "vermont", "virginia", "washington",
            "wisconsin", "west virginia", "wyoming" };
    final static String FILE_PATH = "StateData/states.csv";
    final static int HWH = 10;
    final static int A_BUCK = 100;

    /**
     * Runs the main functionality. Provides many options for how a user can view data through multiple functions.
     * @param args just works
     * @throws IOException an exceptoion
     */
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        userOptions(s);
        System.out.print("Would you like to view a different data set. Enter (y)es or (n)o: ");
        char cont = s.next().toLowerCase().charAt(0);
        while(cont!='n') {
            userOptions(s);
            System.out.print("Would you like to view a different data set. Enter (y)es or (n)o: ");
            cont = s.next().toLowerCase().charAt(0);
        }
        s.close();

    }

    public static String getDataSet(Scanner s) {
        String input = "";
        boolean existsInSet = false;
        while(!existsInSet) {
            System.out.print("Enter the state you want the data for, or USA:");
            input = s.nextLine().toLowerCase();
            for(int i=0;i<STATE_NAMES.length;i++) {
                if(input.equals(STATE_NAMES[i])) {
                    existsInSet = true;
                }
            }
        }
        return input;
    }
    
    public static void userOptions(Scanner s) throws IOException {
        String data = getDataSet(s);
        ReadStateFile f;
        ArrayList<CountyData> counties;
        if (data.equals(STATE_NAMES[0])) {
            System.out.print("Would you like to see general(g) stats or demographic(d) based stats: ");
            char d = s.next().toLowerCase().charAt(0);
            while (d != 'g' && d != 'd') {
                System.out.print("Try Again. Would you like to see general(g) stats or demographic(d) based stats: ");
                d = s.next().toLowerCase().charAt(0);
            }
            if (d == 'g') {
                ArrayList<ReadStateFile> rs = wholeUSDataSet();
                for(int i=0;i<rs.size();i++) {
                    ArrayList<CountyData> cd = rs.get(i).getStateData();
                    basicStats(cd,STATE_NAMES[i+1]);
                    seeAllStats(cd,s);
                }
            }
            else {
                System.out.print("How many of the highest % white and highest % minority would you like to see? ");
                int num = s.nextInt();
                System.out.println("This will take a while, don't worry!");
                demographicStats(num, s);
            }
        } else {
            f = new ReadStateFile(new File(FILE_PATH), data);
            counties = f.getStateData();
            System.out.print("Would you like to see general(g) stats or demographic(d) based stats: ");
            char d = s.next().toLowerCase().charAt(0);
            while (d != 'g' && d != 'd') {
                System.out.print("Try Again. Would you like to see general(g) stats or demographic(d) based stats: ");
                d = s.next().toLowerCase().charAt(0);
            }
            if (d == 'g') {
                basicStats(counties, data);
                seeAllStats(counties, s);
                
            } else {
                if (counties.size() >= 2 * HWH) {
                    edgeDataStats(counties, data);
                    seeAllStats(counties,s);
                } else {
                    System.out.println("State does not have enough data to provide accurate demographics based data.");
                    seeAllStats(counties,s);
                }
            }
        }
    }

    public static void basicStats(ArrayList<CountyData> c, String state) {
        double averagePercentWhite = 0.0;
        double averageIncome = 0.0;
        for (int i = 0; i < c.size(); i++) {
            averagePercentWhite += c.get(i).getPercentWhite();
            averageIncome += c.get(i).getAverageIncome();
        }
        averagePercentWhite /= c.size();
        averageIncome /= c.size();
        double percentMinority = 100 - averagePercentWhite;
        System.out.println(state + " has " + c.size() + " counties.");
        System.out.printf("The average household income here is $%.2f.\n", averageIncome);
        System.out.printf("Demographics: %.2f%% white, %.2f%% minority\n\n", averagePercentWhite, percentMinority);
    }

    public static void edgeDataStats(ArrayList<CountyData> c, String state) {
        double averageStateIncome = 0.0;
        for (int i = 0; i < c.size(); i++) {
            averageStateIncome += c.get(i).getAverageIncome();
        }
        averageStateIncome /= c.size();
        double highestMinorityIncome = 0.0;
        for (int j = 0; j < HWH; j++) {
            highestMinorityIncome += c.get(j).getAverageIncome();
        }
        highestMinorityIncome /= HWH;
        double highestWhiteIncome = 0.0;
        for (int j = c.size() - 1; j > c.size() - 1 - HWH; j--) {
            highestWhiteIncome += c.get(j).getAverageIncome();
        }
        highestWhiteIncome /= HWH;
        System.out.println("\n" + state + " has " + c.size() + " counties.");
        System.out.printf("The average household income here is $%.2f.\n", averageStateIncome);
        System.out.printf(
                "In the counties with the highest minority population percentage, the average household income is $%.2f.\n",
                highestMinorityIncome);
        System.out.printf(
                "In the counties with the highest white population percentage, the average household income is $%.2f.\n",
                highestWhiteIncome);
    }

    private static ArrayList<CountyData> findKMostWhitestCounties(ArrayList<ReadStateFile> rs, int num){
        ArrayList<CountyData> counties = new ArrayList<CountyData>();
        ArrayList<CountyData> initialCounties = rs.get(0).getStateData();
        for(int i=initialCounties.size()-1;i>=0;i--) {
           counties.add(initialCounties.get(i));
           if(i>=num) {
               break;
           }
        }
        for(int i=1;i<rs.size();i++) {
            initialCounties = rs.get(i).getStateData();
            int checker = initialCounties.size()-1;
            while (checker >0) {
                CountyData cd = initialCounties.get(checker);
                // insert it where it belongs, break when done
                for (int k = 0; k < counties.size(); k++) {
                    if (cd.getPercentWhite() > counties.get(k).getPercentWhite()) {
                        counties.add(k, cd);
                        break;
                    }
                }
                // if lower than last index break
                if (cd.getPercentWhite() < counties.get(counties.size() - 1).getPercentWhite()) {
                    break;
                }
                if (counties.size() > num) {
                    counties.remove(counties.size() - 1);
                }
                checker--;
            }
        }
        return counties;
    }

    private static ArrayList<CountyData> findKMostNotWhitestCounties(ArrayList<ReadStateFile> rs, int num){
        ArrayList<CountyData> counties = new ArrayList<CountyData>();
        ArrayList<CountyData> initialCounties = rs.get(0).getStateData();
        for(int i=0;i<initialCounties.size();i++) {
           counties.add(initialCounties.get(i));
           if(i>=num) {
               break;
           }
        }
        for(int i=1;i<rs.size();i++) {
            initialCounties = rs.get(i).getStateData();
            int checker = 0;
            while (checker < initialCounties.size()) {
                CountyData cd = initialCounties.get(checker);
                // insert it where it belongs, break when done
                for (int k = 0; k < counties.size(); k++) {
                    if (cd.getPercentMinority() > counties.get(k).getPercentMinority()) {
                        counties.add(k, cd);
                        break;
                    }
                }
                // if lower than last index break
                if (cd.getPercentMinority() < counties.get(counties.size() - 1).getPercentMinority()) {
                    break;
                }
                if (counties.size() > num) {
                    counties.remove(counties.size() - 1);
                }
                checker++;
            }
        }
        return counties;
    }

    public static void demographicStats(int num, Scanner s) throws IOException {

        ArrayList<ReadStateFile> rs = wholeUSDataSet();
        ArrayList<CountyData> white = findKMostWhitestCounties(rs, num);
        ArrayList<CountyData> notWhite = findKMostNotWhitestCounties(rs, num);
        double highestMinorityIncome = 0.0;
        for (int i = 0; i < num; i++) {
            highestMinorityIncome += notWhite.get(i).getAverageIncome();
        }
        highestMinorityIncome /= num;
        double highestWhiteIncome = 0.0;
        for (int i = 0; i < num; i++) {
            highestWhiteIncome += white.get(i).getAverageIncome();
        }
        highestWhiteIncome /= num;
        System.out.printf(
                "In the counties with the highest minority population percentage, the average household income is $%.2f.\n",
                highestMinorityIncome);
        System.out.printf(
                "In the counties with the highest white population percentage, the average household income is $%.2f.\n",
                highestWhiteIncome);
        System.out.print("\nWould you like to see the counties with the highest minority population percentage.");
        seeAllStats(notWhite,s);
        System.out.print("\nWould you like to see the counties with the highest white population percentage.");
        seeAllStats(white,s);
        
    }
    
    public static ArrayList<ReadStateFile> wholeUSDataSet() throws IOException{
        ArrayList<ReadStateFile> rs = new ArrayList<ReadStateFile>();
        for (int i = 1; i < STATE_NAMES.length; i++) {
            ReadStateFile f = new ReadStateFile(new File(FILE_PATH), STATE_NAMES[i]);
            rs.add(f);
        }
        return rs;
    }
    
    public static void seeAllStats(ArrayList<CountyData> c, Scanner s) {
        System.out.print("Do you want to see all data, enter yes or no: ");
        c.sort(new CountyDataByNameComparator());
        if (s.next().toLowerCase().charAt(0) == 'y') {
            for (CountyData d : c) {
                d.print();
            }
        }
    }
}
