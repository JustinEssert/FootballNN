import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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

	public List<Game> games = new ArrayList<Game>(); // ordered list of instances
	

	/**
	 * Add instance to collection.
	 * 
	 * @param line begins with label
	 */
	public void addInstance(String line, boolean configure) {

		String[] splitline = line.split(Config.DELIMITER);

		
		
		if(splitline.length!=28){
			System.out.print("Non Compatible data " + splitline.length + line);
			System.exit(0);
		}

		ArrayList<Double> data = new ArrayList<Double>();
		Team away, home;
		away = FootballMain.teams.get((int)Double.parseDouble(splitline[0]));
		home = FootballMain.teams.get((int)Double.parseDouble(splitline[1]));

		for(int i=2;i<splitline.length;i++){
			if(i==4||i==8||i==16||i==20){
				data.add(Double.parseDouble(splitline[i])/100);
			}else if(i==3||i==5||i==10||i==15||i==17||i==22){
				data.add(Double.parseDouble(splitline[i])*10);
			}else if(i==6||i==7||i==11||i==12||i==18||i==19||i==23||i==24){
				data.add(Double.parseDouble(splitline[i]));
			}else {
				data.add(Double.parseDouble(splitline[i])/10);
			}
		}

		Game newGame = new Game(away, home, data, configure);
		if(!configure) games.add(newGame);
	}

	/**
	 * Verifies that two DataSets use the same values for labels and attributes as wells as the same
	 * ordering. Returns false otherwise.
	 */
	public static DataSet createDataSet(String file) {
		DataSet set = new DataSet();
		boolean configure = false;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) {
				String line = in.readLine();
				String prefix = line.substring(0, 2);
				if(prefix.equals("#%")) configure=true;
				else if (!prefix.equals("//")&&!prefix.equals("%%")&&!prefix.equals("##")) {
					set.addInstance(line, configure);
				}
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
