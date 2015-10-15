package au.edu.uwa.csse;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import au.edu.uwa.csse.plot.Plot;

public class EditInfoParser {
	static public Map<String, Developer> developers = new HashMap<String, Developer>();
	static public Map<String, CodeFile> codeFiles = new HashMap<String, CodeFile>();
	static public Map<String, Map<String, Relationship>> relationships = new HashMap<String, Map<String, Relationship>>();
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
	static public Date startDate;
	static public Date endDate;

	static public final List<EditInfo> parse(final String fileName) {
		List<EditInfo> edits = new ArrayList<EditInfo>();
		FileReader fr;
		try {
			startDate = sdf.parse("2999-12-31");
			endDate = sdf.parse("1900-01-01");
			fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				EditInfo editInfo = new EditInfo(line, developers, codeFiles);
				if (editInfo.time.before(startDate)) {
					startDate = editInfo.time;
				} else if (editInfo.time.after(endDate)) {
					endDate = editInfo.time;
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CodeFile codeFile : codeFiles.values()) {
			Map<Developer, Integer> currDevelopers = codeFile.getDevelopers();
			Map<Developer, Date> modifyTimes = codeFile.getModifyTimes();
			for (Developer d1 : currDevelopers.keySet()) {
				for (Developer d2 : currDevelopers.keySet()) {
					if (d1.developerName.equals(d2.developerName))
						continue;
					Date day1 = modifyTimes.get(d1);
					Date day2 = modifyTimes.get(d2);

					if (relationships.containsKey(d1.developerName)) {
						Map<String, Relationship> tmpRelations = relationships
								.get(d1.developerName);
						if (tmpRelations.containsKey(d2.developerName))
						{
							double newWeight = tmpRelations.get(d2.developerName).weight + currDevelopers.get(d1)
									+ currDevelopers.get(d2);
							tmpRelations.put(d2.developerName,
									new Relationship(d1, d2, newWeight, day1
											.after(day2) ? day2 : day1));
						} else
						{
							tmpRelations.put(d2.developerName,
									new Relationship(d1, d2, currDevelopers.get(d1)
											+ currDevelopers.get(d2), day1
											.after(day2) ? day2 : day1));
						}
								
					} else {
						Relationship relationship = new Relationship(
								d1,
								d2,
								currDevelopers.get(d1) + currDevelopers.get(d2),
								day1.after(day2) ? day2 : day1);
						Map<String, Relationship> tmpRelations = new HashMap<String, Relationship>();
						tmpRelations.put(d2.developerName, relationship);
						relationships.put(d1.developerName, tmpRelations);
					}
				}
			}
		}
		return edits;
	}

	static public void visualize(Container content) {
		Plot.currDate = endDate;
		Plot.draw(content, developers, relationships);
	}

	static public void toMatix(String filePath) {
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(filePath)), true);
			String line = " ";
			for (String developer : developers.keySet()) {
				line += "\t" + developer;
			}
			pw.println(line);
			for (String developer : developers.keySet()) {
				double weight = 0;
				line = developer;
				Map<String, Relationship> currRelationship = relationships
						.get(developer);
				for (String otherDeveloper : developers.keySet()) {
					if (currRelationship != null
							&& currRelationship.containsKey(otherDeveloper)) {
						weight = currRelationship.get(otherDeveloper).weight;
					} else {
						weight = 0;
					}
					line += "\t" + weight;
				}
				pw.println(line);
			}
			pw.close();
		} catch (Exception e) {

		}

	}
	
	/*
	static public void fromMatrix(String filePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line = " ";
			for (String developer : developers.keySet()) {
				line += "\t" + developer;
			}
			pw.println(line);
			for (String developer : developers.keySet()) {
				double weight = 0;
				line = developer;
				Map<String, Relationship> currRelationship = relationships
						.get(developer);
				for (String otherDeveloper : developers.keySet()) {
					if (currRelationship != null
							&& currRelationship.containsKey(otherDeveloper)) {
						weight = currRelationship.get(otherDeveloper).weight;
					} else {
						weight = 0;
					}
					line += "\t" + weight;
				}
				pw.println(line);
			}
			pw.close();
		} catch (Exception e) {

		}
	}
	*/

	public static void main(String[] args) {
		final String DSN_PATH = "D:\\report\\job2012_2\\projects\\hbase.DSN";
		final String MATRIX_PATH = "d:\\test\\trunk.csv";
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();
		EditInfoParser.parse(DSN_PATH);
		EditInfoParser.visualize(content);
		EditInfoParser.toMatix(MATRIX_PATH);
		frame.pack();
		frame.setVisible(true);
	}

}