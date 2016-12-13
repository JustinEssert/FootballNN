import java.util.ArrayList;
/**
 * Holds data for a particular instance.
 * Attributes are represented as an ArrayList of Doubles
 * Class labels are represented as an ArrayList of Integers. For example,
 * a 3-class instance will have classValues as [0 1 0] meaning this 
 * instance has class 1.
 * Do not modify
 */
 

public class Game {
	public ArrayList<Double> data;
	public Team away;
	public Team home;
	
	public Game(Team away, Team home, ArrayList<Double> data, boolean configure)
	{
		
		this.data= data;
		this.away = away;
		this.home = home;
		if(configure) Configure();
	}
	
	private void Configure(){
		home.addWeek(this);
		away.addWeek(this);
	}
	
}
	