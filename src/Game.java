import java.util.ArrayList;
/**
 * Holds data for a particular instance.
 * Attributes are represented as an ArrayList of Doubles
 * Class labels are represented as an ArrayList of Integers. For example,
 * a 3-class instance will have classValues as [0 1 0] meaning this 
 * instance has class 1.
 * Do not modify
 */
 

public class Game{
	int yrIndex,wkIndex;
	public ArrayList<Double> data;
	public Team away;
	public Team home;
	
	public Game(Team away, Team home, ArrayList<Double> data, int year, int week)
	{
		if(year<Config.BASEYEAR){
			System.out.println("year=" + year);
			System.exit(-1);
		}
		this.data= data;
		this.away = away;
		this.home = home;
		this.yrIndex = year-Config.BASEYEAR;
		this.wkIndex = week-1;
	}
	
}
	