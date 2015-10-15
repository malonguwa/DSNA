package au.edu.uwa.csse.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;

public class Feature {

	public static double getMean(List<Double> variables) {
		if (variables.size() == 0)
			return 0;
		double mean = 0;
		for (double variable : variables) {
			mean += variable;
		}
		return mean / variables.size();
	}

	public static double getVariance(List<Double> variables) {
		if (variables.size() == 0)
			return 0;
		double variance = 0;
		double mean = getMean(variables);
		for (double variable : variables) {
			variance += Math.pow(variable - mean, 2);
		}
		return Math.sqrt(variance) / variables.size();
	}

	public static String getDegree(Graph graph) {

		DegreeScorer ds = new DegreeScorer(graph);
		List<Double> scores = new ArrayList<Double>();
		for (Object v : graph.getVertices()) {
			scores.add((double) ds.getVertexScore(v));
		}
		double mean = getMean(scores);
		double variance = getVariance(scores);
		return String.format("%f, %f", mean, variance);
	}

	public static String getBetweenness(Graph graph) {
		BetweennessCentrality bc = new BetweennessCentrality(graph);
		List<Double> scores = new ArrayList<Double>();
		for (Object v : graph.getVertices()) {
			scores.add((double) bc.getVertexScore(v));
		}
		double mean = getMean(scores);
		double variance = getVariance(scores);
		return String.format("%f, %f", mean, variance);
	}

	public static String getCloseness(Graph graph) {
		ClosenessCentrality cc = new ClosenessCentrality(graph);
		List<Double> scores = new ArrayList<Double>();
		for (Object v : graph.getVertices()) {
			scores.add((double) cc.getVertexScore(v));
		}
		double mean = getMean(scores);
		double variance = getVariance(scores);
		return String.format("%f, %f", mean, variance);
	}

	public static double getAssortativity(Graph graph) {
		int edgeNumber = graph.getEdgeCount();
		double num1 = 0, num2 = 0, den1 = 0;
		Map<Number, Integer> degrees = new HashMap<Number, Integer>();
		Map<Number, Integer> degrees_sq = new HashMap<Number, Integer>();
		for (Object vertice : graph.getVertices()) {
			Number verticeInt = (Number) vertice;
			int degree = graph.degree(vertice);
			degrees.put(verticeInt, degree);
			degrees_sq.put(verticeInt, degree * degree);
		}
		for (Object edge : graph.getEdges()) {
			edge = (Integer) edge;
			Number target = (Number) graph.getDest(edge);
			Number source = (Number) graph.getSource(edge);
			num1 += degrees.get(source) * degrees.get(target);
			num2 += degrees.get(source) + degrees.get(target);
			den1 += degrees_sq.get(source) + degrees_sq.get(target);
		}
		num1 /= edgeNumber;
		den1 /= 2 * edgeNumber;
		num2 = Math.pow(num2 / (2 * edgeNumber), 2);
		return (num1 - num2) / (den1 - num2);
	}

	public static double getDiameter(Graph graph) {
		double diameter = DistanceStatistics.diameter(graph);
		return diameter;
	}

	public static String getDistance(Graph graph) {
		org.apache.commons.collections15.Transformer distances = DistanceStatistics
				.averageDistances(graph);
		List<Double> scores = new ArrayList<Double>();
		for (Object v : graph.getVertices()) {
			scores.add((Double) distances.transform(v));
		}
		double mean = getMean(scores);
		double variance = getVariance(scores);
		return String.format("%f, %f", mean, variance);
	}

	public static String getCluster(Graph graph) {
		EdgeBetweennessClusterer<Integer, Number> clusterer1 = new EdgeBetweennessClusterer<Integer, Number>(
				graph.getEdgeCount() / 4);
		Set<Set<Integer>> clusterSet1 = clusterer1.transform(graph);
		EdgeBetweennessClusterer<Integer, Number> clusterer2 = new EdgeBetweennessClusterer<Integer, Number>(
				graph.getEdgeCount() / 2);
		Set<Set<Integer>> clusterSet2 = clusterer2.transform(graph);
		EdgeBetweennessClusterer<Integer, Number> clusterer3 = new EdgeBetweennessClusterer<Integer, Number>(
				graph.getEdgeCount() * 3 / 4);
		Set<Set<Integer>> clusterSet3 = clusterer3.transform(graph);
		return String.format("%d, %d, %d", clusterSet1.size(), clusterSet2.size(), clusterSet3.size());
	}

	public static String getFeatures(Graph graph) {
		StringBuffer sb = new StringBuffer();
		sb.append(getDegree(graph) + ", ");
		sb.append(getBetweenness(graph) + ", ");
		sb.append(getCloseness(graph) + ", ");
		sb.append(getAssortativity(graph) + ", ");
		sb.append(getDistance(graph) + ", ");
		sb.append(getDiameter(graph) + ", ");
		sb.append(getCluster(graph));
		return sb.toString();
	}
}
