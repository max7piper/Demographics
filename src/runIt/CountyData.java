package runIt;

/**
 * An object that stores specific data about US counties.
 * @author max
 *
 */
public class CountyData implements Comparable<CountyData> {
    private int countyPop;
    private int whitePop;
    private int minorityPop;
    private double percentWhite;
    private double percentMinority;
    private int averageIncome;
    private String countyName;

    /**
     * Constructor for a countyData Object.
     * @param countyName a String representing the name of the county
     * @param whitePop an int representing the white population of the county
     * @param minorityPop an int representing the minority population of the county
     * @param totalPop an int representing the total population of the county
     * @param averageIncome an int representing the average income of the county
     */
    public CountyData(String countyName, int whitePop, int minorityPop, int totalPop, int averageIncome) {
        this.countyName = countyName;
        this.countyPop = totalPop;
        this.whitePop = whitePop;
        this.minorityPop = minorityPop;
        this.setAverageIncome(averageIncome);
        this.percentWhite = ((double) this.whitePop / this.countyPop) * 100;
        this.percentMinority = ((double) this.minorityPop / this.countyPop) * 100;
    }

    @Override
    /**
     * a compareTo function for countyData, comparing them by their percentWhite first, then by name
     * @param o an object of the CountyData class
     * @return an int, representing a positive if it is whiter, or a negative if it is more minority.
     */
    public int compareTo(CountyData o) {
        if(this.percentWhite>o.percentWhite) {
            return 1;
        }
        else if(this.percentWhite<o.percentWhite) {
            return -1;
        }
        else if(this.countyName.compareTo(o.getCountyName())>0){
            return 1;
        }
        return -1;
    }

    /**
     * Prints out the county.
     */
    public void print() {
        System.out.printf("%s\nPercent White: %.2f%% \nPercent Minority: %.2f%%\nAverage Income: $%d\n",this.countyName, this.percentWhite, this.percentMinority,this.averageIncome);
        System.out.println("Total Population: " + this.countyPop  + "\n");
    }
    /*
     * BELOW ARE SOME GETTERS AND SETTERS I DID NOT WRITE JAVADOCS FOR
     * 
     */
    public double getPercentWhite() {
        return this.percentWhite;
    }

    public double getPercentMinority() {
        return percentMinority;
    }

    public void setPercentMinority(double percentMinority) {
        this.percentMinority = percentMinority;
    }

    public int getAverageIncome() {
        return averageIncome;
    }

    public void setAverageIncome(int averageIncome) {
        this.averageIncome = averageIncome;
    }
    public String getCountyName() {
        return this.countyName;
    }
}
