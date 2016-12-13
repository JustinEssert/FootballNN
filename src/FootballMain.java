import java.io.*;
import java.util.*;
import java.net.URL;

public class FootballMain {

	public static ArrayList<Team> teams = new ArrayList<Team>();

	static final int WEEK = 7;
	static final boolean EVAL = true;
	static int win = 0;
	static int lose = 0;

	public static void main(String[] args) throws Exception{
		//Checking for correct number of arguments

		for(int i = 0; i< Config.TEAMNAMES.length; i++){
			teams.add(new Team(Config.TEAMNAMES[i]));
		}

		DataSet set = DataSet.createDataSet("alldataw"+(WEEK-1)+".txt");

		ArrayList<Instance> instances = new ArrayList<Instance>();
		for(int i=0; i<set.games.size();i++){
			Instance inst = new Instance();
			for(int j=0;j<set.games.get(i).data.size()-2;j++){
				inst.attributes.add(set.games.get(i).data.get(j));
			}
			inst.classValues.add((set.games.get(i).data.get(set.games.get(i).data.size() - 2)));
			inst.classValues.add((set.games.get(i).data.get(set.games.get(i).data.size() - 1)));
			instances.add(inst);
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

		Double learningRate= Config.LEARNINGRATE;

		if(learningRate>1 || learningRate<=0)
		{
			System.out.println("Incorrect value for learning rate\n");
			System.exit(-1);
		}

		NNImpl nn=new NNImpl(instances,hiddenWeights,outputWeights);
		nn.train();
		if(EVAL) evalWeek(nn);
		else printWeek(nn);

	}

	// Gets weights randomly
	public static void readWeights(Double [][]hiddenWeights, Double[][]outputWeights)
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

	public static Instance createGame(Team away, Team home){

		ArrayList<Double> awayGames = away.calcAve();
		ArrayList<Double> homeGames = home.calcAve();
		ArrayList<Double> ave = new ArrayList<Double>();
		ArrayList<Double> score = new ArrayList<Double>();

		int marker = (awayGames.size()-2)/2;
		for(int i=0;i<marker;i++){
			ave.add((awayGames.get(i)+homeGames.get(i + marker))/2);
		}
		for(int i=0;i<marker;i++){
			ave.add((awayGames.get(i + marker)+homeGames.get(i))/2);
		}
		score.add(((awayGames.get(Config.NUMBEROFSTATS-1) + homeGames.get(Config.NUMBEROFSTATS-2))/2));
		score.add(((awayGames.get(Config.NUMBEROFSTATS-2) + homeGames.get(Config.NUMBEROFSTATS-1))/2));

		Instance inst = new Instance();
		inst.attributes = ave;
		inst.classValues = score;

		return inst;

	}

	private static void printWeek(NNImpl nn) throws Exception{
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
					for(int j = 0; j< Config.TEAMNAMES.length; j++){
						if(s2.equals(Config.TEAMNAMES[j])){
							teamlist.add(teams.get(j));
						}
					}

				}
				for(int i=0;i<teamlist.size();i+=2){
					Team home = teamlist.get(i);
					Team away = teamlist.get(i+1);
					Instance inst = createGame(away, home);
					ArrayList<Double> out = nn.calcInstance(inst);
					int awayScore = (int)((out.get(0))*10);
					int homeScore = (int)((out.get(1))*10);

					System.out.print(away.name + ", " + awayScore + "\t" + 
							home.name + ", " + homeScore + "\t\t");
					if(awayScore>homeScore)
						System.out.println(away.name + " by " + (awayScore-homeScore));
					else System.out.println(home.name + " by " + (homeScore-awayScore));
				}
			}
		}
	}

	private static void evalWeek(NNImpl nn) throws Exception{
		URL url = new URL("http://www.espn.com/nfl/scoreboard/_/year/2016/seasontype/2/week/" + WEEK);
		String lineA;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream()));
		System.out.println("Pred Score \t\tPred Dif \tActual Score \t\tActual Dif \t Win/Lose \t Error\n");
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

						for(int j = 0; j< Config.TEAMNAMES.length; j++){
							if(t2.equals(Config.TEAMNAMES[j])){
								teamlist.add(teams.get(j));
								scoreList.add(s2);
							}
						}
						for(int j = 0; j< Config.TEAMNAMES.length; j++){
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
					Instance inst = createGame(away, home);
					ArrayList<Double> out = nn.calcInstance(inst);
					int awayScore = (int)((out.get(0))*10);
					int homeScore = (int)((out.get(1))*10);

					System.out.print(away.name + ", " + awayScore + "\t" + 
							home.name + ", " + homeScore + "\t\t");
					
					boolean predHWin;
					boolean hWin;
					
					int predDif = Math.abs(awayScore-homeScore);
					int actDif = Math.abs(sAway-sHome);
					if(awayScore>homeScore){
						System.out.print(away.name + " by " + predDif + "  ");
						predHWin=false;
					}else{
						System.out.print(home.name + " by " + predDif + "  ");
						predHWin=true;
					}

					System.out.print("\t" + away.name + ", " + sAway + "\t" +
							home.name + ", " + sHome + "\t\t");
					
					if(sAway>sHome){
						System.out.print(away.name + " by " + actDif + "  ");
						hWin=false;
					}else{
						System.out.print(home.name + " by " + actDif + "  ");
						hWin=true;
					}
					if(hWin&&predHWin || !hWin&&!predHWin){
						System.out.println("\tWin Pick \tOff by " + Math.abs(predDif - actDif) );
						win++;
					}else if(predDif==0){
						System.out.println("\tNo Pick \tOff by " + actDif);
					}else{
						System.out.println("\tLose Pick \tOff by " + (predDif + actDif) );
						lose++;
					}
					
				}
				System.out.println("\nWin Percentage: \t" + (win*100/(win+lose)) + "% (" + win + "-" + lose + ")");
			}
		}
	}
}
