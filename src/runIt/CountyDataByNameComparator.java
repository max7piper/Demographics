package runIt;
import java.util.Comparator;
public class CountyDataByNameComparator implements Comparator<CountyData> {


    @Override
    public int compare(CountyData o1, CountyData o2) {
        return o1.getCountyName().compareTo(o2.getCountyName());
    }
}
