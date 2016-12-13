import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class organizes the information of a data set into simple structures. To speed up program
 * performance, the label value of an instance is stored as an Integer that reflect the position of
 * the label in the DataSet labels list. Similarly, the attribute values of an instance are stored
 * as Integers that reflect the position of that value in the list attributes[<attribute>]. See the
 * Instance class for more details. All ordering of attribute values in an instance follow the
 * ordering of the DataSet attributes list.
 *
 * Do not modify.
 */
public class DataSet {

	ArrayList<ArrayList<ArrayList<Game>>> games = new ArrayList<ArrayList<ArrayList<Game>>>();

	/**
	 * Add instance to collection.
	 * 
	 * @param line begins with label
	 */
	public void addInstance(String line) {

		String[] splitline = line.split(Config.DELIMITER);



		if(splitline.length!=30){
			System.out.print("Non Compatible data " + splitline.length + line);
			System.exit(0);
		}

		ArrayList<Double> data = new ArrayList<Double>();
		Team away, home;

		away = FootballMain.teams.get((int)Double.parseDouble(splitline[2]));
		home = FootballMain.teams.get((int)Double.parseDouble(splitline[3]));
		int year,week;
		year = (int)Double.parseDouble(splitline[0]);
		week = (int)Double.parseDouble(splitline[1]);
		
		if(week>Config.TOTALWEEKS){
			System.out.println("Incorrect number of weeks ," + week);
			System.exit(-1);
		}

		for(int i=4;i<splitline.length;i++){
			if(i==5||i==7||i==12||i==17||i==19||i==24){
				data.add((double)((((int)(Double.parseDouble(splitline[i])*100)))/10));
			}else if(i==8||i==9||i==13||i==14||i==20||i==21||i==25||i==26){
				data.add(Double.parseDouble(splitline[i]));
			}else if(i==4||i==11||i==15||i==16||i==23||i==27||i==28||i==29){
				data.add(Double.parseDouble(splitline[i])/10);
			}else {
				data.add(Double.parseDouble(splitline[i])/100);
			}
		}

		Game newGame = new Game(away, home, data, year, week);
		int yrIndex = year - Config.BASEYEAR;
		int wkIndex = week - 1;
		while(games.size()-1<yrIndex){
			games.add(new ArrayList<ArrayList<Game>>());
		}
		while(games.get(yrIndex).size()-1<wkIndex){
			games.get(yrIndex).add(new ArrayList<Game>());
		}
		games.get(yrIndex).get(wkIndex).add(newGame);
	}

	/**
	 * Verifies that two DataSets use the same values for labels and attributes as wells as the same
	 * ordering. Returns false otherwise.
	 */
	public static DataSet createDataSet(String file) {
		DataSet set = new DataSet();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) {
				String line = in.readLine();
				int prefix = Integer.parseInt(line.substring(0,4));
				if(prefix>=Config.BASEYEAR) set.addInstance(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}


		return set;
	}

	public static void printGame(int week){

	}



}
