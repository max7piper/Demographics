package runIt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
/**
 * Reads the US Census CSV file and stores the data from each of the counties.
 * @author max sievenpiper
 *
 */
public class ReadStateFile {

    private static final String COUNTY_NAME = "CTYNAME";
    private static final String COUNTY_FINDER = "COUNTYNAME";
    private static final String INCOME = "INC";
    private static final String TOTAL_POP = "TOT_POP";
    private static final String WHITE_POP = "WA_MALE";
    private static final String WHITE_WOMEN_POP = "WA_FEMALE";
    private static final String STATE = "STNAME";
    private ArrayList<CountyData> stateCounties;
    private CSVParser income;
    private CSVParser stateDemo;
    private String statename;

    /**
     * Gathers, sorts and stores the county demographic and income data of any US state.
     * 
     * @param f, the file we will be reading
     * @param statename, the state we are looking for
     * @throws IOException, an exception
     */
    public ReadStateFile(File f, String statename) throws IOException {
        this.statename = statename;
        readTheData(f);
        sortData();
    }

    /**
     * Reads the data from the file and stores it in the state county array list.
     * 
     * @param f, the file we read from
     * @throws IOException, an exception
     */
    private void readTheData(File f) throws IOException {
        try {
            // creates file reader and csvParser, storing all data in lists
            FileReader fe = new FileReader(f);
            FileReader money = new FileReader(new File("StateData/lapi1.csv"));
            stateDemo = new CSVParser(fe, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            income = new CSVParser(money, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            List<CSVRecord> records = stateDemo.getRecords();
            List<CSVRecord> incomes = income.getRecords();
            int indexOfCountyForIncomes = 0;

            // sorts the us income table and stops when it gets to the state
            for (int i = 0; i < incomes.size(); i++) {
                if (incomes.get(i).get(COUNTY_FINDER).equals("")
                        && incomes.get(i + 1).get(COUNTY_FINDER).toLowerCase().equals(statename.toLowerCase())) {
                    indexOfCountyForIncomes = i + 2;
                    break;
                }
            }

            // reads through the county data recording the sums of the total population, white population and income
            stateCounties = new ArrayList<CountyData>();
            String countyName = records.get(0).get(COUNTY_NAME);
            int countySum = 0;
            int whiteSum = 0;
            String countyIncomeString = "";
            int countyIncome = 0;
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                // if we reach the correct state
                if (this.statename.toLowerCase().equals(records.get(i).get(STATE).toLowerCase())) {
                    // checks if we changed counties, if we do we continue summing the population
                    if (countyName.equals(records.get(i).get(COUNTY_NAME))) {
                        countySum += Integer.parseInt(records.get(i).get(TOTAL_POP));
                        whiteSum += Integer.parseInt(records.get(i).get(WHITE_POP))
                                + Integer.parseInt(records.get(i).get(WHITE_WOMEN_POP));
                    } else {
                        //stores the data when the county changes
                        countySum = Integer.parseInt(records.get(i).get(TOTAL_POP));
                        countyName = records.get(i).get(COUNTY_NAME);
                        whiteSum = Integer.parseInt(records.get(i).get(WHITE_POP))
                                + Integer.parseInt(records.get(i).get(WHITE_WOMEN_POP));
                        countyIncomeString = (incomes.get(indexOfCountyForIncomes).get(INCOME));
                        //gathers the average income from a string ie 6,466.
                        //Splits it and then parses it to an int, does not store if the file contains no cincome
                        String[] temp = countyIncomeString.split(",");
                        String toParse = "";
                        for (String s : temp) {
                            toParse += s;
                        }
                        try {
                            countyIncome = Integer.parseInt(toParse);
                        } catch (Exception e) {
                            continue;
                        }
                        indexOfCountyForIncomes++;
                        int minorityPop = countySum - whiteSum;
                        CountyData c = new CountyData(countyName, whiteSum, minorityPop, countySum, countyIncome);
                        stateCounties.add(c);
                    }
                    found = true;
                    continue;
                }
                //after dealing with our state the loop breaks.
                if (found) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stateDemo.close();
            income.close();
        }
    }

    /** 
     * Sorts the data arrayList based on the percent minority
     */
    private void sortData() {
        stateCounties.sort(null);
    }

    /**
     * Gathers an array of sum size of the counties with the highest minority percentage.
     * @param sum, the size of the array
     * @return an array of CountyData Objects
     */
    public CountyData[] getHighestMinorityCounties(int sum) {
        CountyData[] c = new CountyData[sum];
        for (int i = 0; i < c.length; i++) {
            c[i] = stateCounties.get(i);
        }
        return c;
    }

    /**
     * Gathers an array of sum size of the counties with the highest white percentage.
     * @param sum, the size of the array
     * @return an array of CountyData Objects
     */
    public CountyData[] getWhitestCounties(int sum) {
        CountyData[] c = new CountyData[sum];
        for (int i = 0; i < c.length; i++) {
            c[i] = stateCounties.get(stateCounties.size() - 1 - i);
        }
        return c;
    }

    /**
     * Getter for the state data arrayList
     * @return the state data arrayList, which is an arrayList of CountyData objects
     */
    public ArrayList<CountyData> getStateData() {
        return this.stateCounties;
    }
}
