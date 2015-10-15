package au.edu.uwa.csse.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;

import au.edu.uwa.csse.Developer;
import au.edu.uwa.csse.EditInfoParser;
import au.edu.uwa.csse.Relationship;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;

public class Plot {
	public static Date currDate;
	public static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 */
	private static final long serialVersionUID = -6077157664507049647L;

	/**
	 * the graph
	 */
	static Graph<Integer, Number> graph;

	/**
	 * the visual component and renderer for the graph
	 */
	static VisualizationViewer<Integer, Number> vv;

	/**
     */
	static VertexLabelRenderer vertexLabelRenderer;
	static EdgeLabelRenderer edgeLabelRenderer;

	static ScalingControl scaler = new CrossoverScalingControl();

	static Map<Integer, String> verticesToNames = new HashMap<Integer, String>();
	static Map<String, Integer> namesToVertices = new HashMap<String, Integer>();

	static Map<Number, String> edges = new HashMap<Number, String>();
	static Map<Number, Double> weights = new HashMap<Number, Double>();
	static private Map<Integer, String> vertexLabels = new HashMap<Integer, String>();
	static public Transformer<Integer, String> vertexLabelTransformer = new Transformer<Integer, String>() {

		public String transform(Integer i) {
			return vertexLabels.get(i);
		}
	};

	static public Graph generateGraph(Map<String, Developer> developers,
			final Map<String, Map<String, Relationship>> relationships) {
		graph = new SparseMultigraph<Integer, Number>();
		createVertices(developers.values());
		createEdges(relationships);
		return graph;
	}

	/**
	 * create an instance of a simple graph with controls to demo the label
	 * positioning features
	 * 
	 */
	@SuppressWarnings("serial")
	static public VisualizationViewer<Integer, Number> draw(Container content,
			Map<String, Developer> developers,
			final Map<String, Map<String, Relationship>> relationships) {

		// create a simple graph for the demo
		graph = new SparseMultigraph<Integer, Number>();

		createVertices(developers.values());
		createEdges(relationships);

		final Layout<Integer, Number> layout = new CircleLayout<Integer, Number>(
				graph);
		vv = new VisualizationViewer<Integer, Number>(layout, new Dimension(
				600, 400));
		vv.setBackground(Color.white);

		vertexLabelRenderer = vv.getRenderContext().getVertexLabelRenderer();
		edgeLabelRenderer = vv.getRenderContext().getEdgeLabelRenderer();

		Transformer<Number, String> stringer = new Transformer<Number, String>() {
			public String transform(Number e) {
				return "RelationShip:" + graph.getEndpoints(e).toString() + ":"
						+ weights.get(e);
			}
		};
		vv.getRenderContext().setEdgeLabelTransformer(stringer);
		vv.getRenderContext().setEdgeDrawPaintTransformer(
				new PickableEdgePaintTransformer<Number>(vv
						.getPickedEdgeState(), Color.black, Color.cyan));
		vv.getRenderContext().setVertexFillPaintTransformer(
				new PickableVertexPaintTransformer<Integer>(vv
						.getPickedVertexState(), Color.red, Color.yellow));
		// add my listener for ToolTips
		vv.setVertexToolTipTransformer(vertexLabelTransformer);

		// create a frame to hold the graph
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		// Container content = getContentPane();
		content.add(panel);

		final DefaultModalGraphMouse<Integer, Number> graphMouse = new DefaultModalGraphMouse<Integer, Number>();
		vv.setGraphMouse(graphMouse);

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		ButtonGroup radio = new ButtonGroup();
		JRadioButton lineButton = new JRadioButton("Line");
		lineButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(
							new EdgeShape.Line<Integer, Number>());
					vv.repaint();
				}
			}
		});

		JRadioButton quadButton = new JRadioButton("QuadCurve");
		quadButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(
							new EdgeShape.QuadCurve<Integer, Number>());
					vv.repaint();
				}
			}
		});

		JRadioButton cubicButton = new JRadioButton("CubicCurve");
		cubicButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(
							new EdgeShape.CubicCurve<Integer, Number>());
					vv.repaint();
				}
			}
		});
		radio.add(lineButton);
		radio.add(quadButton);
		radio.add(cubicButton);

		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		JCheckBox rotate = new JCheckBox("Parallel");
		rotate.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton) e.getSource();
				edgeLabelRenderer.setRotateEdgeLabels(b.isSelected());
				vv.repaint();
			}
		});
		rotate.setSelected(true);
		MutableDirectionalEdgeValue mv = new MutableDirectionalEdgeValue(.5, .7);
		vv.getRenderContext().setEdgeLabelClosenessTransformer(mv);

		JButton clusterButton = new JButton("Cluster");
		clusterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Clustering");
				frame.setSize(400, 3000);
				Clustering cluster = new Clustering();
				cluster.start(graph);
				frame.add(cluster);
				frame.setVisible(true);
			}
		});

		JSlider edgeOffsetSlider = new JSlider(0, 50) {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width /= 2;
				return d;
			}
		};
		edgeOffsetSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				AbstractEdgeShapeTransformer<Integer, Number> aesf = (AbstractEdgeShapeTransformer<Integer, Number>) vv
						.getRenderContext().getEdgeShapeTransformer();
				aesf.setControlOffsetIncrement(s.getValue());
				vv.repaint();
			}

		});

		final JLabel timeText = new JLabel();
		String s = formatter.format(currDate);
		timeText.setText(s);

		JSlider timeSlider = new JSlider(0, 100) {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				return d;
			}
		};
		timeSlider.setPaintLabels(true);
		timeSlider.setValue(100);

		timeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				float pos = ((float) s.getValue()) / 100;
				long delta = EditInfoParser.endDate.getTime()
						- EditInfoParser.startDate.getTime();
				int days = (int) (delta / 1000 / 60 / 60 / 24);
				days = (int) (pos * days);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(EditInfoParser.startDate);
				calendar.add(Calendar.DATE, days);
				currDate = calendar.getTime();
				timeText.setText(formatter.format(calendar.getTime()));
				// JOptionPane.showMessageDialog(null, "Time",
				// String.format("%f", pos),JOptionPane.ERROR_MESSAGE);
				vv.repaint();
			}

		});

		JButton changeTimeButton = new JButton("Draw");
		changeTimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEdges(relationships);
				vv.repaint();
			}
		});

		JPanel slidePanel = new JPanel(new GridLayout(2, 2));
		slidePanel.setBorder(BorderFactory.createTitledBorder("Time"));
		slidePanel.add(edgeOffsetSlider);
		slidePanel.add(timeSlider);
		slidePanel.add(timeText);
		slidePanel.add(changeTimeButton);

		Box controls = Box.createHorizontalBox();
		Box info = Box.createVerticalBox();
		info.setMinimumSize(new Dimension(300, 600));

		JPanel zoomPanel = new JPanel(new GridLayout(0, 1));
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Scale"));
		zoomPanel.add(plus);
		zoomPanel.add(minus);

		JPanel edgePanel = new JPanel(new GridLayout(0, 1));
		edgePanel.setBorder(BorderFactory.createTitledBorder("EdgeType Type"));
		edgePanel.add(lineButton);
		edgePanel.add(quadButton);
		edgePanel.add(cubicButton);

		JPanel rotatePanel = new JPanel(new GridLayout(0, 1));
		rotatePanel.setBorder(BorderFactory.createTitledBorder("Mode"));
		rotatePanel.add(rotate);
		rotatePanel.add(graphMouse.getModeComboBox());

		JPanel clusterPanel = new JPanel(new GridLayout(0, 1));
		clusterPanel.setBorder(BorderFactory.createTitledBorder("Clustering"));
		clusterPanel.add(clusterButton);

		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.add(rotatePanel, BorderLayout.WEST);

		controls.add(zoomPanel);
		controls.add(edgePanel);
		controls.add(labelPanel);
		controls.add(clusterPanel);
		controls.add(slidePanel);

		StringBuffer degreeStr = new StringBuffer(
				"<html><body style=\"width:140\">");
		StringBuffer betweennessStr = new StringBuffer(
				"<html><body style=\"width:140\">");
		StringBuffer closenessStr = new StringBuffer(
				"<html><body style=\"width:140\">");
		StringBuffer hitsStr = new StringBuffer(
				"<html><body style=\"width:140\">");
		StringBuffer eigenvectorStr = new StringBuffer(
				"<html><body style=\"width:140\">");

		BetweennessCentrality bc = new BetweennessCentrality(graph);
		DegreeScorer ds = new DegreeScorer(graph);
		ClosenessCentrality cc = new ClosenessCentrality(graph);
		HITS hits = new HITS(graph);
		EigenvectorCentrality ec = new EigenvectorCentrality(graph);

		for (int step = 0; step < hits.getMaxIterations(); step++)
			hits.step();
		for (int step = 0; step < ec.getMaxIterations(); step++)
			ec.step();
		System.out.println(hits.getMaxIterations() + ":"
				+ ec.getMaxIterations());

		for (Number v : graph.getVertices()) {
			String name = verticesToNames.get(v);
			degreeStr.append(name + "\t" + ds.getVertexScore(v) + "<br>");
			betweennessStr.append(name + "\t" + bc.getVertexScore(v) + "<br>");
			closenessStr.append(name + "\t" + cc.getVertexScore(v) + "<br>");
			hitsStr.append(name + "\t" + hits.getVertexScore(v) + "<br>");
			eigenvectorStr.append(name + "\t" + ec.getVertexScore(v) + "<br>");
		}
		degreeStr.append("</body></html>");
		betweennessStr.append("</body></html>");
		closenessStr.append("</body></html>");
		hitsStr.append("</body></html>");
		eigenvectorStr.append("</body></html>");

		JPanel degreePanel = new JPanel();
		degreePanel.setPreferredSize(new Dimension(200, 300));
		degreePanel.setBorder(BorderFactory.createTitledBorder("Degree"));
		JLabel degreeLabel = new JLabel();
		degreeLabel.setText(degreeStr.toString());
		degreePanel.add(degreeLabel);
		info.add(degreePanel);

		JPanel betweennessPanel = new JPanel();
		betweennessPanel.setBorder(BorderFactory
				.createTitledBorder("Betweenness"));
		JLabel betweennessLabel = new JLabel();
		betweennessLabel.setText(betweennessStr.toString());
		betweennessPanel.add(betweennessLabel);
		info.add(betweennessPanel);

		JPanel closenessPanel = new JPanel();
		closenessPanel.setBorder(BorderFactory.createTitledBorder("Closeness"));
		JLabel closenessLabel = new JLabel();
		closenessLabel.setText(closenessStr.toString());
		closenessPanel.add(closenessLabel);
		info.add(closenessPanel);

		JPanel hitsPanel = new JPanel();
		hitsPanel.setBorder(BorderFactory.createTitledBorder("HITS"));
		JLabel hitsLabel = new JLabel();
		hitsLabel.setText(hitsStr.toString());
		hitsPanel.add(hitsLabel);
		info.add(hitsPanel);

		JPanel eigenPanel = new JPanel();
		eigenPanel.setBorder(BorderFactory.createTitledBorder("Eigen Vector"));
		JLabel eigenLabel = new JLabel();
		eigenLabel.setText(eigenvectorStr.toString());
		eigenPanel.add(eigenLabel);
		info.add(eigenPanel);

		content.add(controls, BorderLayout.SOUTH);
		JScrollPane pane = new JScrollPane(info);
		content.add(pane, BorderLayout.EAST);

		quadButton.setSelected(true);
		return vv;
	}

	/**
	 * subclassed to hold two BoundedRangeModel instances that are used by
	 * JSliders to move the edge label positions
	 * 
	 * @author Tom Nelson
	 * 
	 * 
	 */
	static class MutableDirectionalEdgeValue extends
			ConstantDirectionalEdgeValueTransformer<Integer, Number> {
		BoundedRangeModel undirectedModel = new DefaultBoundedRangeModel(5, 0,
				0, 10);
		BoundedRangeModel directedModel = new DefaultBoundedRangeModel(7, 0, 0,
				10);

		public MutableDirectionalEdgeValue(double undirected, double directed) {
			super(undirected, directed);
			undirectedModel.setValue((int) (undirected * 10));
			directedModel.setValue((int) (directed * 10));

			undirectedModel.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					setUndirectedValue(new Double(
							undirectedModel.getValue() / 10f));
					vv.repaint();
				}
			});
			directedModel.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					setDirectedValue(new Double(directedModel.getValue() / 10f));
					vv.repaint();
				}
			});
		}

		/**
		 * @return Returns the directedModel.
		 */
		public BoundedRangeModel getDirectedModel() {
			return directedModel;
		}

		/**
		 * @return Returns the undirectedModel.
		 */
		public BoundedRangeModel getUndirectedModel() {
			return undirectedModel;
		}
	}

	/**
	 * create some vertices
	 * 
	 * @param count
	 *            how many to create
	 * @return the Vertices in an array
	 */
	private static void createVertices(Collection<Developer> developers) {
		int count = 0;
		for (Developer developer : developers) {
			verticesToNames.put(count, developer.developerName);
			namesToVertices.put(developer.developerName, count);
			graph.addVertex(count);
			vertexLabels.put(count, count + ":" + developer.developerName);
			count++;
		}
	}

	/**
	 * create edges for this demo graph
	 * 
	 * @param v
	 *            an array of Vertices to connect
	 */
	static void createEdges(Map<String, Map<String, Relationship>> relationships) {
		int count = 0;
		Collection<Number> numbers = graph.getEdges();
		List<Number> numberList = new ArrayList<Number>();
		for (Number n : numbers) {
			numberList.add(n);
		}
		for (Number n : numberList)
			graph.removeEdge(n);
		for (Map<String, Relationship> tmpRelationships : relationships
				.values()) {
			for (Entry<String, Relationship> relationship : tmpRelationships
					.entrySet()) {
				Relationship rel = relationship.getValue();
				if (rel.date.after(currDate))
					continue;
				int d1 = namesToVertices.get(rel.d1.developerName);
				int d2 = namesToVertices.get(rel.d2.developerName);
				graph.addEdge(count, d1, d2, EdgeType.DIRECTED);
				weights.put(count, rel.weight);
				count++;
			}
		}
		System.out.println("Found " + count + " edges!");
	}

	/*
	 * public static void main(String[] args) { JFrame frame = new JFrame();
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); Container content =
	 * frame.getContentPane(); // Plot.draw(content); frame.pack();
	 * frame.setVisible(true); }
	 */
}
