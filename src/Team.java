import java.util.*;

public class Team {
	String name;
	ArrayList<ArrayList<Integer>> gameIndex = new ArrayList<ArrayList<Integer>>();

	//constructor for a new team
	public Team(String name){
		this.name = name;
	}


	public Game findGame(ArrayList<Game> games){
		for(int i=0;i<games.size();i++){
			if(games.get(i).away==this||games.get(i).home==this){
				return games.get(i);
			}
		}
		return null;
	}


	public ArrayList<Double> calcAve(ArrayList<ArrayList<ArrayList<Game>>> games, int yrIndex, int wkIndex){

		if(yrIndex==0&&wkIndex<2) return null;

		int count = 0;
		double sum[] = new double[Config.NUMBEROFSTATS-2];

		for(int i=0;i<wkIndex-1;i++){
			Game game = findGame(games.get(yrIndex).get(i));
			if(game!=null){
				for(int j=0;j<sum.length;j++){
					sum[j] += game.data.get(j);
				}
			}
			count++;
		}
		if(wkIndex<2){
			Game game = findGame(games.get(yrIndex-1).get(Config.TOTALWEEKS-1));
			for(int j=0;j<sum.length;j++){
				sum[j] += game.data.get(j);
			}
			count++;
		}

		if(wkIndex<1){
			Game game = findGame(games.get(yrIndex-1).get(Config.TOTALWEEKS-2));
			for(int j=0;j<sum.length;j++){
				sum[j] += game.data.get(j);
			}
			count++;
		}
		ArrayList<Double> stats = new ArrayList<Double>();

		for(int i=0;i<sum.length;i++){
			stats.add(sum[i]/count);
		}
		return stats;
	}

}
