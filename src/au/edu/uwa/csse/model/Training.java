package au.edu.uwa.csse.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibLINEAR;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class Training {

	static public J48 tree = new J48();
	static public Logistic log = new Logistic();
	static public LibLINEAR libLinear = new LibLINEAR();
	static public NaiveBayes nb = new NaiveBayes();
	
	static public Instances generateInstances(String path) {
		Instances instances = null;
		try {
			instances = new Instances(new FileReader(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instances;
	}

	public static void DecisionTree(Instances instances) {
		
		try {
			tree.buildClassifier(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void DecisionTreeTest(Instances instances) {
		
		for (int index = 0; index < instances.numInstances(); index++) {
			Instance instance = instances.instance(index);
			double result;
			try {
				result = tree.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void LogisticRegression(Instances instances) {
		
		try {
			log.buildClassifier(instances);
			for (int index = 0; index < instances.numInstances(); index++) {
				Instance instance = instances.instance(index);
				double result = log.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void LogisticRegressionTest(Instances instances) {
		
		for (int index = 0; index < instances.numInstances(); index++) {
			Instance instance = instances.instance(index);
			double result;
			try {
				result = log.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void Linear(Instances instances) {
		
		try {
			libLinear.buildClassifier(instances);
			for (int index = 0; index < instances.numInstances(); index++) {
				Instance instance = instances.instance(index);
				double result = libLinear.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void LinearTest(Instances instances) {
		
		for (int index = 0; index < instances.numInstances(); index++) {
			Instance instance = instances.instance(index);
			double result;
			try {
				result = libLinear.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void NaiveBayes(Instances instances) {
		
		try {
			nb.buildClassifier(instances);
			for (int index = 0; index < instances.numInstances(); index++) {
				Instance instance = instances.instance(index);
				double result = nb.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void NaiveBayesTest(Instances instances) {
		
		for (int index = 0; index < instances.numInstances(); index++) {
			Instance instance = instances.instance(index);
			double result;
			try {
				result = nb.classifyInstance(instance);
				System.out.println(instance.toString() + ":" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		String path = "";
		Instances instances = Training.generateInstances(path);
		for (int n = 0; n < 10; n++) {
			Instances train = instances.trainCV(10, n);
			Instances test = instances.testCV(10, n);
			Training.DecisionTree(train);
			Training.DecisionTreeTest(test);
		}
	}

}
