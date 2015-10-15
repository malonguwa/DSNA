package au.edu.uwa.csse.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import au.edu.uwa.csse.EditInfoParser;
import au.edu.uwa.csse.plot.Plot;
import edu.uci.ics.jung.graph.Graph;

public class Model {
	public static void main(String[] args) {
		String localPath = "d:\\report\\job2012_2\\";
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(localPath + "model\\training.arff", false)));
			String line = "@RELATION DSN";
			pw.println(line);
			line = "@ATTRIBUTE degree_mean NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE degree_variance NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE betweenness_mean NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE betweenness_variance NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE closeness_mean NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE closeness_variance NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE assortativity NUMERIC";
			pw.println(line);
			line = "@ATTRIBUTE class {fine, problematic}";
			pw.println(line);
			line = "@DATA";
			pw.println("\n" + line);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileReader fr = new FileReader(localPath +  "model\\instances.txt");
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String segs[] = line.trim().split("\t");
				EditInfoParser.parse(localPath + "projects\\" + segs[0] + ".DSN");
				Plot.currDate = EditInfoParser.endDate;
				Graph graph = Plot.generateGraph(EditInfoParser.developers,
						EditInfoParser.relationships);
				String feature = Feature.getFeatures(graph);
				pw.println(feature + segs[1]);
			}
			pw.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
