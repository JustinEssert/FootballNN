import java.util.*;

public class Team {
	String name;
	ArrayList<Game> games = new ArrayList<Game>();

	//constructor for a new team
	public Team(String name){
		this.name = name;
	}

	//Method for adding stats for a specific week
	//needs an Double array with a length of 14
	public void addWeek(Game game){
		games.add(game);
	}	
	
	public ArrayList<Double> calcAve(){
		
		ArrayList<Double> stats = new ArrayList<Double>();
		
		for(int i = 0; i< Config.NUMBEROFSTATS; i++){
			Double count = 0.0;
			for(int j=0;j<games.size();j++){
				count += games.get(j).data.get(i);
			}
			stats.add(count/games.size());
		}
		return stats;
	}
	
}
