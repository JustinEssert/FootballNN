import java.io.*;
import java.util.*;
import java.net.URL;

public class FootballMain {

	public static ArrayList<Team> teams = new ArrayList<Team>();

	static final int WEEK = 8;
	static final int YEAR = 2015;
	static final boolean EVAL = true;
	static int win = 0;
	static int lose = 0;

	public static void main(String[] args) throws Exception{
		//Checking for correct number of arguments

		for(int i=0;i<Config.TEAMNAMES.length;i++){
			teams.add(new Team(Config.TEAMNAMES[i]));
		}

		DataSet set = DataSet.createDataSet("./alldata.txt");//"../alldataw"+(WEEK-1)+".txt");
		
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for(int i=0; i<set.games.size();i++){
			for(int j=0; j<set.games.get(i).size(); j++){
				if(i>0||j>2){	
					for(int k=0;k<set.games.get(i).get(j).size();k++){
						if(set.games.get(i).get(j).get(k)!=null){
							instances.add(replayGame(set.games.get(i).get(j).get(k), set));
						}
					}
				}
			}
		}

		//Reading the weights
		Double[][] hiddenWeights=new Double[Config.NOHIDDENNODES][];

		for(int i=0;i<hiddenWeights.length;i++)
		{

			hiddenWeights[i]=new Double[instances.get(0).attributes.size()+1];
		}

		Double [][] outputWeights=new Double[instances.get(0).classValues.size()][];
		for (int i=0; i<outputWeights.length; i++) {
			outputWeights[i]=new Double[hiddenWeights.length+1];

		}

		readWeights(hiddenWeights,outputWeights);

		Double learningRate=Config.LEARNINGRATE;

		if(learningRate>1 || learningRate<=0)
		{
			System.out.println("Incorrect value for learning rate\n");
			System.exit(-1);
		}

		NNImpl nn=new NNImpl(instances,hiddenWeights,outputWeights);
		nn.train();
		if(EVAL) evalWeek(nn, set);
		else printWeek(nn, set);
		System.exit(0);
	}

	// Gets weights randomly
	public static void readWeights(Double[][] hiddenWeights, Double[][] outputWeights)
	{
		Random r = new Random();

		for(int i=0;i<hiddenWeights.length;i++)
		{
			for(int j=0;j<hiddenWeights[i].length;j++)
			{
				hiddenWeights[i][j] = r.nextDouble()*0.01;

			}
		}

		for(int i=0;i<outputWeights.length;i++)
		{
			for (int j=0; j<outputWeights[i].length; j++)
			{
				outputWeights[i][j] = r.nextDouble()*0.01;
			}
		}	
	}

	public static Instance createGame(Team away, Team home, DataSet set){

		ArrayList<Double> awayGames = away.calcAve(set.games, YEAR-Config.BASEYEAR, WEEK-1);
		ArrayList<Double> homeGames = home.calcAve(set.games, YEAR-Config.BASEYEAR, WEEK-1);
		ArrayList<Double> ave = new ArrayList<Double>();
		ArrayList<Double> score = new ArrayList<Double>();

		int marker = (awayGames.size()-2)/2;
		for(int i=0;i<marker;i++){
			ave.add((awayGames.get(i)+homeGames.get(i + marker))/2);
		}
		for(int i=0;i<marker;i++){
			ave.add((awayGames.get(i + marker)+homeGames.get(i))/2);
		}
		Instance inst = new Instance();
		inst.attributes = ave;
		inst.classValues = score;

		return inst;
	}

	public static Instance replayGame(Game game, DataSet set){

		ArrayList<Double> awayGames = game.away.calcAve(set.games, game.yrIndex, game.wkIndex);
		ArrayList<Double> homeGames = game.home.calcAve(set.games, game.yrIndex, game.wkIndex);

		Instance inst = new Instance();

		int marker = (awayGames.size()-2)/2;
		for(int i=0;i<marker;i++){
			inst.attributes.add((awayGames.get(i)+homeGames.get(i + marker))/2);
		}
		for(int i=0;i<marker;i++){
			inst.attributes.add((awayGames.get(i + marker)+homeGames.get(i))/2);
		}
		inst.classValues.add(game.data.get(game.data.size()-2));
		inst.classValues.add(game.data.get(game.data.size()-1));

		return inst;
	}	

	private static void printWeek(NNImpl nn, DataSet set) throws Exception{
		URL url = new URL("http://www.espn.com/nfl/scoreboard/_/year/2016/seasontype/2/week/" + WEEK);
		String lineA;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream()));

		System.out.println("Pred Score \t\tPred Dif\n");
		while ((lineA = in.readLine()) != null){
			if(lineA.contains("\"},\"records\":")){
				String data[] = lineA.split("\"},\"records\":");
				ArrayList<Team> teamlist = new ArrayList<Team>();
				for(int i=0;i<data.length-1;i+=1){

					String s1[] = data[i].split("\"");
					String s2 = s1[s1.length-1];
					for(int j=0;j<Config.TEAMNAMES.length;j++){
						if(s2.equals(Config.TEAMNAMES[j])){
							teamlist.add(teams.get(j));
						}
					}

				}
				for(int i=0;i<teamlist.size();i+=2){
					Team home = teamlist.get(i);
					Team away = teamlist.get(i+1);
					Instance inst = createGame(away, home, set);
					ArrayList<Double> out = nn.calcInstance(inst);
					int awayScore = (int)(out.get(0)*10);
					int homeScore = (int)(out.get(1)*10);

					System.out.print(away.name + ", " + awayScore + "\t" + 
							home.name + ", " + homeScore + "\t\t");
					if(awayScore>homeScore)
						System.out.println(away.name + " by " + (awayScore-homeScore));
					else System.out.println(home.name + " by " + (homeScore-awayScore));
				}
			}
		}
	}

	private static void evalWeek(NNImpl nn, DataSet set) throws Exception{
		URL url = new URL("http://www.espn.com/nfl/scoreboard/_/year/2016/seasontype/2/week/" + WEEK);
		String lineA;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream()));
		System.out.format("%-25s%-17s%-23s%-17s%-15s%s","Pred Score","Pred Dif","Actual Score","Actual Dif","Win/Lose","Error\n");
		while ((lineA = in.readLine()) != null){
			if(lineA.contains("/i/teamlogos/nfl/500/scoreboard/")){			
				String data[] = lineA.split("/i/teamlogos/nfl/500/scoreboard/");
				ArrayList<Team> teamlist = new ArrayList<Team>();
				ArrayList<Integer> scoreList = new ArrayList<Integer>();
				boolean bye=false;
				for(int i=1;i<data.length;i+=2){

					if(!data[i].contains("teams")) bye=true;
					if(!bye){

						String t2 = data[i].split(".png")[0].toUpperCase();
						Integer s2 = Integer.parseInt(data[i-1].split("\"score\":\"")[1].split("\"")[0]);
						String t1 = data[i+1].split(".png")[0].toUpperCase();
						Integer s1 = Integer.parseInt(data[i].split("\"score\":\"")[1].split("\"")[0]);

						for(int j=0;j<Config.TEAMNAMES.length;j++){
							if(t2.equals(Config.TEAMNAMES[j])){
								teamlist.add(teams.get(j));
								scoreList.add(s2);
							}
						}
						for(int j=0;j<Config.TEAMNAMES.length;j++){
							if(t1.equals(Config.TEAMNAMES[j])){
								teamlist.add(teams.get(j));
								scoreList.add(s1);
							}
						}	
					}

				}
				for(int i=0;i<teamlist.size();i+=2){
					Team home = teamlist.get(i);
					Integer sHome = scoreList.get(i);
					Team away = teamlist.get(i+1);
					Integer sAway = scoreList.get(i+1);
					Instance inst = createGame(away, home, set);
					ArrayList<Double> out = nn.calcInstance(inst);
					int awayScore = (int)(out.get(0)*10);
					int homeScore = (int)(out.get(1)*10);
					if(awayScore==homeScore){
						if(out.get(0)>out.get(1)) awayScore++;
						if(out.get(0)<out.get(1)) homeScore++;

					}
					System.out.format("%-5s%-4d %-4s %-10d", away.name + ",", awayScore, home.name + ",", homeScore);
					//System.out.print(away.name + ", " + awayScore + "\t" + 
					//		home.name + ", " + homeScore + "\t\t");

					int predDif = awayScore-homeScore;
					int actDif = sAway-sHome;

					if(predDif>0){
						System.out.format("%-3s %-2s %-10d", away.name, "by", predDif);
					}else if(predDif==0){
						System.out.format("%-17s", "Tie");
					}else{
						System.out.format("%-3s %-2s %-10d", home.name, "by", -predDif);
					}

					System.out.format("%-5s%-4d%-4s%-10d", away.name+",", sAway, home.name+",", sHome);

					if(actDif>0){
						System.out.format("%-3s %-2s %-10d", away.name, "by", actDif);
					}else if (actDif==0){
						System.out.format("%-17s", "Tie");
					}else{
						System.out.format("%-3s %-2s %-10d", home.name, "by", -actDif);
					}

					if(actDif>0&&predDif>0 || actDif<0&&predDif<0 || actDif==0&&predDif==0){
						System.out.format("%-15s%-7s%d\n", "Win Pick", "Off by", Math.abs(predDif - actDif));
						win++;
					}else if(predDif==0){
						System.out.format("%-15s%-7s%d\n", "No Pick", "Off by", actDif);
					}else{
						System.out.format("%-15s%-7s%d\n", "Lose Pick", "Off by", (predDif + actDif) );
						lose++;
					}

				}
				System.out.println("\nWin Percentage: \t" + (win*100/(win+lose)) + "% (" + win + "-" + lose + ")");
			}
		}
	}
}
